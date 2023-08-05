package dev.isxander.yacl3.gui.controllers.dropdown;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.utils.ItemRegistryHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemControllerElement extends AbstractDropdownControllerElement<Item, ResourceLocation> {
	private final ItemController itemController;

	public ItemControllerElement(ItemController control, YACLScreen screen, Dimension<Integer> dim) {
		super(control, screen, dim);
		this.itemController = control;
	}

	@Override
	protected void drawValueText(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		var oldDimension = getDimension();
		setDimension(getDimension().withWidth(getDimension().width() - getDecorationPadding()));
		super.drawValueText(graphics, mouseX, mouseY, delta);
		setDimension(oldDimension);
		if (ItemRegistryHelper.isRegisteredItem(inputField)) {
			graphics.renderFakeItem(new ItemStack(ItemRegistryHelper.getItemFromName(inputField)), getDimension().xLimit() - getXPadding() - getDecorationPadding() + 2, getDimension().y() + 2);
		}
	}

	@Override
	public List<ResourceLocation> getMatchingValues() {
		return ItemRegistryHelper.getMatchingItemIdentifiers(inputField).toList();
	}

	@Override
	protected void renderDropdownEntry(GuiGraphics graphics, ResourceLocation identifier, int n) {
		super.renderDropdownEntry(graphics, identifier, n);
		Item item = BuiltInRegistries.ITEM.get(identifier);
		graphics.renderFakeItem(new ItemStack(item), getDimension().xLimit() - getDecorationPadding() + 2, getDimension().y() + n * getDimension().height() + 4);
	}

	@Override
	public String getString(ResourceLocation identifier) {
		return BuiltInRegistries.ITEM.get(identifier).toString();
	}

	protected int getDecorationPadding() {
		return 20;
	}

	@Override
	protected int getControlWidth() {
		return super.getControlWidth() + getDecorationPadding();
	}

	@Override
	protected Component getValueText() {
		if (inputField.isEmpty() || itemController == null)
			return super.getValueText();

		if (inputFieldFocused)
			return Component.literal(inputField);

		return itemController.option().pendingValue().getDescription();
	}
}
