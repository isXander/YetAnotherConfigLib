package dev.isxander.yacl3.gui.controllers.dropdown;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.utils.ItemRegistryHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemControllerElement extends AbstractDropdownControllerElement<Item, Identifier> {
	private final ItemController itemController;
	protected Item currentItem = null;
	protected Map<Identifier, Item> matchingItems = new HashMap<>();


	public ItemControllerElement(ItemController control, YACLScreen screen, Dimension<Integer> dim) {
		super(control, screen, dim);
		this.itemController = control;
	}

	@Override
	protected void extractValueText(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		var oldDimension = getDimension();
		setDimension(getDimension().withWidth(getDimension().width() - getDecorationPadding()));
		super.extractValueText(graphics, mouseX, mouseY, a);
		setDimension(oldDimension);
		if (currentItem != null) {
            extractFakeItem(
                    graphics,
                    currentItem,
                    getDimension().xLimit() - getXPadding() - getDecorationPadding() + 2,
                    getDimension().y() + 2
            );
		}
	}

	@Override
	public List<Identifier> computeMatchingValues() {
		List<Identifier> identifiers = ItemRegistryHelper.getMatchingItemIdentifiers(inputField).toList();
		currentItem = ItemRegistryHelper.getItemFromName(inputField, null);
		for (Identifier identifier : identifiers) {
			matchingItems.put(identifier, ItemRegistryHelper.getFromRegistry(BuiltInRegistries.ITEM, identifier));
		}
		return identifiers;
	}

	@Override
	protected void extractDropdownEntry(GuiGraphicsExtractor graphics, Dimension<Integer> entryDimension, Identifier identifier) {
		super.extractDropdownEntry(graphics, entryDimension, identifier);
        extractFakeItem(
                graphics,
                matchingItems.get(identifier),
                entryDimension.xLimit() - 2,
                entryDimension.y() + 1
        );
	}

    private void extractFakeItem(GuiGraphicsExtractor graphics, Item item, int x, int y) {
        ItemStack stack = null;
        try {
            stack = new ItemStack(item);
        } catch (NullPointerException ignored) {
            // ItemStacks no longer exist until dynamic registries have been loaded,
            // which is either loading into a level or opening the create world screen.
            // This means we cannot do anything that involves ItemStacks until then.
        }
        if (stack == null) return;
        graphics.fakeItem(stack, x, y);
    }

	@Override
	public String getString(Identifier identifier) {
		return identifier.toString();
	}

	@Override
	protected int getDecorationPadding() {
		return 16;
	}

	@Override
	protected int getDropdownEntryPadding() {
		return 4;
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

        Item item = itemController.option().pendingValue();
        ItemStack stack = null;
        try {
            stack = item.getDefaultInstance();
        } catch (NullPointerException ignored) {
            // fapi has a bug (i think) that doesn't bind item components early enough anymore,
            // causing an NPE on <init> of ItemStack.
        }
        if (stack != null) {
            return item.getName(stack);
        } else {
            return Component.literal(item.toString());
        }
	}
}
