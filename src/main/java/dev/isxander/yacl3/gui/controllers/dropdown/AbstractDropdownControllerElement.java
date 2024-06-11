package dev.isxander.yacl3.gui.controllers.dropdown;

import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.string.StringControllerElement;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractDropdownControllerElement<T, U> extends StringControllerElement {

	private final AbstractDropdownController<T> dropdownController;
	protected DropdownWidget<T> dropdownWidget;

	protected boolean dropdownVisible = false;

	// Stores a cached list of matching values
	protected List<U> matchingValues = null;

	public AbstractDropdownControllerElement(AbstractDropdownController<T> control, YACLScreen screen, Dimension<Integer> dim) {
		super(control, screen, dim, false);
		this.dropdownController = control;
		this.dropdownController.option.addListener((opt, val) -> this.matchingValues = this.computeMatchingValues());
	}

	public void ensureValidValue() {
		if (!dropdownController.isValueValid(inputField)) {
			if (dropdownWidget == null) {
				inputField = dropdownController.getValidValue(inputField);
			} else {
				inputField = dropdownController.getValidValue(inputField, dropdownWidget.selectedIndex());
				dropdownWidget.resetSelectedIndex();
			}
			caretPos = getDefaultCaretPos();
			this.matchingValues = this.computeMatchingValues();
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (super.mouseClicked(mouseX, mouseY, button)) {
			if (!dropdownVisible) {
				createDropdownWidget();
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
		if (dropdownVisible) {
			removeDropdownWidget();
		}
		super.unfocus();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!inputFieldFocused)
			return false;
		if (dropdownVisible) {
			switch (keyCode) {
				case InputConstants.KEY_DOWN -> {
					dropdownWidget.selectNextEntry();
					return true;
				}
				case InputConstants.KEY_UP -> {
					dropdownWidget.selectPreviousEntry();
					return true;
				}
				case InputConstants.KEY_TAB -> {
					if (Screen.hasShiftDown()) {
						dropdownWidget.selectPreviousEntry();
					} else {
						dropdownWidget.selectNextEntry();
					}
					return true;
				}
			}
		} else {
			if (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_NUMPADENTER) {
				createDropdownWidget();
				return true;
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (!inputFieldFocused) {
			return false;
		}
		if (!dropdownVisible) {
			createDropdownWidget();
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

	@Override
	public boolean modifyInput(Consumer<StringBuilder> builder) {
		boolean success = super.modifyInput(builder);
		if (success) {
			this.matchingValues = this.computeMatchingValues();
		}
		return success;
	}

	public abstract List<U> computeMatchingValues();

	public boolean matchingValue(String value) {
		return value.toLowerCase().contains(inputField.toLowerCase());
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		if (matchingValues == null) matchingValues = computeMatchingValues();

		super.render(graphics, mouseX, mouseY, delta);
	}

	void renderDropdownEntry(GuiGraphics graphics, Dimension<Integer> entryDimension, int index) {
		renderDropdownEntry(graphics, entryDimension, matchingValues.get(index));
	}
	protected void renderDropdownEntry(GuiGraphics graphics, Dimension<Integer> entryDimension, U value) {
		String entry = getString(value);
		Component text;
		if (entry.isBlank()) {
			text = Component.translatable("yacl.control.text.blank").withStyle(ChatFormatting.GRAY);
		} else {
			text = shortenString(entry);
		}
		graphics.drawString(
				textRenderer,
				text,
				entryDimension.xLimit() - textRenderer.width(text) - getDropdownEntryPadding(),
				getTextY(entryDimension),
				-1,
				true
		);
	}

	protected int getTextY(Dimension<Integer> dim) {
		return (int)(dim.y() + dim.height() / 2f - textRenderer.lineHeight / 2f);
	}

	@Override
	public void setDimension(Dimension<Integer> dim) {
		super.setDimension(dim);

		if (dropdownWidget != null) {
			dropdownWidget.setDimension(dropdownWidget.getDimension().withY(this.getDimension().y()));
			// checks if the popup is being partially rendered offscreen
			if (this.getDimension().y() < screen.tabArea.top() || this.getDimension().yLimit() > screen.tabArea.bottom()) {
				removeDropdownWidget();
			}
		}
	}

	public abstract String getString(U object);

	public Component shortenString(String value) {
		return Component.literal(GuiUtils.shortenString(value, textRenderer, getDimension().width() - 20, "..."));
	}

	protected int getDecorationPadding() {
		return super.getXPadding();
	}

	protected int getDropdownEntryPadding() {
		return 0;
	}

	public void createDropdownWidget() {
		dropdownVisible = true;
		dropdownWidget = new DropdownWidget<>(dropdownController, screen, getDimension(), this);
		screen.addPopupControllerWidget(dropdownWidget);
	}

	public DropdownWidget<T> dropdownWidget() {
		return dropdownWidget;
	}

	public boolean isDropdownVisible() {
		return dropdownVisible;
	}

	public void removeDropdownWidget() {
		ensureValidValue();
		screen.clearPopupControllerWidget();
		this.dropdownVisible = false;
		this.dropdownWidget = null;
	}
}
