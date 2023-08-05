package dev.isxander.yacl3.gui.controllers.dropdown;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.string.StringControllerElement;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class AbstractDropdownControllerElement<T, U> extends StringControllerElement {
	public static final int MAX_SHOWN_NUMBER_OF_ITEMS = 7;

	private final AbstractDropdownController<T> dropdownController;
	protected boolean dropdownVisible = false;
	// Stores the current selection position. The item at this position in the dropdown list will be chosen as the
	// accepted value when the element is closed.
	protected int selectedIndex = 0;

	public AbstractDropdownControllerElement(AbstractDropdownController<T> control, YACLScreen screen, Dimension<Integer> dim) {
		super(control, screen, dim, false);
		this.dropdownController = control;
	}

	public void showDropdown() {
		dropdownVisible = true;
		selectedIndex = 0;
	}

	public void closeDropdown() {
		dropdownVisible = false;
		ensureValidValue();
	}

	public void ensureValidValue() {
		inputField = dropdownController.getValidValue(inputField, selectedIndex);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (super.mouseClicked(mouseX, mouseY, button)) {
			if (!dropdownVisible) {
				showDropdown();
				doSelectAll();
			}
			return true;
		}
		return false;
	}

	@Override
	public void setFocused(boolean focused) {
		if (focused) {
			doSelectAll();
			super.setFocused(true);
		} else unfocus();
	}

	@Override
	public void unfocus() {
		closeDropdown();
		super.unfocus();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!inputFieldFocused)
			return false;
		if (dropdownVisible) {
			switch (keyCode) {
				case InputConstants.KEY_DOWN -> {
					selectNextEntry();
					return true;
				}
				case InputConstants.KEY_UP -> {
					selectPreviousEntry();
					return true;
				}
				case InputConstants.KEY_TAB -> {
					if (Screen.hasShiftDown()) {
						selectPreviousEntry();
					} else {
						selectNextEntry();
					}
					return true;
				}
			}
		} else {
			if (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_NUMPADENTER) {
				showDropdown();
				return true;
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (!dropdownVisible) {
			showDropdown();
		}
		return super.charTyped(chr, modifiers);
	}

	@Override
	protected int getValueColor() {
		if (inputFieldFocused) {
			if (!dropdownController.isValueValid(inputField)) {
				return 0xFFF06080;
			}
		}
		return super.getValueColor();
	}

	public void selectNextEntry() {
		if (selectedIndex == getDropdownLength() - 1) {
			selectedIndex = 0;
		} else {
			selectedIndex++;
		}
	}

	public void selectPreviousEntry() {
		if (selectedIndex == 0) {
			selectedIndex = getDropdownLength() - 1;
		} else {
			selectedIndex--;
		}
	}

	public int getDropdownLength() {
		return getMatchingValues().size();
	}

	public abstract List<U> getMatchingValues();

	public boolean matchingValue(String value) {
		return value.toLowerCase().contains(inputField.toLowerCase());
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		super.render(graphics, mouseX, mouseY, delta);

		if (inputFieldFocused && dropdownVisible) {
			PoseStack matrices = graphics.pose();
			matrices.pushPose();
			matrices.translate(0, 0, 200);
			renderDropdown(graphics);
			matrices.popPose();
		}
	}

	public void renderDropdown(GuiGraphics graphics) {
		List<U> options = getMatchingValues();
		if (options.size() == 0) return;
		// Limit the visible options to allow scrolling through the suggestion list
		int begin = Math.max(0, selectedIndex - MAX_SHOWN_NUMBER_OF_ITEMS / 2);
		int end = begin + MAX_SHOWN_NUMBER_OF_ITEMS;
		if (end >= options.size()) {
			end = options.size();
			begin = Math.max(0, end - MAX_SHOWN_NUMBER_OF_ITEMS);
		}

		renderDropdownBackground(graphics, end - begin);
		if (options.size() >= 1) {
			// Highlight the currently selected element
			graphics.renderOutline(
					getDimension().x(),
					getDimension().yLimit() + 2 + getDimension().height() * (selectedIndex - begin),
					getDimension().width(),
					getDimension().height(),
					-1);
		}

		int n = 1;
		for (int i = begin; i < end; ++i) {
			renderDropdownEntry(graphics, options.get(i), n);
			++n;
		}
	}

	protected void renderDropdownEntry(GuiGraphics graphics, U value, int n) {
		Component text = shortenString(getString(value));
		graphics.drawString(textRenderer, text, getDimension().xLimit() - textRenderer.width(text) - getDecorationPadding(), getTextY() + n * getDimension().height() + 2, -1, true);
	}

	public abstract String getString(U object);

	public Component shortenString(String value) {
		return Component.literal(GuiUtils.shortenString(value, textRenderer, getDimension().width() - 20, "..."));
	}

	public void renderDropdownBackground(GuiGraphics graphics, int numberOfItems) {
		graphics.setColor(0.25f, 0.25f, 0.25f, 1.0f);
		graphics.blit(Screen.BACKGROUND_LOCATION, getDimension().x(), getDimension().yLimit() + 2, 0, 0.0f, 0.0f, getDimension().width(), getDimension().height() * numberOfItems + 2, 32, 32);
		graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		graphics.renderOutline(getDimension().x(), getDimension().yLimit() + 2, getDimension().width(), getDimension().height() * numberOfItems, -1);
	}

	protected int getDecorationPadding() {
		return super.getXPadding();
	}

}
