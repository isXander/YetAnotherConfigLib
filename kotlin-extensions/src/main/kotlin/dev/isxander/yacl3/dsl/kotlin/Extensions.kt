package dev.isxander.yacl3.dsl.kotlin

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import net.minecraft.network.chat.Component
import kotlin.reflect.KMutableProperty0

fun <T : Any> Option.Builder<T>.binding(property: KMutableProperty0<T>, default: T) {
    binding(default, { property.get() }, { property.set(it) })
}

fun Option.Builder<*>.descriptionBuilder(block: OptionDescription.Builder.() -> Unit) {
    description(OptionDescription.createBuilder().apply(block).build())
}

fun Option.Builder<*>.available(block: () -> Boolean) {
    available(block())
}

fun OptionDescription.Builder.text(block: () -> Component) {
    text(block())
}

fun OptionGroup.Builder.descriptionBuilder(block: OptionDescription.Builder.() -> Unit) {
    description(OptionDescription.createBuilder().apply(block).build())
}
