package dev.isxander.yacl.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.isxander.yacl.api.*;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.api.utils.MutableDimension;
import dev.isxander.yacl.api.utils.OptionUtils;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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

    public Text saveButtonMessage, saveButtonTooltipMessage;
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
        finishedSaveButton = new TooltipButtonWidget(this, actionDim.x() - actionDim.width() / 2, actionDim.y(), actionDim.width(), actionDim.height(), Text.empty(), Text.empty(), (btn) -> {
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
                        option.forgetPendingValue();
                    }
                });
                config.saveFunction().run();

                flags.forEach(flag -> flag.accept(client));
            } else close();
        });
        actionDim.expand(-actionDim.width() / 2 - 2, 0).move(-actionDim.width() / 2 - 2, -22);
        cancelResetButton = new TooltipButtonWidget(this, actionDim.x() - actionDim.width() / 2, actionDim.y(), actionDim.width(), actionDim.height(), Text.empty(), Text.empty(), (btn) -> {
            if (pendingChanges()) { // if pending changes, button acts as a cancel button
                OptionUtils.forEachOptions(config, Option::forgetPendingValue);
                close();
            } else { // if not, button acts as a reset button
                OptionUtils.forEachOptions(config, Option::requestSetDefault);
            }

        });
        actionDim.move(actionDim.width() + 4, 0);
        undoButton = new TooltipButtonWidget(this, actionDim.x() - actionDim.width() / 2, actionDim.y(), actionDim.width(), actionDim.height(), Text.translatable("yacl.gui.undo"), Text.translatable("yacl.gui.undo.tooltip"), (btn) -> {
            OptionUtils.forEachOptions(config, Option::forgetPendingValue);
        });

        searchFieldWidget = new SearchFieldWidget(this, textRenderer, width / 3 / 2 - paddedWidth / 2 + 1, undoButton.getY() - 22, paddedWidth - 2, 18, Text.translatable("gui.recipebook.search_hint"), Text.translatable("gui.recipebook.search_hint"));

        categoryList = new CategoryListWidget(client, this, width, height);
        addSelectableChild(categoryList);

        updateActionAvailability();
        addDrawableChild(searchFieldWidget);
        addDrawableChild(cancelResetButton);
        addDrawableChild(undoButton);
        addDrawableChild(finishedSaveButton);

        optionList = new OptionListWidget(this, client, width, height);
        addSelectableChild(optionList);

        config.initConsumer().accept(this);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);

        super.render(matrices, mouseX, mouseY, delta);
        categoryList.render(matrices, mouseX, mouseY, delta);
        searchFieldWidget.render(matrices, mouseX, mouseY, delta);
        optionList.render(matrices, mouseX, mouseY, delta);

        categoryList.postRender(matrices, mouseX, mouseY, delta);
        optionList.postRender(matrices, mouseX, mouseY, delta);

        for (Element child : children()) {
            if (child instanceof TooltipButtonWidget tooltipButtonWidget) {
                tooltipButtonWidget.renderHoveredTooltip(matrices);
            }
        }
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
            client.setScreen(placeholderCategory.screen().apply(client, this));
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
        finishedSaveButton.setMessage(pendingChanges ? Text.translatable("yacl.gui.save") : ScreenTexts.DONE);
        finishedSaveButton.setTooltip(pendingChanges ? Text.translatable("yacl.gui.save.tooltip") : Text.translatable("yacl.gui.finished.tooltip"));
        cancelResetButton.setMessage(pendingChanges ? ScreenTexts.CANCEL : Text.translatable("controls.reset"));
        cancelResetButton.setTooltip(pendingChanges ? Text.translatable("yacl.gui.cancel.tooltip") : Text.translatable("yacl.gui.reset.tooltip"));
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

    private void setSaveButtonMessage(Text message, Text tooltip) {
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
            setSaveButtonMessage(Text.translatable("yacl.gui.save_before_exit").formatted(Formatting.RED), Text.translatable("yacl.gui.save_before_exit.tooltip"));
            return false;
        }
        return true;
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    public static void renderMultilineTooltip(MatrixStack matrices, TextRenderer textRenderer, MultilineText text, int centerX, int yAbove, int yBelow, int screenWidth, int screenHeight) {
        if (text.count() > 0) {
            int maxWidth = text.getMaxWidth();
            int lineHeight = textRenderer.fontHeight + 1;
            int height = text.count() * lineHeight - 1;

            int belowY = yBelow + 12;
            int aboveY = yAbove - height + 12;
            int maxBelow = screenHeight - (belowY + height);
            int minAbove = aboveY - height;
            int y = belowY;
            if (maxBelow < -8)
                y = maxBelow > minAbove ? belowY : aboveY;

            int x = Math.max(centerX - text.getMaxWidth() / 2 - 12, -6);

            int drawX = x + 12;
            int drawY = y - 12;

            matrices.push();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            TooltipBackgroundRenderer.render(
                    DrawableHelper::fillGradient,
                    matrix4f,
                    bufferBuilder,
                    drawX,
                    drawY,
                    maxWidth,
                    height,
                    400
            );
            RenderSystem.enableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
            matrices.translate(0.0, 0.0, 400.0);

            text.drawWithShadow(matrices, drawX, drawY, lineHeight, -1);

            matrices.pop();
        }
    }
}
