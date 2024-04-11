package dev.isxander.yacl3.config.v2.impl.autogen;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.ItemControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.autogen.ItemField;
import dev.isxander.yacl3.config.v2.api.autogen.OptionAccess;
import dev.isxander.yacl3.config.v2.api.autogen.SimpleOptionFactory;
import net.minecraft.world.item.Item;

public class ItemFieldImpl extends SimpleOptionFactory<ItemField, Item> {
	@Override
	protected ControllerBuilder<Item> createController(ItemField annotation, ConfigField<Item> field, OptionAccess storage, Option<Item> option) {
		return ItemControllerBuilder.create(option);
	}
}
