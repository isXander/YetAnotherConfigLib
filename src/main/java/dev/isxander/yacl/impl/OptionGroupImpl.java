package dev.isxander.yacl.impl;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public record OptionGroupImpl(@Nullable Text name, ImmutableList<Option<?>> options, boolean isRoot) implements OptionGroup {
}
