package dev.isxander.yacl.gui;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.api.*;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.controllers.ListEntryWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class OptionListWidget extends ElementListWidgetExt<OptionListWidget.Entry> {
    private final YACLScreen yaclScreen;
    private boolean singleCategory = false;

    private ImmutableList<Entry> viewableChildren;

    public OptionListWidget(YACLScreen screen, MinecraftClient client, int width, int height) {
        super(client, width / 3, 0, width / 3 * 2, height, true);
        this.yaclScreen = screen;

        refreshOptions();

        for (ConfigCategory category : screen.config.categories()) {
            for (OptionGroup group : category.groups()) {
                if (group instanceof ListOption<?> listOption) {
                    listOption.addRefreshListener((opt, val) -> refreshListEntries(listOption, category));
                }
            }
        }
    }

    public void refreshOptions() {
        clearEntries();

        List<ConfigCategory> categories = new ArrayList<>();
        if (yaclScreen.getCurrentCategoryIdx() == -1) {
            // -1 = no category, search in progress, so use all categories for search
            categories.addAll(yaclScreen.config.categories());
        } else {
            categories.add(yaclScreen.config.categories().get(yaclScreen.getCurrentCategoryIdx()));
        }
        singleCategory = categories.size() == 1;

        for (ConfigCategory category : categories) {
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

                List<OptionEntry> optionEntries = new ArrayList<>();
                for (Option<?> option : group.options()) {
                    OptionEntry entry = new OptionEntry(option, category, group, groupSeparatorEntry, option.controller().provideWidget(yaclScreen, getDefaultEntryPosition()));
                    addEntry(entry);
                    optionEntries.add(entry);
                }

                if (groupSeparatorEntry != null) {
                    groupSeparatorEntry.setOptionEntries(optionEntries);
                }
            }
        }

        recacheViewableChildren();
        setScrollAmount(0);
        resetSmoothScrolling();
    }

    private void refreshListEntries(ListOption<?> listOption, ConfigCategory category) {
        // find group separator for group
        GroupSeparatorEntry groupSeparator = super.children().stream().filter(e -> e instanceof GroupSeparatorEntry gs && gs.group == listOption).map(GroupSeparatorEntry.class::cast).findAny().orElse(null);

        if (groupSeparator == null)
            return;

        for (OptionEntry entry : groupSeparator.optionEntries)
            super.removeEntry(entry);

        groupSeparator.optionEntries.clear();
        Entry lastEntry = groupSeparator;
        for (ListOptionEntry<?> listOptionEntry : listOption.options()) {
            OptionEntry optionEntry = new OptionEntry(listOptionEntry, category, listOption, groupSeparator, listOptionEntry.controller().provideWidget(yaclScreen, getDefaultEntryPosition()));
            addEntryBelow(lastEntry, optionEntry);
            groupSeparator.optionEntries.add(optionEntry);
            lastEntry = optionEntry;
        }
    }

    public Dimension<Integer> getDefaultEntryPosition() {
        return Dimension.ofInt(getRowLeft(), 0, getRowWidth(), 20);
    }

    public void expandAllGroups() {
        for (Entry entry : super.children()) {
            if (entry instanceof GroupSeparatorEntry groupSeparatorEntry) {
                groupSeparatorEntry.setExpanded(true);
            }
        }
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
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        super.mouseScrolled(mouseX, mouseY, amount);

        for (Entry child : children()) {
            if (child.mouseScrolled(mouseX, mouseY, amount))
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
    protected int getScrollbarPositionX() {
        return right - (int)(width * 0.05f);
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
    public boolean removeEntryWithoutScrolling(Entry entry) {
        boolean ret = super.removeEntryWithoutScrolling(entry);
        recacheViewableChildren();
        return ret;
    }

    @Override
    public boolean removeEntry(Entry entry) {
        boolean ret = super.removeEntry(entry);
        recacheViewableChildren();
        return ret;
    }

    public abstract class Entry extends ElementListWidgetExt.Entry<Entry> {
        public boolean isViewable() {
            return true;
        }

        protected boolean isHovered() {
            return Objects.equals(getHoveredEntry(), this);
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
            if (option instanceof ListOptionEntry<?> listOptionEntry)
                this.widget = new ListEntryWidget(yaclScreen, listOptionEntry, widget);
            else this.widget = widget;
            this.categoryName = category.name().getString().toLowerCase();
            this.groupName = group.name().getString().toLowerCase();
            if (option.canResetToDefault() && this.widget.canReset()) {
                this.widget.setDimension(this.widget.getDimension().expanded(-21, 0));
                this.resetButton = new TextScaledButtonWidget(widget.getDimension().xLimit() + 1, -50, 20, 20, 2f, Text.of("\u21BB"), button -> {
                    option.requestSetDefault();
                });
                option.addListener((opt, val) -> this.resetButton.active = !opt.isPendingValueDefault() && opt.available());
                this.resetButton.active = !option.isPendingValueDefault() && option.available();
            } else {
                this.resetButton = null;
            }
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            widget.setDimension(widget.getDimension().withY(y));

            widget.render(matrices, mouseX, mouseY, tickDelta);

            if (resetButton != null) {
                resetButton.setY(y);
                resetButton.render(matrices, mouseX, mouseY, tickDelta);
            }
        }

        @Override
        public void postRender(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            widget.postRender(matrices, mouseX, mouseY, delta);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
            return widget.mouseScrolled(mouseX, mouseY, amount);
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
            String query = yaclScreen.searchFieldWidget.getText();
            return (groupSeparatorEntry == null || groupSeparatorEntry.isExpanded())
                    && (yaclScreen.searchFieldWidget.isEmpty()
                    || (!singleCategory && categoryName.contains(query))
                    || groupName.contains(query)
                    || widget.matchesSearch(query));
        }

        @Override
        public int getItemHeight() {
            return widget.getDimension().height() + 2;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            if (resetButton == null)
                return ImmutableList.of(widget);

            return ImmutableList.of(widget, resetButton);
        }

        @Override
        public List<? extends Element> children() {
            if (resetButton == null)
                return ImmutableList.of(widget);

            return ImmutableList.of(widget, resetButton);
        }
    }

    public class GroupSeparatorEntry extends Entry {
        protected final OptionGroup group;
        protected final MultilineText wrappedName;
        protected final MultilineText wrappedTooltip;

        protected final LowProfileButtonWidget expandMinimizeButton;

        protected final Screen screen;
        protected final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        protected boolean groupExpanded;

        protected List<OptionEntry> optionEntries;

        private int y;

        private GroupSeparatorEntry(OptionGroup group, Screen screen) {
            this.group = group;
            this.screen = screen;
            this.wrappedName = MultilineText.create(textRenderer, group.name(), getRowWidth() - 45);
            this.wrappedTooltip = MultilineText.create(textRenderer, group.tooltip(), screen.width / 3 * 2 - 10);
            this.groupExpanded = !group.collapsed();
            this.expandMinimizeButton = new LowProfileButtonWidget(0, 0, 20, 20, Text.empty(), btn -> {
                setExpanded(!isExpanded());
                recacheViewableChildren();
            });
            updateExpandMinimizeText();
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.y = y;

            int buttonY = y + entryHeight / 2 - expandMinimizeButton.getHeight() / 2;

            expandMinimizeButton.setY(buttonY);
            expandMinimizeButton.setX(x);
            expandMinimizeButton.render(matrices, mouseX, mouseY, tickDelta);

            wrappedName.drawCenterWithShadow(matrices, x + entryWidth / 2, y + getYPadding());
        }

        @Override
        public void postRender(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            if ((isHovered() && !expandMinimizeButton.isMouseOver(mouseX, mouseY)) || expandMinimizeButton.isFocused()) {
                YACLScreen.renderMultilineTooltip(matrices, textRenderer, wrappedTooltip, getRowLeft() + getRowWidth() / 2, y - 3, y + getItemHeight() + 3, screen.width, screen.height);
            }
        }

        public boolean isExpanded() {
            return groupExpanded;
        }

        public void setExpanded(boolean expanded) {
            this.groupExpanded = expanded;
            updateExpandMinimizeText();
        }

        private void updateExpandMinimizeText() {
            expandMinimizeButton.setMessage(Text.of(isExpanded() ? "▼" : "▶"));
        }

        public void setOptionEntries(List<OptionEntry> optionEntries) {
            this.optionEntries = optionEntries;
        }

        @Override
        public boolean isViewable() {
            return yaclScreen.searchFieldWidget.isEmpty() || optionEntries.stream().anyMatch(OptionEntry::isViewable);
        }

        @Override
        public int getItemHeight() {
            return Math.max(wrappedName.count(), 1) * textRenderer.fontHeight + getYPadding() * 2;
        }

        private int getYPadding() {
            return 6;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return ImmutableList.of(new Selectable() {
                @Override
                public Selectable.SelectionType getType() {
                    return Selectable.SelectionType.HOVERED;
                }

                @Override
                public void appendNarrations(NarrationMessageBuilder builder) {
                    builder.put(NarrationPart.TITLE, group.name());
                    builder.put(NarrationPart.HINT, group.tooltip());
                }
            });
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(expandMinimizeButton);
        }
    }

    public class ListGroupSeparatorEntry extends GroupSeparatorEntry {
        private final TextScaledButtonWidget resetListButton;
        private final TooltipButtonWidget addListButton;

        private ListGroupSeparatorEntry(ListOption<?> group, Screen screen) {
            super(group, screen);

            this.resetListButton = new TextScaledButtonWidget(getRowRight() - 20, -50, 20, 20, 2f, Text.of("\u21BB"), button -> {
                group.requestSetDefault();
            });
            group.addListener((opt, val) -> this.resetListButton.active = !opt.isPendingValueDefault() && opt.available());
            this.resetListButton.active = !group.isPendingValueDefault() && group.available();

            this.addListButton = new TooltipButtonWidget(yaclScreen, resetListButton.getX() - 20, -50, 20, 20, Text.of("+"), Text.translatable("yacl.list.add_top"), btn -> {
                group.insertNewEntryToTop();
            });
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);

            int buttonY = expandMinimizeButton.getY();

            resetListButton.setY(buttonY);
            addListButton.setY(buttonY);

            resetListButton.render(matrices, mouseX, mouseY, tickDelta);
            addListButton.render(matrices, mouseX, mouseY, tickDelta);
        }

        @Override
        public void postRender(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            super.postRender(matrices, mouseX, mouseY, delta);
            
            addListButton.renderHoveredTooltip(matrices);
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(expandMinimizeButton, resetListButton, addListButton);
        }
    }
}
