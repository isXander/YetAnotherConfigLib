package dev.isxander.yacl3.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.api.utils.MutableDimension;
import dev.isxander.yacl3.api.utils.OptionUtils;
import dev.isxander.yacl3.gui.controllers.PopupControllerScreen;
import dev.isxander.yacl3.gui.controllers.ControllerPopupWidget;
import dev.isxander.yacl3.gui.tab.ListHolderWidget;
import dev.isxander.yacl3.gui.tab.ScrollableNavigationBar;
import dev.isxander.yacl3.gui.tab.TabExt;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class YACLScreen extends Screen {
    public final YetAnotherConfigLib config;

    private final Screen parent;

    public final TabManager tabManager = new TabManager(this::addRenderableWidget, this::removeWidget);
    public ScrollableNavigationBar tabNavigationBar;
    public ScreenRectangle tabArea;

    public Component saveButtonMessage;
    public Tooltip saveButtonTooltipMessage;
    private int saveButtonMessageTime;

    private boolean pendingChanges;

    public ControllerPopupWidget<?> currentPopupController = null;
    public boolean popupControllerVisible = false;

    public YACLScreen(YetAnotherConfigLib config, Screen parent) {
        super(config.title());
        this.config = config;
        this.parent = parent;

        OptionUtils.forEachOptions(config, option -> {
            option.addListener((opt, val) -> onOptionChanged(opt));
        });
    }

    @Override
    protected void init() {
        tabArea = new ScreenRectangle(0, 24 - 1, this.width, this.height - 24 + 1);

        int currentTab = tabNavigationBar != null
                ? tabNavigationBar.getTabs().indexOf(tabManager.getCurrentTab())
                : 0;
        if (currentTab == -1)
            currentTab = 0;

        tabNavigationBar = new ScrollableNavigationBar(this.width, tabManager, config.categories()
                .stream()
                .map(category -> {
                    if (category instanceof CustomTabProvider tabProvider) {
                        return tabProvider.createTab(this, tabArea);
                    }
                    if (category instanceof PlaceholderCategory placeholder)
                        return new PlaceholderTab(placeholder, this);
                    return new CategoryTab(this, category, tabArea);
                }).toList());
        tabNavigationBar.selectTab(currentTab, false);
        tabNavigationBar.arrangeElements();
        tabManager.setTabArea(tabArea);
        addRenderableWidget(tabNavigationBar);

        config.initConsumer().accept(this);
    }

    public void addPopupControllerWidget(ControllerPopupWidget<?> controllerPopupWidget) {

        //Safety check for the color picker
        if (currentPopupController != null) {
            clearPopupControllerWidget();
        }

        currentPopupController = controllerPopupWidget;
        popupControllerVisible = true;

        OptionListWidget optionListWidget = null;
        if(this.tabNavigationBar.getTabManager().getCurrentTab() instanceof CategoryTab categoryTab) {
            optionListWidget = categoryTab.optionList.getList();
        }
        if(optionListWidget != null) {
            this.minecraft.setScreen(new PopupControllerScreen(this, controllerPopupWidget));
        }
    }

    public void clearPopupControllerWidget() {
        if(Minecraft.getInstance().screen instanceof PopupControllerScreen popupControllerScreen) {
            popupControllerScreen.onClose();
        }
        popupControllerVisible = false;
        currentPopupController = null;
    }

    /*? if <=1.20.4 {*/
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderDirtBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
    }
    /*?}*/

    @Override
    public void renderBackground(GuiGraphics guiGraphics/*? if >1.20.1 {*/, int mouseX, int mouseY, float partialTick/*?}*/) {
        super.renderBackground(guiGraphics/*? if >1.20.1 {*/, mouseX, mouseY, partialTick/*?}*/);

        if (tabManager.getCurrentTab() instanceof TabExt tab) {
            tab.renderBackground(guiGraphics);
        }
    }

    public void finishOrSave() {
        saveButtonMessage = null;

        if (pendingChanges()) {
            Set<OptionFlag> flags = new HashSet<>();
            OptionUtils.forEachOptions(config, option -> {
                if (option.applyValue()) {
                    flags.addAll(option.flags());
                }
            });
            OptionUtils.forEachOptions(config, option -> {
                if (option.changed()) {
                    // if still changed after applying, reset to the current value from binding
                    // as something has gone wrong.
                    option.forgetPendingValue();
                    YACLConstants.LOGGER.error("Option '{}' value mismatch after applying! Reset to binding's getter.", option.name().getString());
                }
            });
            config.saveFunction().run();

            flags.forEach(flag -> flag.accept(minecraft));

            pendingChanges = false;
            if (tabManager.getCurrentTab() instanceof CategoryTab categoryTab) {
                categoryTab.updateButtons();
            }
        } else onClose();
    }

    public void cancelOrReset() {
        if (pendingChanges()) { // if pending changes, button acts as a cancel button
            OptionUtils.forEachOptions(config, Option::forgetPendingValue);
            onClose();
        } else { // if not, button acts as a reset button
            OptionUtils.forEachOptions(config, Option::requestSetDefault);
        }
    }

    public void undo() {
        OptionUtils.forEachOptions(config, Option::forgetPendingValue);
    }

    @Override
    public void tick() {
        if (tabManager.getCurrentTab() instanceof TabExt tabExt) {
            tabExt.tick();
        }

        if (tabManager.getCurrentTab() instanceof CategoryTab categoryTab) {
            if (saveButtonMessage != null) {
                if (saveButtonMessageTime > 140) {
                    saveButtonMessage = null;
                    saveButtonTooltipMessage = null;
                    saveButtonMessageTime = 0;
                } else {
                    saveButtonMessageTime++;
                    categoryTab.saveFinishedButton.setMessage(saveButtonMessage);
                    if (saveButtonTooltipMessage != null) {
                        categoryTab.saveFinishedButton.setTooltip(saveButtonTooltipMessage);
                    }
                }
            }
        }
    }

    public void setSaveButtonMessage(Component message, Component tooltip) {
        saveButtonMessage = message;
        saveButtonTooltipMessage = Tooltip.create(tooltip);
        saveButtonMessageTime = 0;
    }

    public boolean pendingChanges() {
        return pendingChanges;
    }

    private void onOptionChanged(Option<?> option) {
        pendingChanges = false;

        OptionUtils.consumeOptions(config, opt -> {
            pendingChanges |= opt.changed();
            return pendingChanges;
        });

        if (tabManager.getCurrentTab() instanceof CategoryTab categoryTab) {
            categoryTab.updateButtons();
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (pendingChanges()) {
            setSaveButtonMessage(Component.translatable("yacl.gui.save_before_exit").withStyle(ChatFormatting.RED), Component.translatable("yacl.gui.save_before_exit.tooltip"));
            return false;
        }
        return true;
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    public static void renderMultilineTooltip(GuiGraphics graphics, Font font, MultiLineLabel text, int centerX, int yAbove, int yBelow, int screenWidth, int screenHeight) {
        if (text.getLineCount() > 0) {
            int maxWidth = text.getWidth();
            int lineHeight = font.lineHeight + 1;
            int height = text.getLineCount() * lineHeight - 1;

            int belowY = yBelow + 12;
            int aboveY = yAbove - height + 12;
            int maxBelow = screenHeight - (belowY + height);
            int minAbove = aboveY - height;
            int y = aboveY;
            if (minAbove < 8)
                y = maxBelow > minAbove ? belowY : aboveY;

            int x = Math.max(centerX - text.getWidth() / 2 - 12, -6);

            int drawX = x + 12;
            int drawY = y - 12;

            graphics.pose().pushPose();
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = tesselator.getBuilder();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            TooltipRenderUtil.renderTooltipBackground(
                    graphics,
                    drawX,
                    drawY,
                    maxWidth,
                    height,
                    400
            );
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            BufferUploader.drawWithShader(bufferBuilder.end());
            RenderSystem.disableBlend();
            graphics.pose().translate(0.0, 0.0, 400.0);

            text.renderLeftAligned(graphics, drawX, drawY, lineHeight, -1);

            graphics.pose().popPose();
        }
    }

    public static class CategoryTab implements TabExt {
        /*? if >1.20.4 {*//*
        private static final ResourceLocation DARKER_BG = new ResourceLocation("textures/gui/menu_list_background.png");
        *//*?}*/

        private final YACLScreen screen;
        private final ConfigCategory category;
        private final Tooltip tooltip;

        private ListHolderWidget<OptionListWidget> optionList;
        public final Button saveFinishedButton;
        private final Button cancelResetButton;
        private final Button undoButton;
        private final SearchFieldWidget searchField;
        private OptionDescriptionWidget descriptionWidget;

        private final ScreenRectangle rightPaneDim;

        public CategoryTab(YACLScreen screen, ConfigCategory category, ScreenRectangle tabArea) {
            this.screen = screen;
            this.category = category;
            this.tooltip = Tooltip.create(category.tooltip());

            int columnWidth = screen.width / 3;
            int padding = columnWidth / 20;
            columnWidth = Math.min(columnWidth, 400);
            int paddedWidth = columnWidth - padding * 2;
            rightPaneDim = new ScreenRectangle(screen.width / 3 * 2, tabArea.top() + 1, screen.width / 3, tabArea.height());
            MutableDimension<Integer> actionDim = Dimension.ofInt(screen.width / 3 * 2 + screen.width / 6, screen.height - padding - 20, paddedWidth, 20);

            saveFinishedButton = Button.builder(Component.literal("Done"), btn -> screen.finishOrSave())
                    .pos(actionDim.x() - actionDim.width() / 2, actionDim.y())
                    .size(actionDim.width(), actionDim.height())
                    .build();

            actionDim.expand(-actionDim.width() / 2 - 2, 0).move(-actionDim.width() / 2 - 2, -22);
            cancelResetButton = Button.builder(Component.literal("Cancel"), btn -> screen.cancelOrReset())
                    .pos(actionDim.x() - actionDim.width() / 2, actionDim.y())
                    .size(actionDim.width(), actionDim.height())
                    .build();

            actionDim.move(actionDim.width() + 4, 0);
            undoButton = Button.builder(Component.translatable("yacl.gui.undo"), btn -> screen.undo())
                    .pos(actionDim.x() - actionDim.width() / 2, actionDim.y())
                    .size(actionDim.width(), actionDim.height())
                    .tooltip(Tooltip.create(Component.translatable("yacl.gui.undo.tooltip")))
                    .build();

            searchField = new SearchFieldWidget(
                    screen,
                    screen.font,
                    screen.width / 3 * 2 + screen.width / 6 - paddedWidth / 2 + 1,
                    undoButton.getY() - 22,
                    paddedWidth - 2, 18,
                    Component.translatable("gui.recipebook.search_hint"),
                    Component.translatable("gui.recipebook.search_hint"),
                    searchQuery -> optionList.getList().updateSearchQuery(searchQuery)
            );

            this.optionList = new ListHolderWidget<>(
                    () -> new ScreenRectangle(tabArea.position(), tabArea.width() / 3 * 2, tabArea.height()),
                    new OptionListWidget(screen, category, screen.minecraft, 0, 0, screen.width / 3 * 2 + 1, screen.height, desc -> {
                        descriptionWidget.setOptionDescription(desc);
                    })
            );

            descriptionWidget = new OptionDescriptionWidget(
                    () -> new ScreenRectangle(
                            screen.width / 3 * 2 + padding,
                            tabArea.top() + padding,
                            paddedWidth,
                            searchField.getY() - 1 - tabArea.top() - padding * 2
                    ),
                    null
            );

            updateButtons();
        }

        @Override
        public Component getTabTitle() {
            return category.name();
        }

        @Override
        public void visitChildren(Consumer<AbstractWidget> consumer) {
            consumer.accept(optionList);
            consumer.accept(saveFinishedButton);
            consumer.accept(cancelResetButton);
            consumer.accept(undoButton);
            consumer.accept(searchField);
            consumer.accept(descriptionWidget);
        }

        /*? if >1.20.4 {*//*
        @Override
        public void renderBackground(GuiGraphics graphics) {
            RenderSystem.enableBlend();
            // right pane darker db
            graphics.blit(DARKER_BG, rightPaneDim.left(), rightPaneDim.top(), rightPaneDim.right() + 2, rightPaneDim.bottom() + 2, rightPaneDim.width() + 2, rightPaneDim.height() + 2, 32, 32);

            // top separator for right pane
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 10);
            graphics.blit(CreateWorldScreen.HEADER_SEPARATOR, rightPaneDim.left() - 1, rightPaneDim.top() - 2, 0.0F, 0.0F, rightPaneDim.width() + 1, 2, 32, 2);
            graphics.pose().popPose();

            // left separator for right pane
            graphics.pose().pushPose();
            graphics.pose().translate(rightPaneDim.left(), rightPaneDim.top() - 1, 0);
            graphics.pose().rotateAround(Axis.ZP.rotationDegrees(90), 0, 0, 1);
            graphics.blit(CreateWorldScreen.FOOTER_SEPARATOR, 0, 0, 0f, 0f, rightPaneDim.height() + 1, 2, 32, 2);
            graphics.pose().popPose();

            RenderSystem.disableBlend();
        }
        *//*?}*/

        @Override
        public void doLayout(ScreenRectangle screenRectangle) {

        }

        @Override
        public void tick() {
            descriptionWidget.tick();
        }

        @Nullable
        @Override
        public Tooltip getTooltip() {
            return tooltip;
        }

        public void updateButtons() {
            boolean pendingChanges = screen.pendingChanges();

            undoButton.active = pendingChanges;
            saveFinishedButton.setMessage(pendingChanges ? Component.translatable("yacl.gui.save") : GuiUtils.translatableFallback("yacl.gui.done", CommonComponents.GUI_DONE));
            saveFinishedButton.setTooltip(new YACLTooltip(pendingChanges ? Component.translatable("yacl.gui.save.tooltip") : Component.translatable("yacl.gui.finished.tooltip"), saveFinishedButton));
            cancelResetButton.setMessage(pendingChanges ? GuiUtils.translatableFallback("yacl.gui.cancel", CommonComponents.GUI_CANCEL) : Component.translatable("controls.reset"));
            cancelResetButton.setTooltip(new YACLTooltip(pendingChanges ? Component.translatable("yacl.gui.cancel.tooltip") : Component.translatable("yacl.gui.reset.tooltip"), cancelResetButton));
        }
    }

    public static class PlaceholderTab implements TabExt {
        private final YACLScreen screen;
        private final PlaceholderCategory category;
        private final Tooltip tooltip;

        public PlaceholderTab(PlaceholderCategory category, YACLScreen screen) {
            this.screen = screen;
            this.category = category;
            this.tooltip = Tooltip.create(category.tooltip());
        }

        @Override
        public Component getTabTitle() {
            return category.name();
        }

        @Override
        public void visitChildren(Consumer<AbstractWidget> consumer) {

        }

        @Override
        public void doLayout(ScreenRectangle screenRectangle) {
            screen.minecraft.setScreen(category.screen().apply(screen.minecraft, screen));
        }

        @Override
        public @Nullable Tooltip getTooltip() {
            return this.tooltip;
        }
    }
}
