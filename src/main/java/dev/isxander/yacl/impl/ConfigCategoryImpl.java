package dev.isxander.yacl.impl;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.OptionGroup;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public record ConfigCategoryImpl(Text name, ImmutableList<OptionGroup> groups) implements ConfigCategory {

}
