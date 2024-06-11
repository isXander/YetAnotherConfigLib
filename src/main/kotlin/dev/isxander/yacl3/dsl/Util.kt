package dev.isxander.yacl3.dsl

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class RegisterableDelegateProvider<R>(
    private val registerFunction: (id: String) -> R,
    private val id: String?,
) {
    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ExistingDelegateProvider<R> {
        return ExistingDelegateProvider(registerFunction(id ?: property.name))
    }
}

class RegisterableActionDelegateProvider<Dsl, Return>(
    private val registerFunction: (String, Dsl.() -> Unit) -> Return,
    private val action: Dsl.() -> Unit,
    private val name: String?
) {
    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ExistingDelegateProvider<Return> {
        return ExistingDelegateProvider(registerFunction(name ?: property.name, action))
    }
}

class ExistingDelegateProvider<Return>(
    private val delegate: Return
) : ReadOnlyProperty<Any?, Return> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Return {
        return delegate
    }
}
