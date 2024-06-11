package dev.isxander.yacl3.config.v3

import com.mojang.serialization.Codec
import dev.isxander.yacl3.dsl.OptionDsl
import dev.isxander.yacl3.dsl.OptionRegistrar
import org.jetbrains.annotations.ApiStatus
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@ApiStatus.Experimental
fun <T> EntryAddable.register(
    default: T,
    codec: Codec<T>
) = PropertyDelegateProvider<EntryAddable, ReadOnlyProperty<EntryAddable, ConfigEntry<T>>> { thisRef, property ->
    val entry = thisRef.register(property.name, default, codec)
    ReadOnlyProperty { _, _ -> entry }
}

fun <T : CodecConfig<T>> EntryAddable.register(
    fieldName: String? = null,
    configInstance: T
) = PropertyDelegateProvider<EntryAddable, T> { thisRef, property ->
    thisRef.register(fieldName ?: property.name, configInstance)
    configInstance
}

operator fun <T : CodecConfig<T>> T.getValue(thisRef: CodecConfig<*>?, property: KProperty<*>): T {
    return this
}

@get:ApiStatus.Experimental
@set:ApiStatus.Experimental
var <T> ConfigEntry<T>.value: T
    get() = this.get()
    set(value) = this.set(value)

@get:ApiStatus.Experimental
val <T> ConfigEntry<T>.default: T
    get() = this.defaultValue()

@get:ApiStatus.Experimental
val ConfigEntry<*>.fieldName: String
    get() = this.fieldName()

@ApiStatus.Experimental
fun <T : Any> OptionRegistrar.register(
    configEntry: ConfigEntry<T>,
    block: OptionDsl<T>.() -> Unit
) = register<T>(configEntry.fieldName) {
    binding(configEntry.asBinding())
    block()
}
