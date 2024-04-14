package dev.isxander.yacl3.dsl

import dev.isxander.yacl3.api.Option
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface FutureValue<T> {
    fun onReady(block: (T) -> Unit)
    fun <R> map(block: (T) -> R): FutureValue<R>
    fun <R> flatMap(block: (T) -> FutureValue<R>): FutureValue<R>
    fun getOrNull(): T?
    fun getOrThrow(): T = getOrNull() ?: error("Value not ready yet!")

    open class Impl<T>(default: T? = null) : FutureValue<T> {
        var value: T? = default
            set(value) {
                field = value
                while (taskQueue.isNotEmpty()) {
                    taskQueue.removeFirst()(value!!)
                }
            }
        private val taskQueue = ArrayDeque<(T) -> Unit>()

        override fun onReady(block: (T) -> Unit) {
            if (value != null) block(value!!)
            else taskQueue.add(block)
        }

        override fun <R> map(block: (T) -> R): FutureValue<R> {
            val future = Impl<R>()
            onReady {
                future.value = block(it)
            }
            return future
        }

        override fun <R> flatMap(block: (T) -> FutureValue<R>): FutureValue<R> {
            val future = Impl<R>()
            onReady {
                block(it).onReady { inner ->
                    future.value = inner
                }
            }
            return future
        }

        override fun getOrNull(): T? = value
    }
}

interface Reference<T> : ReadOnlyProperty<Any?, FutureValue<T>> {
    operator fun get(id: String): FutureValue<T>

    override fun getValue(thisRef: Any?, property: KProperty<*>): FutureValue<T> {
        return get(property.name)
    }

    operator fun invoke(name: String? = null, block: (T) -> Unit): ReadOnlyProperty<Any?, FutureValue<T>> {
        return ReadOnlyProperty { thisRef, property ->
            val future = get(name ?: property.name)
            future.onReady(block)
            future
        }
    }
}


operator fun <T> FutureValue<out Reference<T>>.get(id: String): FutureValue<T> {
    val future = FutureValue.Impl<FutureValue<T>>()
    onReady {
        future.value = it[id]
    }
    return future.flatten()
}

fun FutureValue<GroupDslReference>.getOption(id: String): FutureValue<Option<*>> {
    val future = FutureValue.Impl<FutureValue<Option<*>>>()
    onReady {
        future.value = it.get<Any?>(id) as FutureValue<Option<*>>
    }
    return future.flatten()
}


private fun <T> FutureValue<FutureValue<T>>.flatten(): FutureValue<T> {
    val future = FutureValue.Impl<T>()
    onReady { outer ->
        outer.onReady { inner ->
            future.value = inner
        }
    }
    return future
}

class RegisterableDelegateProvider<Dsl, Return>(
    private val registerFunction: (String, Dsl.() -> Unit) -> Return,
    private val action: Dsl.() -> Unit
) {
    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ExistingDelegateProvider<Return> {
        return ExistingDelegateProvider(registerFunction(property.name, action))
    }
}

class ExistingDelegateProvider<Return>(
    private val delegate: Return
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Return {
        return delegate
    }
}
