package dev.isxander.yacl3.impl.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ItemControllerBuilder;
import dev.isxander.yacl3.gui.controllers.dropdown.ItemController;
import net.minecraft.world.item.Item;

public class ItemControllerBuilderImpl extends AbstractControllerBuilderImpl<Item> implements ItemControllerBuilder {
	public ItemControllerBuilderImpl(Option<Item> option) {
		super(option);
	}

	@Override
	public Controller<Item> build() {
		return new ItemController(option);
	}
}
