package dev.isxander.yacl3.dsl.kotlin

import dev.isxander.yacl3.api.Option

interface FutureValue<T> {
    fun onReady(block: (T) -> Unit)
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

        override fun getOrNull(): T? = value
    }
}

interface Reference<T> {
    operator fun get(id: String): FutureValue<T>
}


fun <T, A : FutureValue<T>> FutureValue<A>.onReady(block: (T) -> Unit) {
    onReady { it.onReady(block) }
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
