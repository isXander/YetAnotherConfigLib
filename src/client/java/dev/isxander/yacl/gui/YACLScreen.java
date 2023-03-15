package dev.isxander.yacl.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.isxander.yacl.api.*;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.api.utils.MutableDimension;
import dev.isxander.yacl.api.utils.OptionUtils;
import dev.isxander.yacl.gui.utils.GuiUtils;
import dev.isxander.yacl.impl.utils.YACLConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class YACLScreen extends Screen {
    public final YetAnotherConfigLib config;
    private int currentCategoryIdx;

    private final Screen parent;

    public OptionListWidget optionList;
    public CategoryListWidget categoryList;
    public TooltipButtonWidget finishedSaveButton, cancelResetButton, undoButton;
    public SearchFieldWidget searchFieldWidget;

    public Component saveButtonMessage, saveButtonTooltipMessage;
    private int saveButtonMessageTime;


    public YACLScreen(YetAnotherConfigLib config, Screen parent) {
        super(config.title());
        this.config = config;
        this.parent = parent;
        this.currentCategoryIdx = 0;
    }

    @Override
    protected void init() {
        int columnWidth = width / 3;
        int padding = columnWidth / 20;
        columnWidth = Math.min(columnWidth, 400);
        int paddedWidth = columnWidth - padding * 2;

        MutableDimension<Integer> actionDim = Dimension.ofInt(width / 3 / 2, height - padding - 20, paddedWidth, 20);
        finishedSaveButton = new TooltipButtonWidget(
                this,
                actionDim.x() - actionDim.width() / 2,
                actionDim.y(),
                actionDim.width(),
                actionDim.height(),
                Component.empty(),
                Component.empty(),
                btn -> finishOrSave()
        );
        actionDim.expand(-actionDim.width() / 2 - 2, 0).move(-actionDim.width() / 2 - 2, -22);
        cancelResetButton = new TooltipButtonWidget(
                this,
                actionDim.x() - actionDim.width() / 2,
                actionDim.y(),
                actionDim.width(),
                actionDim.height(),
                Component.empty(),
                Component.empty(),
                btn -> cancelOrReset()
        );
        actionDim.move(actionDim.width() + 4, 0);
        undoButton = new TooltipButtonWidget(
                this,
                actionDim.x() - actionDim.width() / 2,
                actionDim.y(),
                actionDim.width(),
                actionDim.height(),
                Component.translatable("yacl.gui.undo"),
                Component.translatable("yacl.gui.undo.tooltip"),
                btn -> undo()
        );

        searchFieldWidget = new SearchFieldWidget(
                this,
                font,
                width / 3 / 2 - paddedWidth / 2 + 1,
                undoButton.getY() - 22,
                paddedWidth - 2, 18,
                Component.translatable("gui.recipebook.search_hint"),
                Component.translatable("gui.recipebook.search_hint")
        );

        categoryList = new CategoryListWidget(minecraft, this, width, height);
        addWidget(categoryList);

        updateActionAvailability();
        addRenderableWidget(searchFieldWidget);
        addRenderableWidget(cancelResetButton);
        addRenderableWidget(undoButton);
        addRenderableWidget(finishedSaveButton);

        optionList = new OptionListWidget(this, minecraft, width, height);
        addWidget(optionList);

        config.initConsumer().accept(this);
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);

        super.render(matrices, mouseX, mouseY, delta);
        categoryList.render(matrices, mouseX, mouseY, delta);
        searchFieldWidget.render(matrices, mouseX, mouseY, delta);
        optionList.render(matrices, mouseX, mouseY, delta);

        categoryList.postRender(matrices, mouseX, mouseY, delta);
        optionList.postRender(matrices, mouseX, mouseY, delta);

        for (GuiEventListener child : children()) {
            if (child instanceof TooltipButtonWidget tooltipButtonWidget) {
                tooltipButtonWidget.renderHoveredTooltip(matrices);
            }
        }
    }

    protected void finishOrSave() {
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
        } else onClose();
    }

    protected void cancelOrReset() {
        if (pendingChanges()) { // if pending changes, button acts as a cancel button
            OptionUtils.forEachOptions(config, Option::forgetPendingValue);
            onClose();
        } else { // if not, button acts as a reset button
            OptionUtils.forEachOptions(config, Option::requestSetDefault);
        }
    }

    protected void undo() {
        OptionUtils.forEachOptions(config, Option::forgetPendingValue);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (optionList.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (optionList.charTyped(chr, modifiers)) {
            return true;
        }

        return super.charTyped(chr, modifiers);
    }

    public void changeCategory(int idx) {
        if (idx == currentCategoryIdx)
            return;

        if (idx != -1 && config.categories().get(idx) instanceof PlaceholderCategory placeholderCategory) {
            minecraft.setScreen(placeholderCategory.screen().apply(minecraft, this));
        } else {
            currentCategoryIdx = idx;
            optionList.refreshOptions();
        }
    }

    public int getCurrentCategoryIdx() {
        return currentCategoryIdx;
    }

    private void updateActionAvailability() {
        boolean pendingChanges = pendingChanges();

        undoButton.active = pendingChanges;
        finishedSaveButton.setMessage(pendingChanges ? Component.translatable("yacl.gui.save") : GuiUtils.translatableFallback("yacl.gui.done", CommonComponents.GUI_DONE));
        finishedSaveButton.setTooltip(pendingChanges ? Component.translatable("yacl.gui.save.tooltip") : Component.translatable("yacl.gui.finished.tooltip"));
        cancelResetButton.setMessage(pendingChanges ? GuiUtils.translatableFallback("yacl.gui.cancel", CommonComponents.GUI_CANCEL) : Component.translatable("controls.reset"));
        cancelResetButton.setTooltip(pendingChanges ? Component.translatable("yacl.gui.cancel.tooltip") : Component.translatable("yacl.gui.reset.tooltip"));
    }

    @Override
    public void tick() {
        searchFieldWidget.tick();

        updateActionAvailability();

        if (saveButtonMessage != null) {
            if (saveButtonMessageTime > 140) {
                saveButtonMessage = null;
                saveButtonTooltipMessage = null;
                saveButtonMessageTime = 0;
            } else {
                saveButtonMessageTime++;
                finishedSaveButton.setMessage(saveButtonMessage);
                if (saveButtonTooltipMessage != null) {
                    finishedSaveButton.setTooltip(saveButtonTooltipMessage);
                }
            }
        }
    }

    private void setSaveButtonMessage(Component message, Component tooltip) {
        saveButtonMessage = message;
        saveButtonTooltipMessage = tooltip;
        saveButtonMessageTime = 0;
    }

    private boolean pendingChanges() {
        AtomicBoolean pendingChanges = new AtomicBoolean(false);
        OptionUtils.consumeOptions(config, (option) -> {
            if (option.changed()) {
                pendingChanges.set(true);
                return true;
            }
            return false;
        });

        return pendingChanges.get();
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

    public static void renderMultilineTooltip(PoseStack matrices, Font font, MultiLineLabel text, int centerX, int yAbove, int yBelow, int screenWidth, int screenHeight) {
        if (text.getLineCount() > 0) {
            int maxWidth = text.getWidth();
            int lineHeight = font.lineHeight + 1;
            int height = text.getLineCount() * lineHeight - 1;

            int belowY = yBelow + 12;
            int aboveY = yAbove - height + 12;
            int maxBelow = screenHeight - (belowY + height);
            int minAbove = aboveY - height;
            int y = belowY;
            if (maxBelow < -8)
                y = maxBelow > minAbove ? belowY : aboveY;

            int x = Math.max(centerX - text.getWidth() / 2 - 12, -6);

            int drawX = x + 12;
            int drawY = y - 12;

            matrices.pushPose();
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferBuilder = tesselator.getBuilder();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            Matrix4f matrix4f = matrices.last().pose();
            TooltipRenderUtil.renderTooltipBackground(
                    GuiComponent::fillGradient,
                    matrix4f,
                    bufferBuilder,
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
            matrices.translate(0.0, 0.0, 400.0);

            text.renderLeftAligned(matrices, drawX, drawY, lineHeight, -1);

            matrices.popPose();
        }
    }
}
