package dev.isxander.yacl3.dsl

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.controller.ControllerBuilder
import net.minecraft.network.chat.Component
import kotlin.reflect.KMutableProperty0

fun <T : Any> Option.Builder<T>.binding(property: KMutableProperty0<T>, default: T) {
    binding(default, { property.get() }, { property.set(it) })
}

fun <T : Any> Option.Builder<T>.descriptionBuilder(block: OptionDescription.Builder.(T) -> Unit) {
    description { OptionDescription.createBuilder().apply { block(it) }.build() }
}

fun Option.Builder<*>.descriptionBuilderConst(block: OptionDescription.Builder.() -> Unit) {
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

fun <T, B : ControllerBuilder<T>> Option.Builder<T>.controller(builder: (Option<T>) -> B, block: B.() -> Unit = {}) {
    controller { builder(it).apply(block) }
}
