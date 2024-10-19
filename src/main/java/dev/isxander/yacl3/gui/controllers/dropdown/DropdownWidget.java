package dev.isxander.yacl3.gui.controllers.dropdown;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.api.utils.MutableDimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerPopupWidget;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DropdownWidget<T> extends ControllerPopupWidget<AbstractDropdownController<T>> {
	public static final int MAX_SHOWN_NUMBER_OF_ITEMS = 7;
	public static final int DROPDOWN_PADDING = 2;

	private final AbstractDropdownControllerElement<T, ?> dropdownElement;

	protected Dimension<Integer> dropdownDim;

	// Scroll offset in the list of possible values
	protected int firstVisibleIndex = 0;
	// Stores the current selection position. The item at this position in the dropdown list will be chosen as the
	// accepted value when the element is closed.
	protected int selectedIndex = 0;

	public DropdownWidget(AbstractDropdownController<T> control, YACLScreen screen, Dimension<Integer> dim, AbstractDropdownControllerElement<T, ?> dropdownElement) {
		super(control, screen, dim, dropdownElement);
		this.dropdownElement = dropdownElement;

		setDimension(dim);
	}

	@Override
	public void setDimension(Dimension<Integer> dim) {
		super.setDimension(dim);

		// Set up the dropdown above the controller ...
		int dropdownHeight = dim.height() * numberOfVisibleItems();
		int dropdownY = dim.y() - dropdownHeight - DROPDOWN_PADDING;

		// ... unless it doesn't fit, then place it below
		if (dropdownY < screen.tabArea.top()) {
			dropdownY = dim.yLimit() + DROPDOWN_PADDING;
		}

		dropdownDim = Dimension.ofInt(dim.x(), dropdownY, dim.width(), dropdownHeight);
	}

	public int entryHeight() {
		return dropdownElement.getDimension().height();
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		if (dropdownLength() == 0) return;

		PoseStack matrices = graphics.pose();
		matrices.pushPose();
		matrices.translate(0, 0, 200);

		// Background
		//graphics.setColor(0.25f, 0.25f, 0.25f, 1.0f);
		GuiUtils.blitGuiTexColor(
                graphics,
				/*? if >1.20.4 {*/
				Screen.MENU_BACKGROUND,
				/*?} else {*/
				/*Screen.BACKGROUND_LOCATION,
				*//*?}*/
				dropdownDim.x(), dropdownDim.y(),
				0.0f, 0.0f,
				dropdownDim.width(), dropdownDim.height(),
				32, 32,
                0xFF3F3F3F
		);
		//graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		graphics.renderOutline(dropdownDim.x(), dropdownDim.y(), dropdownDim.width(), dropdownDim.height(), -1);

		// Highlight the currently selected element
		//graphics.setColor(0.0f, 0.0f, 0.0f, 0.5f);
		int y = dropdownDim.y() + 2 + entryHeight() * selectedVisibleIndex();
		graphics.fill(dropdownDim.x(), y, dropdownDim.xLimit(), y + entryHeight(), 0x7F000000);
		//graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		graphics.renderOutline(dropdownDim.x(), y, dropdownDim.width(), entryHeight(), -1);

		// Render all visible elements
		MutableDimension<Integer> entryDimension = Dimension.ofInt(
				dropdownDim.x() - dropdownElement.getDecorationPadding(),
				dropdownDim.y() + 2,
				dropdownDim.width(),
				entryHeight()
		);
		for (int i = firstVisibleIndex; i < lastVisibleIndex(); ++i) {
			dropdownElement.renderDropdownEntry(graphics, entryDimension, i);
			entryDimension.move(0, entryHeight());
		}

		matrices.popPose();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (isMouseOver(mouseX, mouseY)) {
			// Closes and cleans up the dropdown
			dropdownElement.unfocus();
			return true;
		} else if (dropdownElement.isMouseOver(mouseX, mouseY)) {
			return dropdownElement.mouseClicked(mouseX, mouseY, button);
		} else {
			close();
			return false;
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, /*? if >1.20.1 {*/ double scrollX, /*?}*/ double scrollY) {
		if (isMouseOver(mouseX, mouseY)) {
			if (scrollY < 0) {
				scrollDown();
			} else {
				scrollUp();
			}
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, /*? if >1.20.1 {*/ scrollX, /*?}*/ scrollY);
	}

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (isMouseOver(mouseX, mouseY)) {
            int index = (int) ((mouseY - dropdownDim.y()) / entryHeight());
            selectVisibleItem(index);
        }
    }

    @Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return dropdownDim.isPointInside((int) mouseX, (int) mouseY);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		// Done to allow for typing whilst the color picker is visible
		return dropdownElement.charTyped(chr, modifiers);
	}

	public int dropdownLength() {
		return dropdownElement.matchingValues.size();
	}

	public int numberOfVisibleItems() {
		return Math.min(MAX_SHOWN_NUMBER_OF_ITEMS, dropdownLength());
	}

	public int lastVisibleIndex() {
		return Math.min(firstVisibleIndex + MAX_SHOWN_NUMBER_OF_ITEMS, dropdownLength());
	}

	public int selectedIndex() {
		return selectedIndex;
	}
	public void resetSelectedIndex() {
		selectedIndex = 0;
	}
	/**
	 * @return the offset of the selected element relative to the first visible entry
	 */
	public int selectedVisibleIndex() {
		return selectedIndex - firstVisibleIndex;
	}

	public void selectVisibleItem(int visibleIndex) {
		selectedIndex = Math.min(firstVisibleIndex + visibleIndex, dropdownLength() - 1);
	}

	public void selectNextEntry() {
		if (selectedIndex == dropdownLength() - 1) {
			selectedIndex = 0;
		} else {
			selectedIndex++;
		}
		if (selectedIndex - firstVisibleIndex >= MAX_SHOWN_NUMBER_OF_ITEMS / 2) {
			centerOnSelectedItem();
		}
	}

	public void selectPreviousEntry() {
		if (selectedIndex == 0) {
			selectedIndex = dropdownLength() - 1;
		} else {
			selectedIndex--;
		}
		if (selectedIndex - firstVisibleIndex <= MAX_SHOWN_NUMBER_OF_ITEMS / 2) {
			centerOnSelectedItem();
		}
	}

	private void centerOnSelectedItem() {
		// Limit the visible options to allow scrolling through the suggestion list
		int begin = Math.max(0, selectedIndex - MAX_SHOWN_NUMBER_OF_ITEMS / 2);
		int end = begin + MAX_SHOWN_NUMBER_OF_ITEMS;
		if (end >= dropdownLength()) {
			end = dropdownLength();
			begin = Math.max(0, end - MAX_SHOWN_NUMBER_OF_ITEMS);
		}
		firstVisibleIndex = begin;
	}

	public void scrollDown() {
		if (firstVisibleIndex + 1 + MAX_SHOWN_NUMBER_OF_ITEMS <= dropdownLength()) {
			firstVisibleIndex++;
		}
		if (selectedIndex < firstVisibleIndex) selectedIndex = firstVisibleIndex;
	}
	public void scrollUp() {
		if (firstVisibleIndex > 0) {
			firstVisibleIndex--;
		}
		if (selectedIndex > firstVisibleIndex + MAX_SHOWN_NUMBER_OF_ITEMS - 1) {
			selectedIndex = firstVisibleIndex + MAX_SHOWN_NUMBER_OF_ITEMS - 1;
		}
	}


	@Override
	public void close() {
		dropdownElement.removeDropdownWidget();
	}

	@Override
	public Component popupTitle() {
		return Component.translatable("yacl.control.dropdown.dropdown_widget_title");
	}

}
