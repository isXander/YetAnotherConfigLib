package dev.isxander.yacl3.dsl

import dev.isxander.yacl3.api.*
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import java.util.concurrent.CompletableFuture
import kotlin.properties.ReadOnlyProperty

interface Buildable<T> {
    val built: CompletableFuture<T>

    fun build(): T
}

fun <T> CompletableFuture<T>.onReady(block: (T) -> Unit) =
    this.whenComplete { result, _ -> result?.let(block) }

operator fun <T> CompletableFuture<out ParentRegistrar<*, *, T>>.get(id: String): CompletableFuture<T> =
    thenCompose { it[id] }

typealias FutureOption<T> = CompletableFuture<Option<T>>

fun <T> CompletableFuture<OptionRegistrar>.futureRef(id: String): FutureOption<T> =
    thenCompose { it.futureRef(id) }

fun <T> CompletableFuture<OptionRegistrar>.futureRef(): RegisterableDelegateProvider<FutureOption<T>> =
    RegisterableDelegateProvider({ this.futureRef(it) }, null)

fun YetAnotherConfigLib(id: String, block: RootDsl.() -> Unit) =
    RootDslImpl(id).apply(block).build()

interface ParentRegistrar<T, DSL, INNER> {
    fun register(id: String, registrant: T): T

    fun register(id: String, block: DSL.() -> Unit): T

    /** Registers a registrant via delegation - if id is not provided, the delegated property name is used */
    fun registering(id: String? = null, block: DSL.() -> Unit): RegisterableActionDelegateProvider<DSL, T>

    /** Creates a delegated future reference to a registrant that may or may not exist yet */
    val futureRef: ReadOnlyProperty<Any?, CompletableFuture<T>>

    /** Creates a future reference to a registrant that may or may not exist yet */
    fun futureRef(id: String): CompletableFuture<T>

    /** Gets a registrant with the id, if it exists */
    fun ref(id: String): T?

    /** Creates a delegated property that returns a registrant with a matching id, or null if it does not exist at the time of calling */
    val ref: ReadOnlyProperty<Any?, T?>

    operator fun get(id: String): CompletableFuture<INNER>
}

interface OptionRegistrar {
    /** Registers an option that has already been built. */
    fun <T, OPT : Option<T>> register(id: String, option: OPT): OPT

    /** Registers a regular option */
    fun <T> register(id: String, block: OptionDsl<T>.() -> Unit): Option<T>

    /** Registers a regular option via delegation */
    fun <T> registering(id: String? = null, block: OptionDsl<T>.() -> Unit): RegisterableActionDelegateProvider<OptionDsl<T>, Option<T>>

    fun <T> futureRef(id: String): CompletableFuture<Option<T>>
    fun <T> futureRef(): RegisterableDelegateProvider<CompletableFuture<Option<T>>>

    fun <T> ref(id: String? = null): ReadOnlyProperty<Any?, Option<T>?>


    fun registerLabel(id: String): LabelOption
    val registeringLabel: RegisterableDelegateProvider<LabelOption>

    fun registerLabel(id: String, text: Component): LabelOption

    fun registerLabel(id: String, builder: TextLineBuilderDsl.() -> Unit): LabelOption

    fun registerButton(id: String, block: ButtonOptionDsl.() -> Unit): ButtonOption
    fun registeringButton(id: String? = null, block: ButtonOptionDsl.() -> Unit): RegisterableActionDelegateProvider<ButtonOptionDsl, ButtonOption>
}

typealias CategoryRegistrar = ParentRegistrar<ConfigCategory, CategoryDsl, GroupRegistrar>
typealias GroupRegistrar = ParentRegistrar<OptionGroup, GroupDsl, OptionRegistrar>

interface RootDsl {
    val rootKey: String
    val rootId: String
    val thisRoot: CompletableFuture<YetAnotherConfigLib>

    val categories: CategoryRegistrar

    fun title(component: Component)
    fun title(block: () -> Component)

    fun screenInit(block: () -> Unit)
    fun save(block: () -> Unit)
}

interface CategoryDsl : Buildable<ConfigCategory> {
    val categoryKey: String
    val categoryId: String
    val thisCategory: CompletableFuture<ConfigCategory>

    val groups: GroupRegistrar
    val rootOptions: OptionRegistrar

    fun name(component: Component)
    fun name(block: () -> Component)

    fun tooltip(vararg component: Component)
    fun tooltip(block: TextLineBuilderDsl.() -> Unit)
}

interface GroupDsl : Buildable<OptionGroup> {
    val groupKey: String
    val groupId: String
    val thisGroup: CompletableFuture<OptionGroup>

    val options: OptionRegistrar

    fun name(component: Component)
    fun name(block: () -> Component)

    fun description(description: OptionDescription)
    fun descriptionBuilder(block: OptionDescription.Builder.() -> Unit)
    fun OptionDescription.Builder.addDefaultText(lines: Int? = null) =
        addDefaultText("$groupKey.description", lines)
}

interface OptionDsl<T> : Option.Builder<T>, Buildable<Option<T>> {
    val optionKey: String
    val optionId: String
    val thisOption: CompletableFuture<Option<T>>

    fun OptionDescription.Builder.addDefaultText(lines: Int? = null) =
        addDefaultText("$optionKey.description", lines)
}

interface ButtonOptionDsl : ButtonOption.Builder, Buildable<ButtonOption> {
    val optionKey: String
    val optionId: String
    val thisOption: CompletableFuture<ButtonOption>

    fun OptionDescription.Builder.addDefaultText(lines: Int? = null) =
        addDefaultText("$optionKey.description", lines)
}

interface TextLineBuilderDsl {
    fun text(component: Component)
    fun text(block: () -> Component)

    operator fun Component.unaryPlus()

    class Delegate(private val tooltipFunction: (Component) -> Unit) : TextLineBuilderDsl {
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

    companion object {
        fun createText(block: TextLineBuilderDsl.() -> Unit): Component {
            val text = Component.empty()
            var first = true
            val builder = Delegate {
                if (!first) text.append(CommonComponents.NEW_LINE)
                text.append(it)
                first = false
            }
            block(builder)
            return text
        }
    }
}
