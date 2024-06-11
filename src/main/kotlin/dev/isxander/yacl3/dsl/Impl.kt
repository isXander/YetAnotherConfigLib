package dev.isxander.yacl3.dsl

import dev.isxander.yacl3.api.*
import dev.isxander.yacl3.impl.utils.YACLConstants
import net.minecraft.network.chat.Component
import org.slf4j.Logger
import java.util.concurrent.CompletableFuture
import kotlin.properties.ReadOnlyProperty

private val LOGGER: Logger = YACLConstants.LOGGER

class ParentRegistrarImpl<T, DSL : Buildable<T>, INNER>(
    private val adder: (registrant: T, id: String) -> Unit,
    private val dslFactory: (id: String) -> DSL,
    private val getter: (id: String) -> CompletableFuture<T>,
    private val innerGetter: (id: String) -> CompletableFuture<INNER>,
) : ParentRegistrar<T, DSL, INNER> {
    override fun register(id: String, registrant: T) =
        adder(registrant, id).let { registrant }

    override fun register(id: String, block: DSL.() -> Unit): T =
        register(id, dslFactory(id).apply(block).build())

    override fun registering(id: String?, block: DSL.() -> Unit) =
        RegisterableActionDelegateProvider(this::register, block, id)

    override fun futureRef(id: String): CompletableFuture<T> = getter(id)

    override val futureRef: ReadOnlyProperty<Any?, CompletableFuture<T>>
        get() = ReadOnlyProperty { _, property -> futureRef(property.name) }

    override fun ref(id: String): T? =
        futureRef(id).getNow(null)

    override val ref: ReadOnlyProperty<Any?, T?>
        get() = ReadOnlyProperty { _, property -> ref(property.name) }

    override fun get(id: String): CompletableFuture<INNER> = innerGetter(id)
}

class RootDslImpl(
    override val rootId: String
) : RootDsl, Buildable<YetAnotherConfigLib> {
    override val rootKey: String = "yacl3.config.$rootId"

    override val thisRoot = CompletableFuture<YetAnotherConfigLib>()

    override val built = thisRoot
    private val builder = YetAnotherConfigLib.createBuilder()

    private val categoryFutures = mutableMapOf<String, CompletableFuture<CategoryDsl>>()
    private fun createFuture(id: String) = categoryFutures.computeIfAbsent(id) { CompletableFuture() }

    init {
        builder.title(Component.translatable("$rootKey.title"))
    }

    override val categories: CategoryRegistrar = ParentRegistrarImpl(
        { category, _ -> builder.category(category) },
        { id -> CategoryDslImpl(id, this)
            .also { createFuture(id).complete(it) }
        },
        { id -> createFuture(id).thenCompose { it.built } },
        { id -> createFuture(id).thenApply { it.groups } },
    )

    override fun title(component: Component) {
        builder.title(component)
    }

    override fun title(block: () -> Component) = title(block())

    override fun screenInit(block: () -> Unit) {
        builder.screenInit { block() }
    }

    override fun save(block: () -> Unit) {
        builder.save { block() }
    }

    override fun build(): YetAnotherConfigLib =
        builder.build().also {
            thisRoot.complete(it)
            checkUnresolvedFutures()
        }

    private fun checkUnresolvedFutures() {
        categoryFutures.filterValues { !it.isDone }
            .forEach { LOGGER.error("Future category ${it.key} was referenced but was never built.") }
    }
}

class CategoryDslImpl(
    override val categoryId: String,
    private val parent: RootDsl,
) : CategoryDsl {
    override val categoryKey = "${parent.rootKey}.category.$categoryId"

    override val thisCategory = CompletableFuture<ConfigCategory>()

    override val built = thisCategory
    private val builder = ConfigCategory.createBuilder()

    private val groupFutures = mutableMapOf<String, CompletableFuture<GroupDsl>>()
    private fun createGroupFuture(id: String) = groupFutures.computeIfAbsent(id) { CompletableFuture() }

    private val rootOptFutures = mutableMapOf<String, CompletableFuture<Option<*>>>()
    private fun createRootOptFuture(id: String) = rootOptFutures.computeIfAbsent(id) { CompletableFuture() }

    init {
        builder.name(Component.translatable(categoryKey))
    }

    override val groups: GroupRegistrar = ParentRegistrarImpl(
        { group, _ -> builder.group(group) },
        { id -> GroupDslImpl(id, this)
            .also { createGroupFuture(id).complete(it) }
        },
        { id -> createGroupFuture(id).thenCompose { it.built } },
        { id -> createGroupFuture(id).thenApply { it.options } },
    )

    override val rootOptions: OptionRegistrar = OptionRegistrarImpl(
        { option, id -> builder.option(option).also { createRootOptFuture(id).complete(option) } },
        { id -> createRootOptFuture(id) },
        "$categoryKey.root",
    )

    override fun name(component: Component) {
        builder.name(component)
    }

    override fun name(block: () -> Component) = name(block())

    override fun tooltip(block: TextLineBuilderDsl.() -> Unit) {
        builder.tooltip(TextLineBuilderDsl.createText(block))
    }

    override fun tooltip(vararg component: Component) = tooltip {
        component.forEach { +it }
    }

    override fun build(): ConfigCategory =
        builder.build().also {
            thisCategory.complete(it)
            checkUnresolvedFutures()
        }

    private fun checkUnresolvedFutures() {
        groupFutures.filterValues { !it.isDone }
            .forEach { LOGGER.error("Future group $categoryId/${it.key} was referenced but was never built.") }
        rootOptFutures.filterValues { !it.isDone }
            .forEach { LOGGER.error("Future option $categoryId/root/${it.key} was referenced but was never built.") }
    }
}

