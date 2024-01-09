package dev.isxander.yacl3.gui;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class OptionListWidget extends ElementListWidgetExt<OptionListWidget.Entry> {
    private final YACLScreen yaclScreen;
    private final ConfigCategory category;
    private ImmutableList<Entry> viewableChildren;
    private String searchQuery = "";
    private final Consumer<DescriptionWithName> hoverEvent;
    private DescriptionWithName lastHoveredOption;

    public OptionListWidget(YACLScreen screen, ConfigCategory category, Minecraft client, int x, int y, int width, int height, Consumer<DescriptionWithName> hoverEvent) {
        super(client, width, height, x, y, true);
        this.yaclScreen = screen;
        this.category = category;
        this.hoverEvent = hoverEvent;

        refreshOptions();

        for (OptionGroup group : category.groups()) {
            if (group instanceof ListOption<?> listOption) {
                listOption.addRefreshListener(() -> refreshListEntries(listOption, category));
            }
        }
    }

    public void refreshOptions() {
        clearEntries();

        for (OptionGroup group : category.groups()) {
            GroupSeparatorEntry groupSeparatorEntry;
            if (!group.isRoot()) {
                groupSeparatorEntry = group instanceof ListOption<?> listOption
                        ? new ListGroupSeparatorEntry(listOption, yaclScreen)
                        : new GroupSeparatorEntry(group, yaclScreen);
                addEntry(groupSeparatorEntry);
            } else {
                groupSeparatorEntry = null;
            }

            List<Entry> optionEntries = new ArrayList<>();

            // add empty entry to make sure users know it's empty not just bugging out
            if (groupSeparatorEntry instanceof ListGroupSeparatorEntry listGroupSeparatorEntry) {
                if (listGroupSeparatorEntry.listOption.options().isEmpty()) {
                    EmptyListLabel emptyListLabel = new EmptyListLabel(listGroupSeparatorEntry, category);
                    addEntry(emptyListLabel);
                    optionEntries.add(emptyListLabel);
                }
            }

            for (Option<?> option : group.options()) {
                OptionEntry entry = new OptionEntry(option, category, group, groupSeparatorEntry, option.controller().provideWidget(yaclScreen, getDefaultEntryDimension()));
                addEntry(entry);
                optionEntries.add(entry);
            }

            if (groupSeparatorEntry != null) {
                groupSeparatorEntry.setChildEntries(optionEntries);
            }
        }

        recacheViewableChildren();
        setScrollAmount(0);
        resetSmoothScrolling();
    }

    private void refreshListEntries(ListOption<?> listOption, ConfigCategory category) {
        // find group separator for group
        ListGroupSeparatorEntry groupSeparator = super.children().stream().filter(e -> e instanceof ListGroupSeparatorEntry gs && gs.group == listOption).map(ListGroupSeparatorEntry.class::cast).findAny().orElse(null);

        if (groupSeparator == null) {
            YACLConstants.LOGGER.warn("Can't find group seperator to refresh list option entries for list option " + listOption.name());
            return;
        }

        for (Entry entry : groupSeparator.childEntries)
            super.removeEntry(entry);
        groupSeparator.childEntries.clear();

        // if no entries, below loop won't run where addEntryBelow() recaches viewable children
        if (listOption.options().isEmpty()) {
            EmptyListLabel emptyListLabel;
            addEntryBelow(groupSeparator, emptyListLabel = new EmptyListLabel(groupSeparator, category));
            groupSeparator.childEntries.add(emptyListLabel);
            return;
        }

        Entry lastEntry = groupSeparator;
        for (ListOptionEntry<?> listOptionEntry : listOption.options()) {
            OptionEntry optionEntry = new OptionEntry(listOptionEntry, category, listOption, groupSeparator, listOptionEntry.controller().provideWidget(yaclScreen, getDefaultEntryDimension()));
            addEntryBelow(lastEntry, optionEntry);
            groupSeparator.childEntries.add(optionEntry);
            lastEntry = optionEntry;
        }
    }

    public Dimension<Integer> getDefaultEntryDimension() {
        return Dimension.ofInt(getRowLeft(), 0, getRowWidth(), 20);
    }

    public void expandAllGroups() {
        for (Entry entry : super.children()) {
            if (entry instanceof GroupSeparatorEntry groupSeparatorEntry) {
                groupSeparatorEntry.setExpanded(true);
            }
        }
    }

    public void updateSearchQuery(String query) {
        this.searchQuery = query;
        expandAllGroups();
        recacheViewableChildren();
    }

    @Override
    public int getRowWidth() {
        return Math.min(396, (int)(width / 1.3f));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Entry child : children()) {
            if (child != getEntryAtPosition(mouseX, mouseY) && child instanceof OptionEntry optionEntry)
                optionEntry.widget.unfocus();
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        super.mouseScrolled(mouseX, mouseY, horizontal, vertical);

        for (Entry child : children()) {
            if (child.mouseScrolled(mouseX, mouseY, horizontal, vertical))
                break;
        }

        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Entry child : children()) {
            if (child.keyPressed(keyCode, scanCode, modifiers))
                return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (Entry child : children()) {
            if (child.charTyped(chr, modifiers))
                return true;
        }

        return super.charTyped(chr, modifiers);
    }

    @Override
    protected int getScrollbarPosition() {
        return getX() + getWidth() - (int)(width * 0.05f);
    }

    public void recacheViewableChildren() {
        this.viewableChildren = ImmutableList.copyOf(super.children().stream().filter(Entry::isViewable).toList());

        // update y positions before they need to be rendered are rendered
        int i = 0;
        for (Entry entry : viewableChildren) {
            if (entry instanceof OptionEntry optionEntry)
                optionEntry.widget.setDimension(optionEntry.widget.getDimension().withY(getRowTop(i)));
            i++;
        }
    }

    @Override
    public List<Entry> children() {
        return viewableChildren;
    }

    public void addEntry(int index, Entry entry) {
        super.children().add(index, entry);
        recacheViewableChildren();
    }

    public void addEntryBelow(Entry below, Entry entry) {
        int idx = super.children().indexOf(below) + 1;

        if (idx == 0)
            throw new IllegalStateException("The entry to insert below does not exist!");

        addEntry(idx, entry);
    }

    public void addEntryBelowWithoutScroll(Entry below, Entry entry) {
        double d = (double)this.getMaxScroll() - this.getScrollAmount();
        addEntryBelow(below, entry);
        setScrollAmount(getMaxScroll() - d);
    }

    @Override
    public boolean removeEntryFromTop(Entry entry) {
        boolean ret = super.removeEntryFromTop(entry);
        recacheViewableChildren();
        return ret;
    }

    @Override
    public boolean removeEntry(Entry entry) {
        boolean ret = super.removeEntry(entry);
        recacheViewableChildren();
        return ret;
    }

    private void setHoverDescription(DescriptionWithName description) {
        if (description != lastHoveredOption) {
            lastHoveredOption = description;
            hoverEvent.accept(description);
        }
    }

    public abstract class Entry extends ElementListWidgetExt.Entry<Entry> {
        public boolean isViewable() {
            return true;
        }

        protected boolean isHovered() {
            return Objects.equals(getHovered(), this);
        }
    }

    public class OptionEntry extends Entry {
        public final Option<?> option;
        public final ConfigCategory category;
        public final OptionGroup group;

        public final @Nullable GroupSeparatorEntry groupSeparatorEntry;

        public final AbstractWidget widget;

        private final TextScaledButtonWidget resetButton;

        private final String categoryName;
        private final String groupName;

        public OptionEntry(Option<?> option, ConfigCategory category, OptionGroup group, @Nullable GroupSeparatorEntry groupSeparatorEntry, AbstractWidget widget) {
            this.option = option;
            this.category = category;
            this.group = group;
            this.groupSeparatorEntry = groupSeparatorEntry;
            this.widget = widget;
            this.categoryName = category.name().getString().toLowerCase();
            this.groupName = group.name().getString().toLowerCase();
            if (option.canResetToDefault() && this.widget.canReset()) {
                this.widget.setDimension(this.widget.getDimension().expanded(-20, 0));
                this.resetButton = new TextScaledButtonWidget(yaclScreen, widget.getDimension().xLimit(), -50, 20, 20, 2f, Component.literal("\u21BB"), button -> {
                    option.requestSetDefault();
                });
                option.addListener((opt, val) -> this.resetButton.active = !opt.isPendingValueDefault() && opt.available());
                this.resetButton.active = !option.isPendingValueDefault() && option.available();
            } else {
                this.resetButton = null;
            }
        }

        @Override
        public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            widget.setDimension(widget.getDimension().withY(y));

            widget.render(graphics, mouseX, mouseY, tickDelta);

            if (resetButton != null) {
                resetButton.setY(y);
                resetButton.render(graphics, mouseX, mouseY, tickDelta);
            }

            if (isHovered()) {
                setHoverDescription(DescriptionWithName.of(option.name(), option.description()));
            }
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
            return widget.mouseScrolled(mouseX, mouseY, horizontal, vertical);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return widget.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            return widget.charTyped(chr, modifiers);
        }

        @Override
        public boolean isViewable() {
            return (groupSeparatorEntry == null || groupSeparatorEntry.isExpanded())
                    && (searchQuery.isEmpty()
                    || groupName.contains(searchQuery)
                    || widget.matchesSearch(searchQuery));
        }

        @Override
        public int getItemHeight() {
            return Math.max(widget.getDimension().height(), resetButton != null ? resetButton.getHeight() : 0) + 2;
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            if (focused)
                setHoverDescription(DescriptionWithName.of(option.name(), option.description()));
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            if (resetButton == null)
                return ImmutableList.of(widget);

            return ImmutableList.of(widget, resetButton);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            if (resetButton == null)
                return ImmutableList.of(widget);

            return ImmutableList.of(widget, resetButton);
        }
    }

    public class GroupSeparatorEntry extends Entry {
        protected final OptionGroup group;
        protected final MultiLineLabel wrappedName;
        protected final MultiLineLabel wrappedTooltip;

        protected final LowProfileButtonWidget expandMinimizeButton;

        protected final Screen screen;
        protected final Font font = Minecraft.getInstance().font;

        protected boolean groupExpanded;

        protected List<Entry> childEntries = new ArrayList<>();

        private int y;

        private GroupSeparatorEntry(OptionGroup group, Screen screen) {
            this.group = group;
            this.screen = screen;
            this.wrappedName = MultiLineLabel.create(font, group.name(), getRowWidth() - 45);
            this.wrappedTooltip = MultiLineLabel.create(font, group.tooltip(), screen.width / 3 * 2 - 10);
            this.groupExpanded = !group.collapsed();
            this.expandMinimizeButton = new LowProfileButtonWidget(0, 0, 20, 20, Component.empty(), btn -> onExpandButtonPress());
            updateExpandMinimizeText();
        }

        @Override
        public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.y = y;

            int buttonY = y + entryHeight / 2 - expandMinimizeButton.getHeight() / 2 + 1;

            expandMinimizeButton.setY(buttonY);
            expandMinimizeButton.setX(x);
            expandMinimizeButton.render(graphics, mouseX, mouseY, tickDelta);

            wrappedName.renderCentered(graphics, x + entryWidth / 2, y + getYPadding());

            if (isHovered()) {
                setHoverDescription(DescriptionWithName.of(group.name(), group.description()));
            }
        }

        public boolean isExpanded() {
            return groupExpanded;
        }

        public void setExpanded(boolean expanded) {
            if (this.groupExpanded == expanded)
                return;

            this.groupExpanded = expanded;
            updateExpandMinimizeText();
            recacheViewableChildren();
        }

        protected void onExpandButtonPress() {
            setExpanded(!isExpanded());
        }

        protected void updateExpandMinimizeText() {
            expandMinimizeButton.setMessage(Component.literal(isExpanded() ? "▼" : "▶"));
        }

        public void setChildEntries(List<? extends Entry> childEntries) {
            this.childEntries.clear();
            this.childEntries.addAll(childEntries);
        }

        @Override
        public boolean isViewable() {
            return searchQuery.isEmpty() || childEntries.stream().anyMatch(Entry::isViewable);
        }

        @Override
        public int getItemHeight() {
            return Math.max(wrappedName.getLineCount(), 1) * font.lineHeight + getYPadding() * 2;
        }

        private int getYPadding() {
            return 6;
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            if (focused)
                setHoverDescription(DescriptionWithName.of(group.name(), group.description()));
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(new NarratableEntry() {
                @Override
                public NarrationPriority narrationPriority() {
                    return NarrationPriority.HOVERED;
                }

                @Override
                public void updateNarration(NarrationElementOutput builder) {
                    builder.add(NarratedElementType.TITLE, group.name());
                    builder.add(NarratedElementType.HINT, group.tooltip());
                }
            });
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(expandMinimizeButton);
        }
    }

    public class ListGroupSeparatorEntry extends GroupSeparatorEntry {
        private final ListOption<?> listOption;
        private final TextScaledButtonWidget resetListButton;
        private final TooltipButtonWidget addListButton;

        private ListGroupSeparatorEntry(ListOption<?> group, Screen screen) {
            super(group, screen);
            this.listOption = group;

            this.resetListButton = new TextScaledButtonWidget(screen, getRowRight() - 20, -50, 20, 20, 2f, Component.literal("\u21BB"), button -> {
                group.requestSetDefault();
            });
            group.addListener((opt, val) -> this.resetListButton.active = !opt.isPendingValueDefault() && opt.available());
            this.resetListButton.active = !group.isPendingValueDefault() && group.available();


            this.addListButton = new TooltipButtonWidget(yaclScreen, resetListButton.getX() - 20, -50, 20, 20, Component.literal("+"), Component.translatable("yacl.list.add_top"), btn -> {
                group.insertNewEntry();
                setExpanded(true);
            });

            updateExpandMinimizeText();
            minimizeIfUnavailable();
        }

        @Override
        public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            updateExpandMinimizeText(); // update every render because option could become available/unavailable at any time

            super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);

            int buttonY = expandMinimizeButton.getY();

            resetListButton.setY(buttonY);
            addListButton.setY(buttonY);

            resetListButton.render(graphics, mouseX, mouseY, tickDelta);
            addListButton.render(graphics, mouseX, mouseY, tickDelta);
        }

        private void minimizeIfUnavailable() {
            if (!listOption.available() && isExpanded()) {
                setExpanded(false);
            }
        }

        @Override
        protected void updateExpandMinimizeText() {
            super.updateExpandMinimizeText();
            expandMinimizeButton.active = listOption == null || listOption.available();
            if (addListButton != null)
                addListButton.active = expandMinimizeButton.active && listOption.numberOfEntries() < listOption.maximumNumberOfEntries();
        }

        @Override
        public void setExpanded(boolean expanded) {
            super.setExpanded(listOption.available() && expanded);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(expandMinimizeButton, addListButton, resetListButton);
        }
    }

    public class EmptyListLabel extends Entry {
        private final ListGroupSeparatorEntry parent;
        private final String groupName;
        private final String categoryName;

        public EmptyListLabel(ListGroupSeparatorEntry parent, ConfigCategory category) {
            this.parent = parent;
            this.groupName = parent.group.name().getString().toLowerCase();
            this.categoryName = category.name().getString().toLowerCase();
        }

        @Override
        public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            graphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("yacl.list.empty").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC), x + entryWidth / 2, y, -1);
        }

        @Override
        public boolean isViewable() {
            return parent.isExpanded() && (searchQuery.isEmpty() || groupName.contains(searchQuery));
        }

        @Override
        public int getItemHeight() {
            return 11;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of();
        }
    }
}
