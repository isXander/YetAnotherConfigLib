package dev.isxander.yacl3.dsl.kotlin

import dev.isxander.yacl3.api.*
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

object Foo {
    var bar = true
    var baz = 0
}

fun test() {
    YetAnotherConfigLib("namespace") {
        // default title with translation key:
        // `yacl3.config.namespace.title`
        /* NO CODE REQUIRED */

        // or set the title
        title(Component.literal("A cool title"))


        // usual save function
        save {
            // run your save function!
        }

        // get access to an option from the very root of the dsl!
        categories["testCategory"]["testGroup"].getOption("testOption").onReady {
            // do something with it
        }

        category("testCategory") {
            // default name with translation key:
            // `yacl3.config.namespace.testCategory.testGroup.name`
            /* NO CODE REQUIRED */

            // or set the name
            name { Component.literal("A cool group") }

            // custom tooltip
            tooltipBuilder {
                // add a line like this
                +Component.translatable("somecustomkey")

                // or like this
                text(Component.translatable("somecustomkey"))

                // or like this
                text { Component.translatable("somecustomkey") }
            }

            groups["testGroup"] // you know the drill!

            group("testGroup") {
                // default name with translation key:
                // `yacl3.config.namespace.testCategory.testGroup.name`
                /* NO CODE REQUIRED */

                // or set the name
                name { Component.literal("A cool group") }


                // custom description builder:
                descriptionBuilder {
                    // blah blah blah
                }

                // default description with translation key:
                // `yacl3.config.namespace.testCategory.testGroup.description.1-5`
                // not compatible with custom description builder
                useDefaultDescription(lines = 5)

                option("testOption") { // type is automatically inferred from binding
                    // default name with translation key:
                    // `yacl3.config.namespace.testCategory.testGroup.testOption.name`
                    /* NO CODE REQUIRED */

                    // custom description builder:
                    descriptionBuilder {
                        text { Component.translatable("somecustomkey") }
                        webpImage(ResourceLocation("namespace", "image.png"))
                    }

                    // description with translation key:
                    // `yacl3.config.namespace.testCategory.testGroup.testOption.description.1-5`
                    // not compatible with custom description builder
                    useDefaultDescription(lines = 5)

                    // KProperties are cool!
                    binding(Foo::bar, Foo.bar)

                    // you can access other options like this!
                    // `options` field is from the enclosing group dsl
                    listener { opt, newVal ->
                        options.get<Int>("otherTestOption").getOrThrow().setAvailable(newVal)
                    }

                    // or even get an access to them before creation
                    options.get<Int>("otherTestOption").onReady {
                        // do something with it
                    }

                    // you can set available with a block
                    available { true }

                    // regular controller stuff
                    // this will be DSLed at some point
                    controller { BooleanControllerBuilder.create(it).apply {
                        // blah blah blah
                    } }

                    // flags as usual
                    flag(OptionFlag.ASSET_RELOAD)
                }

                option("otherTestOption") { // type is automatically inferred from binding
                    binding(Foo::baz, Foo.baz)

                    // blah blah blah other stuff
                }
            }
        }
    }
}

fun YetAnotherConfigLib(namespace: String, block: YACLDsl.() -> Unit): YetAnotherConfigLib {
    val context = YACLDslContext(namespace)
    context.block()
    return context.build()
}

class YACLDslContext(
    private val namespace: String,
    private val builder: YetAnotherConfigLib.Builder = YetAnotherConfigLib.createBuilder()
) : YACLDsl {
    private val categoryMap = LinkedHashMap<String, YACLDslCategoryContext>()
    private val categoryDslReferenceMap = mutableMapOf<String, FutureValue.Impl<CategoryDslReference>>()

    override val namespaceKey = "yacl3.config.$namespace"

    private var used = false
    private var built: YetAnotherConfigLib? = null

    override val categories = object : YACLDslReference {
        override fun get(): YetAnotherConfigLib? = built

        override operator fun get(id: String): FutureValue<CategoryDslReference> =
            FutureValue.Impl(categoryMap[id]?.groups).also { categoryDslReferenceMap[id] = it }

        override val isBuilt: Boolean
            get() = built != null
    }

    init {
        title(Component.translatable("$namespaceKey.title"))
    }

    override fun title(component: Component) {
        builder.title(component)
    }

    override fun title(block: () -> Component) {
        title(block())
    }

    override fun category(id: String, block: CategoryDsl.() -> Unit) {
        val context = YACLDslCategoryContext(id, this)
        context.block()
        categoryMap[id] = context
        categoryDslReferenceMap[id]?.value = context.groups
    }

    override fun save(block: () -> Unit) {
        builder.save(block)
    }

    fun build(): YetAnotherConfigLib {
        if (used) error("Cannot use the same DSL context twice!")
        used = true

        for ((id, category) in categoryMap) {
            builder.category(category.build())
        }

        return builder.build().also { built = it }
    }
}

