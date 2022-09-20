package dev.isxander.yacl.impl;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.OptionGroup;
import net.minecraft.text.Text;

public record ConfigCategoryImpl(Text name, ImmutableList<OptionGroup> groups, Text tooltip) implements ConfigCategory {

}
