package dev.isxander.yacl.impl;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public record OptionGroupImpl(@NotNull Text name, @NotNull Text tooltip, ImmutableList<? extends Option<?>> options, boolean collapsed, boolean isRoot) implements OptionGroup {
}
