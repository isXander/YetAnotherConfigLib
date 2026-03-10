package dev.isxander.yacl3.gui.controllers.dropdown;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.api.utils.MutableDimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerPopupWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

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
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		if (dropdownLength() == 0) return;

		// Background
        int x = dropdownDim.x();
        int y1 = dropdownDim.y();
        int textureWidth = dropdownDim.width();
        int textureHeight = dropdownDim.height();
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                Screen.MENU_BACKGROUND,
                x, y1,
                0.0f, 0.0f,
                textureWidth, textureHeight,
                32, 32,
                0xFF3F3F3F
        );
		graphics.outline(dropdownDim.x(), dropdownDim.y(), dropdownDim.width(), dropdownDim.height(), -1);

		// Highlight the currently selected element
		int y = dropdownDim.y() + 2 + entryHeight() * selectedVisibleIndex();
		graphics.fill(dropdownDim.x(), y, dropdownDim.xLimit(), y + entryHeight(), 0x7F000000);
		graphics.outline(dropdownDim.x(), y, dropdownDim.width(), entryHeight(), -1);

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
	}

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (isMouseOver(event.x(), event.y())) {
            // Closes and cleans up the dropdown
            dropdownElement.unfocus();
            return true;
        } else if (dropdownElement.isMouseOver(event.x(), event.y())) {
            return dropdownElement.mouseClicked(event, doubleClick);
        } else {
            close();
            return false;
        }
    }

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
		if (isMouseOver(mouseX, mouseY)) {
			if (vertical < 0) {
				scrollDown();
			} else {
				scrollUp();
			}
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
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
    public boolean charTyped(@NonNull CharacterEvent event) {
        // Done to allow for typing whilst the color picker is visible
        return dropdownElement.charTyped(event);
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