class YACLDslCategoryContext(
    private val id: String,
    private val root: YACLDslContext,
    private val builder: ConfigCategory.Builder = ConfigCategory.createBuilder(),
) : CategoryDsl {
    private val groupMap = LinkedHashMap<String, YACLDslGroupContext>()
    private val groupDslReferenceMap = mutableMapOf<String, FutureValue.Impl<GroupDslReference>>()
    val categoryKey = "${root.namespaceKey}.$id"

    private var built: ConfigCategory? = null

    private val rootGroup: YACLDslGroupContext = YACLDslGroupContext(id, this, builder.rootGroupBuilder())

    override val groups = object : CategoryDslReference {
        override fun get(): ConfigCategory? = built

        override fun get(id: String): FutureValue<GroupDslReference> =
            FutureValue.Impl(groupMap[id]?.options).also { groupDslReferenceMap[id] = it }

        override val root: GroupDslReference
            get() = TODO("Not yet implemented")

        override val isBuilt: Boolean
            get() = built != null

    }

    override val options = rootGroup.options

    init {
        builder.name(Component.translatable("$categoryKey.title"))
    }

    override fun name(component: Component) {
        builder.name(component)
    }

    override fun name(block: () -> Component) {
        name(block())
    }

    override fun group(id: String, block: GroupDsl.() -> Unit) {
        val context = YACLDslGroupContext(id, this)
        context.block()
        groupMap[id] = context
        groupDslReferenceMap[id]?.value = context.options

        builder.group(context.build())
    }

    override fun <T : Any> option(id: String, block: OptionDsl<T>.() -> Unit) {
        rootGroup.option(id, block)
    }

    override fun tooltip(vararg component: Component) {
        builder.tooltip(*component)
    }

    override fun tooltipBuilder(block: TooltipBuilderDsl.() -> Unit) {
        val builder = TooltipBuilderDsl.Delegate { builder.tooltip(it) }
        builder.block()
    }

    override fun useDefaultTooltip(lines: Int) {
        if (lines == 1) {
            builder.tooltip(Component.translatable("$categoryKey.tooltip"))
        } else for (i in 1..lines) {
            builder.tooltip(Component.translatable("$categoryKey.tooltip.$i"))
        }
    }

    fun build(): ConfigCategory {
        return builder.build().also { built = it }
    }
}

class YACLDslGroupContext(
    private val id: String,
    private val category: YACLDslCategoryContext,
    private val builder: OptionGroup.Builder = OptionGroup.createBuilder()
) : GroupDsl {
    private val optionMap = LinkedHashMap<String, YACLDslOptionContext<*>>()
    private val optionDslReferenceMap = mutableMapOf<String, FutureValue.Impl<Option<*>>>()
    val groupKey = "${category.categoryKey}.$id"
    private var built: OptionGroup? = null

    override val options = object : GroupDslReference {
        override fun get(): OptionGroup? = built

        override fun <T> get(id: String): FutureValue<Option<T>> =
            FutureValue.Impl(optionMap[id]?.option as Option<T>).also { optionDslReferenceMap[id] = it as FutureValue.Impl<Option<*>> }

        override val isBuilt: Boolean
            get() = built != null

    }

    override fun name(component: Component) {
        builder.name(component)
    }

    override fun name(block: () -> Component) {
        name(block())
    }

    override fun descriptionBuilder(block: OptionDescription.Builder.() -> Unit) {
        builder.description(OptionDescription.createBuilder().apply(block).build())
    }

    override fun description(description: OptionDescription) {
        builder.description(description)
    }

    init {
        builder.name(Component.translatable("$groupKey.name"))
    }

    override fun <T : Any> option(id: String, block: OptionDsl<T>.() -> Unit) {
        val context = YACLDslOptionContext<T>(id, this)
        context.block()
        optionMap[id] = context

        builder.option(context.build().also { optionDslReferenceMap[id]?.value = it })
    }

    override fun useDefaultDescription(lines: Int) {
        descriptionBuilder {
            if (lines == 1) {
                text(Component.translatable("$groupKey.description"))
            } else for (i in 1..lines) {
                text(Component.translatable("$groupKey.description.$i"))
            }
        }
    }

    fun build(): OptionGroup {
        return builder.build().also { built = it }
    }
}

class YACLDslOptionContext<T : Any>(
    private val id: String,
    private val group: YACLDslGroupContext,
    private val builder: Option.Builder<T> = Option.createBuilder()
) : Option.Builder<T> by builder, OptionDsl<T> {
    val optionKey = "${group.groupKey}.$id"
    private var built: Option<T>? = null

    private val taskQueue = ArrayDeque<(Option<T>) -> Unit>()
    override val option = FutureValue.Impl<Option<T>>()

    init {
        name(Component.translatable("$optionKey.name"))
    }

    override fun useDefaultDescription(lines: Int) {
        descriptionBuilder {
            if (lines == 1) {
                text(Component.translatable("$optionKey.description"))
            } else for (i in 1..lines) {
                text(Component.translatable("$optionKey.description.$i"))
            }
        }
    }

    override fun build(): Option<T> {
        return builder.build().also {
            built = it
            option.value = it
            while (taskQueue.isNotEmpty()) {
                taskQueue.removeFirst()(it)
            }
        }
    }
}
