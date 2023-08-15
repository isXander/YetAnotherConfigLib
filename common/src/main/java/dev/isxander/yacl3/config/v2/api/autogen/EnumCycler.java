package dev.isxander.yacl3.config.v2.api.autogen;

import dev.isxander.yacl3.api.NameableEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An option factory.
 * <p>
 * This creates a regular option with a {@link dev.isxander.yacl3.api.controller.CyclingListControllerBuilder}
 * controller. If the enum implements {@link CyclableEnum}, the allowed values will be used from that,
 * rather than every single enum constant in the class. If not, {@link EnumCycler#allowedOrdinals()} is used.
 * <p>
 * There are two methods of formatting for enum values. First, if the enum implements
 * {@link dev.isxander.yacl3.api.NameableEnum}, {@link NameableEnum#getDisplayName()} is used.
 * Otherwise, the translation key {@code yacl3.config.enum.$enumClassName.$enumName} where
 * {@code $enumClassName} is the exact name of the class and {@code $enumName} is equal to the lower
 * case of {@link Enum#name()}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EnumCycler {
    /**
     * The allowed ordinals of the enum class. If empty, all ordinals are allowed.
     * This is only used if the enum does not implement {@link CyclableEnum}.
     */
    int[] allowedOrdinals() default {};

    interface CyclableEnum<T extends Enum<T>> {
        T[] allowedValues();
    }
}
