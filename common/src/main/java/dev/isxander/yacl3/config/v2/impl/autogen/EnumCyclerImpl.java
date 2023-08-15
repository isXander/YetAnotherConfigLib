package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.CyclingListControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.SimpleOptionFactory;
import dev.isxander.yacl3.config.v2.api.autogen.EnumCycler;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class EnumCyclerImpl extends SimpleOptionFactory<EnumCycler, Enum<?>> {
    @Override
    protected ControllerBuilder<Enum<?>> createController(EnumCycler annotation, ConfigField<Enum<?>> field, OptionAccess storage, Option<Enum<?>> option) {
        List<? extends Enum<?>> values;

        if (option.pendingValue() instanceof EnumCycler.CyclableEnum<?> cyclableEnum) {
            values = Arrays.asList(cyclableEnum.allowedValues());
        } else {
            Enum<?>[] constants = field.access().typeClass().getEnumConstants();
            values = IntStream.range(0, constants.length)
                    .filter(ordinal -> annotation.allowedOrdinals().length == 0 || Arrays.stream(annotation.allowedOrdinals()).noneMatch(allowed -> allowed == ordinal))
                    .mapToObj(ordinal -> constants[ordinal])
                    .toList();
        }

        // EnumController doesn't support filtering
        var builder = CyclingListControllerBuilder.create(option)
                .values(values);
        if (NameableEnum.class.isAssignableFrom(field.access().typeClass())) {
            builder.valueFormatter(v -> ((NameableEnum) v).getDisplayName());
        } else {
            builder.valueFormatter(v -> Component.translatable("yacl3.config.enum.%s.%s".formatted(field.access().typeClass().getSimpleName(), v.name().toLowerCase())));
        }
        return builder;
    }
}
