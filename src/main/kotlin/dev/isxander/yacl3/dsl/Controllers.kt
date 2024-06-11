package dev.isxander.yacl3.dsl

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.controller.*
import net.minecraft.world.item.Item
import java.awt.Color

typealias ControllerBuilderFactory<T> = (Option<T>) -> ControllerBuilder<T>

fun tickBox(): ControllerBuilderFactory<Boolean> = { option ->
    TickBoxControllerBuilder.create(option)
}

fun textSwitch(formatter: ValueFormatter<Boolean>? = null): ControllerBuilderFactory<Boolean> = { option ->
    BooleanControllerBuilder.create(option).apply {
        formatter?.let { formatValue(it) }
    }
}

fun slider(range: IntRange, step: Int = 1, formatter: ValueFormatter<Int>? = null): ControllerBuilderFactory<Int> = { option ->
    IntegerSliderControllerBuilder.create(option).apply {
        range(range.first, range.last)
        step(step)
        formatter?.let { formatValue(it) }
    }
}

fun slider(range: LongRange, step: Long = 1, formatter: ValueFormatter<Long>? = null): ControllerBuilderFactory<Long> = { option ->
    LongSliderControllerBuilder.create(option).apply {
        range(range.first, range.last)
        step(step)
        formatter?.let { formatValue(it) }
    }
}

fun slider(range: ClosedRange<Float>, step: Float = 1f, formatter: ValueFormatter<Float>? = null): ControllerBuilderFactory<Float> = { option ->
    FloatSliderControllerBuilder.create(option).apply {
        range(range.start, range.endInclusive)
        step(step)
        formatter?.let { formatValue(it) }
    }
}

fun slider(range: ClosedRange<Double>, step: Double = 1.0, formatter: ValueFormatter<Double>? = null): ControllerBuilderFactory<Double> = { option ->
    DoubleSliderControllerBuilder.create(option).apply {
        range(range.start, range.endInclusive)
        step(step)
        formatter?.let { formatValue(it) }
    }
}

fun stringField(): ControllerBuilderFactory<String> = { option ->
    StringControllerBuilder.create(option)
}

fun numberField(min: Int? = null, max: Int? = null, formatter: ValueFormatter<Int>? = null): ControllerBuilderFactory<Int> = { option ->
    IntegerFieldControllerBuilder.create(option).apply {
        min?.let { min(it) }
        max?.let { max(it) }
        formatter?.let { formatValue(it) }
    }
}

fun numberField(min: Long? = null, max: Long? = null, formatter: ValueFormatter<Long>? = null): ControllerBuilderFactory<Long> = { option ->
    LongFieldControllerBuilder.create(option).apply {
        min?.let { min(it) }
        max?.let { max(it) }
        formatter?.let { formatValue(it) }
    }
}

fun numberField(min: Float? = null, max: Float? = null, formatter: ValueFormatter<Float>? = null): ControllerBuilderFactory<Float> = { option ->
    FloatFieldControllerBuilder.create(option).apply {
        min?.let { min(it) }
        max?.let { max(it) }
        formatter?.let { formatValue(it) }
    }
}

fun numberField(min: Double? = null, max: Double? = null, formatter: ValueFormatter<Double>? = null): ControllerBuilderFactory<Double> = { option ->
    DoubleFieldControllerBuilder.create(option).apply {
        min?.let { min(it) }
        max?.let { max(it) }
        formatter?.let { formatValue(it) }
    }
}

fun colorPicker(allowAlpha: Boolean = false): ControllerBuilderFactory<Color> = { option ->
    ColorControllerBuilder.create(option).apply {
        allowAlpha(allowAlpha)
    }
}

fun <T> cyclingList(values: Iterable<T>, formatter: ValueFormatter<T>? = null): ControllerBuilderFactory<T> = { option ->
    CyclingListControllerBuilder.create(option).apply {
        values(values)
        formatter?.let { formatValue(it) }
    }
}

fun <T : Enum<T>> enumSwitch(enumClass: Class<T>, formatter: ValueFormatter<T>? = null): ControllerBuilderFactory<T> = { option ->
    EnumControllerBuilder.create(option).apply {
        enumClass(enumClass)
        formatter?.let { formatValue(it) }
    }
}

inline fun <reified T : Enum<T>> enumSwitch(formatter: ValueFormatter<T>? = null): ControllerBuilderFactory<T> =
    enumSwitch(T::class.java, formatter)

fun <T : Enum<T>> enumDropdown(formatter: ValueFormatter<T>? = null): ControllerBuilderFactory<T> = { option ->
    EnumDropdownControllerBuilder.create(option).apply {
        formatter?.let { formatValue(it) }
    }
}

fun minecraftItem(): ControllerBuilderFactory<Item> = { option ->
    ItemControllerBuilder.create(option)
}
