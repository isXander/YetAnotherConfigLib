package dev.isxander.yacl.gui;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.OptionGroup;
import dev.isxander.yacl.api.utils.Dimension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class OptionListWidget extends ElementListWidget<OptionListWidget.Entry> {
    private final YACLScreen yaclScreen;
    private boolean singleCategory = false;

    private ImmutableList<Entry> viewableChildren;

    private double smoothScrollAmount = getScrollAmount();
    private boolean returnSmoothAmount = false;

    public OptionListWidget(YACLScreen screen, MinecraftClient client, int width, int height) {
        super(client, width / 3 * 2, height, 0, height, 22);
        this.yaclScreen = screen;
        left = width - this.width;
        right = width;

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
                GroupSeparatorEntry groupSeparatorEntry = null;
                if (!group.isRoot()) {
                    groupSeparatorEntry = new GroupSeparatorEntry(group, yaclScreen);
                    viewableSupplier = groupSeparatorEntry::isExpanded;
                    addEntry(groupSeparatorEntry);
                } else {
                    viewableSupplier = () -> true;
                }

                List<OptionEntry> optionEntries = new ArrayList<>();
                for (Option<?> option : group.options()) {
                    OptionEntry entry = new OptionEntry(option, category, group, option.controller().provideWidget(yaclScreen, Dimension.ofInt(getRowLeft(), 0, getRowWidth(), 20)), viewableSupplier);
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
    }

    public void expandAllGroups() {
        for (Entry entry : super.children()) {
            if (entry instanceof GroupSeparatorEntry groupSeparatorEntry) {
                groupSeparatorEntry.setExpanded(true);
            }
        }
    }

    /*
      below code is licensed from cloth-config under LGPL3
      modified to inherit vanilla's EntryListWidget and use yarn mappings
    */

    @Nullable
    @Override
    protected Entry getEntryAtPosition(double x, double y) {
        int listMiddleX = this.left + this.width / 2;
        int minX = listMiddleX - this.getRowWidth() / 2;
        int maxX = listMiddleX + this.getRowWidth() / 2;
        int currentY = MathHelper.floor(y - (double) this.top) - this.headerHeight + (int) this.getScrollAmount() - 4;
        int itemY = 0;
        int itemIndex = -1;
        for (int i = 0; i < children().size(); i++) {
            Entry item = children().get(i);
            itemY += item.getItemHeight();
            if (itemY > currentY) {
                itemIndex = i;
                break;
            }
        }
        return x < (double) this.getScrollbarPositionX() && x >= minX && y <= maxX && itemIndex >= 0 && currentY >= 0 && itemIndex < this.getEntryCount() ? this.children().get(itemIndex) : null;
    }

    @Override
    protected int getMaxPosition() {
        return children().stream().map(Entry::getItemHeight).reduce(0, Integer::sum) + headerHeight;
    }

    @Override
    protected void centerScrollOn(Entry entry) {
        double d = (this.bottom - this.top) / -2d;
        for (int i = 0; i < this.children().indexOf(entry) && i < this.getEntryCount(); i++)
            d += children().get(i).getItemHeight();
        this.setScrollAmount(d);
    }

    @Override
    protected int getRowTop(int index) {
        int integer = top + 4 - (int) this.getScrollAmount() + headerHeight;
        for (int i = 0; i < children().size() && i < index; i++)
            integer += children().get(i).getItemHeight();
        return integer;
    }

    @Override
    protected void renderList(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int left = this.getRowLeft();
        int right = this.getRowWidth();
        int count = this.getEntryCount();

        for(int i = 0; i < count; ++i) {
            Entry entry = children().get(i);
            int top = this.getRowTop(i);
            int bottom = top + entry.getItemHeight();
            int entryHeight = entry.getItemHeight() - 4;
            if (bottom >= this.top && top <= this.bottom) {
                this.renderEntry(matrices, mouseX, mouseY, delta, i, left, top, right, entryHeight);
            }
        }
    }

    /* END cloth config code */

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        smoothScrollAmount = MathHelper.lerp(MinecraftClient.getInstance().getLastFrameDuration() * 0.5, smoothScrollAmount, getScrollAmount());
        returnSmoothAmount = true;
        super.render(matrices, mouseX, mouseY, delta);
        returnSmoothAmount = false;
    }

    /**
     * awful code to only use smooth scroll state when rendering,
     * not other code that needs target scroll amount
     */
    @Override
    public double getScrollAmount() {
        if (returnSmoothAmount)
            return smoothScrollAmount;

        return super.getScrollAmount();
    }

    public void postRender(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        for (Entry entry : children()) {
            entry.postRender(matrices, mouseX, mouseY, delta);
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
        for (Entry child : children()) {
            if (child.mouseScrolled(mouseX, mouseY, amount))
                return true;
        }

        this.setScrollAmount(this.getScrollAmount() - amount * 20 /* * (double) (getMaxScroll() / getEntryCount()) / 2.0D */);
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
        return left + width - (int)(width * 0.05f);
    }

    @Override
    protected void renderBackground(MatrixStack matrices) {
        setRenderBackground(client.world == null);
        if (client.world != null)
            fill(matrices, left, top, right, bottom, 0x6B000000);
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

    public abstract class Entry extends ElementListWidget.Entry<Entry> {
        public void postRender(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        }

        public boolean isViewable() {
            return true;
        }

        public int getItemHeight() {
            return 22;
        }

        protected boolean isHovered() {
            return Objects.equals(getHoveredEntry(), this);
        }
    }

    public class OptionEntry extends Entry {
        public final Option<?> option;
        public final ConfigCategory category;
        public final OptionGroup group;

        public final AbstractWidget widget;
        private final Supplier<Boolean> viewableSupplier;

        private final TextScaledButtonWidget resetButton;

        private final String categoryName;
        private final String groupName;

        private OptionEntry(Option<?> option, ConfigCategory category, OptionGroup group, AbstractWidget widget, Supplier<Boolean> viewableSupplier) {
            this.option = option;
            this.category = category;
            this.group = group;
            this.widget = widget;
            this.viewableSupplier = viewableSupplier;
            this.categoryName = category.name().getString().toLowerCase();
            this.groupName = group.name().getString().toLowerCase();
            if (this.widget.canReset()) {
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

        private final Screen screen;
        private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        private boolean groupExpanded;

        private List<OptionEntry> optionEntries;

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

            expandMinimizeButton.setX(x);
            expandMinimizeButton.setY(y + entryHeight / 2 - expandMinimizeButton.getHeight() / 2);
            expandMinimizeButton.render(matrices, mouseX, mouseY, tickDelta);

            wrappedName.drawCenterWithShadow(matrices, x + entryWidth / 2, y + getYPadding());
        }

        @Override
        public void postRender(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            if (isHovered() && !expandMinimizeButton.isMouseOver(mouseX, mouseY)) {
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
            expandMinimizeButton.setMessage(Text.of(isExpanded() ? "\u25BC" : "\u25B6"));
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
                }
            });
        }

        @Override
        public List<? extends Element> children() {
            return ImmutableList.of(expandMinimizeButton);
        }
    }
}
