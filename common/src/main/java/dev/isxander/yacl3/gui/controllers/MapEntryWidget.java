package dev.isxander.yacl3.gui.controllers;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.MapOption;
import dev.isxander.yacl3.api.MapOptionEntry;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.TooltipButtonWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MapEntryWidget extends AbstractWidget implements ContainerEventHandler {
	private final TooltipButtonWidget removeButton, moveUpButton, moveDownButton;
	private final AbstractWidget entryWidget;

	private final MapOption<?, ?> mapOption;
	private final MapOptionEntry<?, ?> mapOptionEntry;

	private final String optionNameString;

	private GuiEventListener focused;
	private boolean dragging;

	@SuppressWarnings("UnnecessaryUnicodeEscape")
	public MapEntryWidget(YACLScreen screen, @NotNull MapOptionEntry<?, ?> mapOptionEntry, @NotNull AbstractWidget entryWidget) {
		super(entryWidget.getDimension().withHeight(
				Math.max(entryWidget.getDimension().height(), 20) - ((mapOptionEntry.parentGroup().indexOf(
						mapOptionEntry) == mapOptionEntry.parentGroup().options().size() - 1) ? 0 : 2))); // -2 to remove the padding
		this.mapOptionEntry = mapOptionEntry;
		this.mapOption = mapOptionEntry.parentGroup();
		this.optionNameString = mapOptionEntry.name().getString().toLowerCase();
		this.entryWidget = entryWidget;

		Dimension<Integer> dim = entryWidget.getDimension();
		entryWidget.setDimension(dim.clone().move(20 * 2, 0).expand(-20 * 3, 0));

		removeButton = new TooltipButtonWidget(screen, dim.xLimit() - 20, dim.y(), 20, 20, Component.literal("\u274c"),
				Component.translatable("yacl.list.remove"), btn -> {
			mapOption.removeEntry(mapOptionEntry);
			updateButtonStates();
		}
		);

		moveUpButton = new TooltipButtonWidget(
				screen, dim.x(), dim.y(), 20, 20, Component.literal("\u2191"), Component.translatable("yacl.list.move_up"), btn -> {
			int index = mapOption.indexOf(mapOptionEntry) - 1;
			if (index >= 0) {
				mapOption.removeEntry(mapOptionEntry);
				mapOption.insertEntry(index, mapOptionEntry);
				updateButtonStates();
			}
		});

		moveDownButton = new TooltipButtonWidget(
				screen, dim.x() + 20, dim.y(), 20, 20, Component.literal("\u2193"), Component.translatable("yacl.list.move_down"), btn -> {
			int index = mapOption.indexOf(mapOptionEntry) + 1;
			if (index < mapOption.options().size()) {
				mapOption.removeEntry(mapOptionEntry);
				mapOption.insertEntry(index, mapOptionEntry);
				updateButtonStates();
			}
		});

		updateButtonStates();
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		updateButtonStates(); // update every render in case option becomes available/unavailable

		removeButton.setY(getDimension().y());
		moveUpButton.setY(getDimension().y());
		moveDownButton.setY(getDimension().y());
		entryWidget.setDimension(entryWidget.getDimension().withY(getDimension().y()));

		removeButton.render(graphics, mouseX, mouseY, delta);
		moveUpButton.render(graphics, mouseX, mouseY, delta);
		moveDownButton.render(graphics, mouseX, mouseY, delta);
		entryWidget.render(graphics, mouseX, mouseY, delta);
	}

	protected void updateButtonStates() {
		removeButton.active = mapOption.available() && mapOption.numberOfEntries() > mapOption.minimumNumberOfEntries();
		moveUpButton.active = mapOption.indexOf(mapOptionEntry) > 0 && mapOption.available();
		moveDownButton.active = mapOption.indexOf(mapOptionEntry) < mapOption.options().size() - 1 && mapOption.available();
	}

	@Override
	public void unfocus() {
		entryWidget.unfocus();
	}

	@Override
	public void updateNarration(NarrationElementOutput builder) {
		entryWidget.updateNarration(builder);
	}

	@Override
	public boolean matchesSearch(String query) {
		return optionNameString.contains(query.toLowerCase());
	}

	@Override
	public @NotNull List<? extends GuiEventListener> children() {
		return ImmutableList.of(moveUpButton, moveDownButton, entryWidget, removeButton);
	}

	@Override
	public boolean isDragging() {
		return dragging;
	}

	@Override
	public void setDragging(boolean dragging) {
		this.dragging = dragging;
	}

	@Nullable
	@Override
	public GuiEventListener getFocused() {
		return focused;
	}

	@Override
	public void setFocused(@Nullable GuiEventListener focused) {
		this.focused = focused;
	}
}
