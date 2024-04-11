package dev.isxander.yacl3.api.controller;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.ItemControllerBuilderImpl;
import net.minecraft.world.item.Item;

public interface ItemControllerBuilder extends ControllerBuilder<Item> {
	static ItemControllerBuilder create(Option<Item> option) {
		return new ItemControllerBuilderImpl(option);
	}
}