class GroupDslImpl(
    override val groupId: String,
    private val parent: CategoryDsl,
) : GroupDsl {
    override val groupKey = "${parent.categoryKey}.group.$groupId"

    override val thisGroup = CompletableFuture<OptionGroup>()

    override val built = thisGroup
    private val builder = OptionGroup.createBuilder()

    private val optionFutures = mutableMapOf<String, CompletableFuture<Option<*>>>()
    private fun createOptionFuture(id: String) = optionFutures.computeIfAbsent(id) { CompletableFuture() }

    init {
        builder.name(Component.translatable(groupKey))
    }

    override val options: OptionRegistrar = OptionRegistrarImpl(
        { option, id -> builder.option(option).also { createOptionFuture(id).complete(option) } },
        { id -> createOptionFuture(id) },
        groupKey,
    )

    override fun name(component: Component) {
        builder.name(component)
    }

    override fun name(block: () -> Component) = name(block())

    override fun description(description: OptionDescription) {
        builder.description(description)
    }

    override fun descriptionBuilder(block: OptionDescription.Builder.() -> Unit) {
        builder.description(OptionDescription.createBuilder().apply(block).build())
    }

    override fun OptionDescription.Builder.addDefaultText(lines: Int?) {
        addDefaultText("$groupKey.description", lines)
    }

    override fun build(): OptionGroup =
        builder.build().also {
            thisGroup.complete(it)
            checkUnresolvedFutures()
        }

    private fun checkUnresolvedFutures() {
        optionFutures.filterValues { !it.isDone }
            .forEach { LOGGER.error("Future option ${parent.categoryId}/$groupId/${it.key} was referenced but was never built.") }
    }
}

class OptionRegistrarImpl(
    private val adder: (registrant: Option<*>, id: String) -> Unit,
    private val getter: (id: String) -> CompletableFuture<Option<*>>,
    private val groupKey: String,
) : OptionRegistrar {
    override fun <T, OPT : Option<T>> register(id: String, option: OPT): OPT =
        adder(option, id).let { option }

    override fun <T> register(id: String, block: OptionDsl<T>.() -> Unit): Option<T> =
        register(id, OptionDslImpl<T>(id, groupKey).apply(block).build())

    override fun <T> registering(
        id: String?,
        block: OptionDsl<T>.() -> Unit
    ) = RegisterableActionDelegateProvider(this::register, block, id)

    @Suppress("UNCHECKED_CAST")
    override fun <T> futureRef(id: String): CompletableFuture<Option<T>> =
        getter(id) as CompletableFuture<Option<T>>

    override fun <T> futureRef() =
        RegisterableDelegateProvider({ this.futureRef<T>(it) }, null)

    override fun <T> ref(id: String?) = ReadOnlyProperty<Any?, Option<T>?> { _, property ->
        futureRef<T>(id ?: property.name).getNow(null)
    }

    override fun registerLabel(id: String): LabelOption =
        register(id, LabelOption.create(Component.translatable("$groupKey.label.$id")))

    override val registeringLabel = RegisterableDelegateProvider(this::registerLabel, null)

    override fun registerLabel(id: String, text: Component): LabelOption =
        register(id, LabelOption.create(text))

    override fun registerLabel(id: String, builder: TextLineBuilderDsl.() -> Unit): LabelOption =
        registerLabel(id, TextLineBuilderDsl.createText(builder))

    override fun registerButton(id: String, block: ButtonOptionDsl.() -> Unit): ButtonOption =
        register(id, ButtonOptionDslImpl(id, groupKey).apply(block).build())

    override fun registeringButton(
        id: String?,
        block: ButtonOptionDsl.() -> Unit
    ) = RegisterableActionDelegateProvider(this::registerButton, block, id)
}

class OptionDslImpl<T>(
    override val optionId: String,
    groupKey: String,
    private val builder: Option.Builder<T> = Option.createBuilder(),
) : OptionDsl<T>, Option.Builder<T> by builder {
    override val optionKey = "$groupKey.option.$optionId"

    override val thisOption = CompletableFuture<Option<T>>()
    override val built = thisOption

    init {
        builder.name(Component.translatable(optionKey))
    }

    override fun OptionDescription.Builder.addDefaultText(lines: Int?) =
        addDefaultText(prefix = "$optionKey.description", lines = lines)

    override fun build(): Option<T> =
        builder.build().also { thisOption.complete(it) }
}

class ButtonOptionDslImpl(
    override val optionId: String,
    groupKey: String,
    private val builder: ButtonOption.Builder = ButtonOption.createBuilder(),
) : ButtonOptionDsl, ButtonOption.Builder by builder {
    override val optionKey = "$groupKey.option.$optionId"

    override val thisOption = CompletableFuture<ButtonOption>()
    override val built = thisOption

    init {
        builder.name(Component.translatable(optionKey))
    }

    override fun OptionDescription.Builder.addDefaultText(lines: Int?) =
        addDefaultText(prefix = "$optionKey.description", lines = lines)

    override fun build(): ButtonOption =
        builder.build().also { thisOption.complete(it) }
}
