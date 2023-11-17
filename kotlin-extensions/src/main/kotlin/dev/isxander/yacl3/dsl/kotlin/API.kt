package dev.isxander.yacl3.dsl.kotlin

import dev.isxander.yacl3.api.*
import net.minecraft.network.chat.Component

interface YACLDsl {
    val namespaceKey: String

    val categories: YACLDslReference

    fun title(component: Component)
    fun title(block: () -> Component)

    fun category(id: String, block: CategoryDsl.() -> Unit)

    fun save(block: () -> Unit)
}

interface OptionAddableDsl {
    fun <T : Any> option(id: String, block: OptionDsl<T>.() -> Unit)
}

interface CategoryDsl : OptionAddableDsl {
    val groups: CategoryDslReference
    val options: GroupDslReference

    fun group(id: String, block: GroupDsl.() -> Unit)

    fun name(component: Component)
    fun name(block: () -> Component)

    fun tooltip(vararg component: Component)
    fun tooltipBuilder(block: TooltipBuilderDsl.() -> Unit)
    fun useDefaultTooltip(lines: Int = 1)
}

interface GroupDsl : OptionAddableDsl {
    val options: GroupDslReference

    fun name(component: Component)
    fun name(block: () -> Component)

    fun descriptionBuilder(block: OptionDescription.Builder.() -> Unit)
    fun description(description: OptionDescription)
    fun useDefaultDescription(lines: Int = 1)
}

interface OptionDsl<T> : Option.Builder<T> {
    val option: FutureValue<Option<T>>

    fun useDefaultDescription(lines: Int = 1)
}

interface TooltipBuilderDsl {
    fun text(component: Component)
    fun text(block: () -> Component)

    operator fun Component.unaryPlus()

    class Delegate(private val tooltipFunction: (Component) -> Unit) : TooltipBuilderDsl {
        override fun text(component: Component) {
            tooltipFunction(component)
        }

        override fun text(block: () -> Component) {
            text(block())
        }

        override fun Component.unaryPlus() {
            text(this)
        }
    }
}

interface YACLDslReference : Reference<CategoryDslReference> {
    fun get(): YetAnotherConfigLib?

    val isBuilt: Boolean
}

interface CategoryDslReference : Reference<GroupDslReference> {
    fun get(): ConfigCategory?

    val root: GroupDslReference

    val isBuilt: Boolean
}

interface GroupDslReference {
    fun get(): OptionGroup?

    operator fun <T> get(id: String): FutureValue<Option<T>>

    val isBuilt: Boolean
}
