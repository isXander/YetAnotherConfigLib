package dev.isxander.yacl3.dsl

import dev.isxander.yacl3.api.Binding
import dev.isxander.yacl3.api.ButtonOption
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.controller.ControllerBuilder
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import kotlin.reflect.KMutableProperty0

fun <T : Any> Option.Builder<T>.binding(property: KMutableProperty0<T>, default: T) {
    binding(default, { property.get() }, { property.set(it) })
}

var <T> Option.Builder<T>.controller: ControllerBuilderFactory<T>
    get() = throw UnsupportedOperationException()
    set(value) {
        controller(value)
    }

var <T> Option.Builder<T>.binding: Binding<T>
    get() = throw UnsupportedOperationException()
    set(value) {
        binding(value)
    }

var Option.Builder<*>.available: Boolean
    get() = throw UnsupportedOperationException()
    set(value) {
        available(value)
    }

fun <T> Option.Builder<T>.descriptionBuilderDyn(block: OptionDescription.Builder.(value: T) -> Unit) {
    description { OptionDescription.createBuilder().apply { block(it) }.build() }
}

fun Option.Builder<*>.descriptionBuilder(block: OptionDescription.Builder.() -> Unit) {
    description(OptionDescription.createBuilder().apply(block).build())
}

fun ButtonOption.Builder.descriptionBuilder(block: OptionDescription.Builder.() -> Unit) {
    description(OptionDescription.createBuilder().apply(block).build())
}

fun OptionGroup.Builder.descriptionBuilder(block: OptionDescription.Builder.() -> Unit) {
    description(OptionDescription.createBuilder().apply(block).build())
}

fun OptionDescription.Builder.addDefaultText(prefix: String, lines: Int? = null) {
    if (lines != null) {
        if (lines == 1) {
            text(Component.translatable(prefix))
        } else for (i in 1..lines) {
            text(Component.translatable("$prefix.$i"))
        }
    } else {
        // loop until we find a key that doesn't exist
        var i = 1
        while (i < 100) {
            val key = "$prefix.$i"
            if (!Language.getInstance().has(key)) {
                break
            }
            text(Component.translatable(key))

            i++
        }
    }
}

fun Option.Builder<*>.available(block: () -> Boolean) {
    available(block())
}

fun OptionDescription.Builder.text(block: () -> Component) {
    text(block())
}

fun <T, B : ControllerBuilder<T>> Option.Builder<T>.controller(builder: (Option<T>) -> B, block: B.() -> Unit = {}) {
    controller { builder(it).apply(block) }
}
