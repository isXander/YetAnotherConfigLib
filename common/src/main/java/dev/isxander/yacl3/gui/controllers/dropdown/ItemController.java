package dev.isxander.yacl3.gui.controllers.dropdown;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.utils.ItemRegistryHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * Simple controller that simply runs the button action on press
 * and renders a {@link} Text on the right.
 */
public class ItemController extends AbstractDropdownController<Item> {

	/**
	 * Constructs an item controller
	 *
	 * @param option bound option
	 */
	public ItemController(Option<Item> option) {
		super(option);
	}

	@Override
	public String getString() {
		return BuiltInRegistries.ITEM.getKey(option.pendingValue()).toString();
	}

	@Override
	public void setFromString(String value) {
		option.requestSet(ItemRegistryHelper.getItemFromName(value, option.pendingValue()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component formatValue() {
		return Component.literal(getString());
	}


	@Override
	public boolean isValueValid(String value) {
		return ItemRegistryHelper.isRegisteredItem(value);
	}

	@Override
	protected String getValidValue(String value, int offset) {
		return ItemRegistryHelper.getMatchingItemIdentifiers(value)
				.skip(offset)
				.findFirst()
				.map(ResourceLocation::toString)
				.orElseGet(this::getString);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
		return new ItemControllerElement(this, screen, widgetDimension);
	}
}
