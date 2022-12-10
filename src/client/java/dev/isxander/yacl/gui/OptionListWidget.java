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
import java.util.function.Supplier;

public class OptionListWidget extends ElementListWidgetExt<OptionListWidget.Entry> {
    private final YACLScreen yaclScreen;
    private boolean singleCategory = false;

    private ImmutableList<Entry> viewableChildren;

    public OptionListWidget(YACLScreen screen, MinecraftClient client, int width, int height) {
        super(client, width / 3, 0, width / 3 * 2, height, true);
        this.yaclScreen = screen;

        refreshOptions();
    }

    public void refreshOptions() {
        clearEntries();

        List<ConfigCategory> categories = new ArrayList<>();
        if (yaclScreen.getCurrentCategoryIdx() == -1) {
            categories.addAll(yaclScreen.config.categories());
        } else {
            categories.add(yaclScreen.config.categories().get(yaclScreen.getCurrentCategoryIdx()));
        }
        singleCategory = categories.size() == 1;

        for (ConfigCategory category : categories) {
            for (OptionGroup group : category.groups()) {
                Supplier<Boolean> viewableSupplier;
                GroupSeparatorEntry groupSeparatorEntry;
                if (!group.isRoot()) {
                    groupSeparatorEntry = new GroupSeparatorEntry(group, category, yaclScreen);
                    viewableSupplier = groupSeparatorEntry::isExpanded;
                    addEntry(groupSeparatorEntry);

                    if (group instanceof ListGroup<?> listGroup) {
                        listGroup.onRedraw((opt, val) -> {
                            for (OptionEntry entry : groupSeparatorEntry.optionEntries)
                                removeEntry(entry);

                            groupSeparatorEntry.optionEntries.clear();
                            Entry lastEntry = groupSeparatorEntry;
                            for (ListOptionEntry<?> listOptionEntry : listGroup.options()) {
                                OptionEntry optionEntry = new OptionEntry(listOptionEntry, category, group, groupSeparatorEntry, listOptionEntry.controller().provideWidget(yaclScreen, Dimension.ofInt(getRowLeft(), 0, getRowWidth(), 20)), viewableSupplier);
                                addEntryBelow(lastEntry, optionEntry);
                                groupSeparatorEntry.optionEntries.add(optionEntry);
                                lastEntry = optionEntry;
                            }
                        });
                    }
                } else {
                    groupSeparatorEntry = null;
                    viewableSupplier = () -> true;
                }

                List<OptionEntry> optionEntries = new ArrayList<>();
                for (Option<?> option : group.options()) {
                    OptionEntry entry = new OptionEntry(option, category, group, groupSeparatorEntry, option.controller().provideWidget(yaclScreen, Dimension.ofInt(getRowLeft(), 0, getRowWidth(), 20)), viewableSupplier);
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
        super.children().add(super.children().indexOf(below) + 1, entry);
        recacheViewableChildren();
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
        public final Supplier<Boolean> viewableSupplier;

        private final TextScaledButtonWidget resetButton;

        private final String categoryName;
        private final String groupName;

        public OptionEntry(Option<?> option, ConfigCategory category, OptionGroup group, @Nullable GroupSeparatorEntry groupSeparatorEntry, AbstractWidget widget, Supplier<Boolean> viewableSupplier) {
            this.option = option;
            this.category = category;
            this.group = group;
            this.groupSeparatorEntry = groupSeparatorEntry;
            if (option instanceof ListOptionEntry<?> listOptionEntry)
                this.widget = new ListEntryWidget(yaclScreen, OptionListWidget.this, this, listOptionEntry, widget);
            else this.widget = widget;
            this.viewableSupplier = viewableSupplier;
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
            return viewableSupplier.get()
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
        private final OptionGroup group;
        private final MultilineText wrappedName;
        private final MultilineText wrappedTooltip;

        private final LowProfileButtonWidget expandMinimizeButton;

        private final TextScaledButtonWidget resetListButton;
        private final TooltipButtonWidget addListButton;

        private final Screen screen;
        private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        private boolean groupExpanded;

        private List<OptionEntry> optionEntries;

        private int y;

        private GroupSeparatorEntry(OptionGroup group, ConfigCategory category, Screen screen) {
            this.group = group;
            this.screen = screen;
            this.wrappedName = MultilineText.create(textRenderer, group.name(), getRowWidth() - 45);
            this.wrappedTooltip = MultilineText.create(textRenderer, group.tooltip(), screen.width / 3 * 2 - 10);
            this.groupExpanded = !group.collapsed();
            this.expandMinimizeButton = new LowProfileButtonWidget(0, 0, 20, 20, Text.empty(), btn -> {
                setExpanded(!isExpanded());
                recacheViewableChildren();
            });
            if (group instanceof ListGroup<?> listGroup && listGroup.canResetToDefault()) {
                this.resetListButton = new TextScaledButtonWidget(getRowRight() - 20, -50, 20, 20, 2f, Text.of("\u21BB"), button -> {
                    listGroup.requestSetDefault();
                });
                listGroup.addListener((opt, val) -> this.resetListButton.active = !opt.isPendingValueDefault() && opt.available());
                this.resetListButton.active = !listGroup.isPendingValueDefault() && listGroup.available();

                this.addListButton = new TooltipButtonWidget(yaclScreen, resetListButton.getX() - 20, -50, 20, 20, Text.of("+"), Text.translatable("yacl.gui.list.add_top"), btn -> {
                    ListOptionEntry<?> newEntry = listGroup.insertNewEntryToTop();
                    //OptionListWidget.OptionEntry newOptEntry = new OptionEntry(newEntry, category, group, this, newEntry.controller().provideWidget(yaclScreen, Dimension.ofInt(getRowLeft(), 0, getRowWidth(), 20)), this::isExpanded);
                    //addEntryBelowWithoutScroll(this, newOptEntry);

                    //optionEntries.add(0, newOptEntry);
                });
            } else {
                this.resetListButton = null;
                this.addListButton = null;
            }
            updateExpandMinimizeText();
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.y = y;

            int buttonY = y + entryHeight / 2 - expandMinimizeButton.getHeight() / 2;

            expandMinimizeButton.setY(buttonY);
            expandMinimizeButton.setX(x);
            expandMinimizeButton.render(matrices, mouseX, mouseY, tickDelta);

            if (resetListButton != null && addListButton != null) {
                resetListButton.setY(buttonY);
                resetListButton.render(matrices, mouseX, mouseY, tickDelta);

                addListButton.setY(buttonY);
                addListButton.render(matrices, mouseX, mouseY, tickDelta);
            }

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
            if (resetListButton != null && addListButton != null)
                return ImmutableList.of(expandMinimizeButton, resetListButton, addListButton);
            else
                return ImmutableList.of(expandMinimizeButton);
        }
    }
}
