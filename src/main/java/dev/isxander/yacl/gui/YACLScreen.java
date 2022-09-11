package dev.isxander.yacl.gui;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.api.utils.OptionUtils;
import dev.isxander.yacl.impl.YACLConstants;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class YACLScreen extends Screen {
    public final YetAnotherConfigLib config;
    public int currentCategoryIdx;

    private final Screen parent;

    public OptionListWidget optionList;
    public final List<CategoryWidget> categoryButtons;
    public TooltipButtonWidget finishedSaveButton, cancelResetButton, undoButton;
    public SearchFieldWidget searchFieldWidget;

    public Text saveButtonMessage;
    public Text saveButtonTooltipMessage;
    private int saveButtonMessageTime;


    public YACLScreen(YetAnotherConfigLib config, Screen parent) {
        super(config.title());
        this.config = config;
        this.parent = parent;
        this.categoryButtons = new ArrayList<>();
        this.currentCategoryIdx = 0;
    }

    @Override
    protected void init() {
        categoryButtons.clear();
        int columnWidth = width / 3;
        int padding = columnWidth / 20;
        columnWidth = Math.min(columnWidth, 400);
        int paddedWidth = columnWidth - padding * 2;
        Dimension<Integer> categoryDim = Dimension.ofInt(width / 3 / 2, padding, paddedWidth, 20);
        int idx = 0;
        for (ConfigCategory category : config.categories()) {
            CategoryWidget categoryWidget = new CategoryWidget(
                    this,
                    category,
                    idx,
                    categoryDim.x() - categoryDim.width() / 2, categoryDim.y(),
                    categoryDim.width(), categoryDim.height()
            );

            categoryButtons.add(categoryWidget);
            addDrawableChild(categoryWidget);

            idx++;
            categoryDim.move(0, 21);
        }

        searchFieldWidget = new SearchFieldWidget(this, textRenderer, width / 3 / 2 - paddedWidth / 2 + 1, height - 71, paddedWidth - 2, 18, Text.translatable("yacl.gui.search"), Text.translatable("yacl.gui.search"));

        Dimension<Integer> actionDim = Dimension.ofInt(width / 3 / 2, height - padding - 20, paddedWidth, 20);
        finishedSaveButton = new TooltipButtonWidget(this, actionDim.x() - actionDim.width() / 2, actionDim.y(), actionDim.width(), actionDim.height(), Text.empty(), Text.empty(), (btn) -> {
            saveButtonMessage = null;

            if (pendingChanges()) {
                AtomicBoolean requiresRestart = new AtomicBoolean(false);
                OptionUtils.forEachOptions(config, option -> {
                    if (option.requiresRestart() && option.changed())
                        requiresRestart.set(true);
                    option.applyValue();
                });
                OptionUtils.forEachOptions(config, option -> {
                    if (option.changed()) {
                        YACLConstants.LOGGER.error("'{}' was saved as '{}' but the changes don't seem to have applied. (Maybe binding is immutable?)", option.name().getString(), option.pendingValue());
                        setSaveButtonMessage(Text.translatable("yacl.gui.fail_apply").formatted(Formatting.RED), Text.translatable("yacl.gui.fail_apply.tooltip"));
                    }
                });
                config.serializer().save();
                if (requiresRestart.get()) {
                    client.setScreen(new RequireRestartScreen(this));
                }
            } else close();
        });
        actionDim.expand(-actionDim.width() / 2 - 2, 0).move(-actionDim.width() / 2 - 2, -22);
        cancelResetButton = new TooltipButtonWidget(this, actionDim.x() - actionDim.width() / 2, actionDim.y(), actionDim.width(), actionDim.height(), Text.empty(), Text.empty(), (btn) -> {
            if (pendingChanges()) {
                OptionUtils.forEachOptions(config, Option::forgetPendingValue);
                close();
            } else {
                OptionUtils.forEachOptions(config, Option::requestSetDefault);
            }

        });
        actionDim.move(actionDim.width() + 4, 0);
        undoButton = new TooltipButtonWidget(this, actionDim.x() - actionDim.width() / 2, actionDim.y(), actionDim.width(), actionDim.height(), Text.translatable("yacl.gui.undo"), Text.translatable("yacl.gui.undo.tooltip"), (btn) -> {
            OptionUtils.forEachOptions(config, Option::forgetPendingValue);
        });

        updateActionAvailability();
        addDrawableChild(searchFieldWidget);
        addDrawableChild(cancelResetButton);
        addDrawableChild(undoButton);
        addDrawableChild(finishedSaveButton);

        ConfigCategory currentCategory = config.categories().get(currentCategoryIdx);
        optionList = new OptionListWidget(currentCategory, this, client, width, height);
        addSelectableChild(optionList);

        config.initConsumer().accept(this);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);

        super.render(matrices, mouseX, mouseY, delta);

        optionList.render(matrices, mouseX, mouseY, delta);
        searchFieldWidget.render(matrices, mouseX, mouseY, delta);

        for (Element child : children()) {
            if (child instanceof TooltipButtonWidget tooltipButtonWidget) {
                tooltipButtonWidget.renderTooltip(matrices, mouseX, mouseY);
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
        currentCategoryIdx = idx;
        refreshGUI();
    }

    private void updateActionAvailability() {
        boolean pendingChanges = pendingChanges();

        undoButton.active = pendingChanges;
        finishedSaveButton.setMessage(pendingChanges ? Text.translatable("yacl.gui.save") : Text.translatable("gui.done"));
        finishedSaveButton.setTooltip(pendingChanges ? Text.translatable("yacl.gui.save.tooltip") : Text.translatable("yacl.gui.finished.tooltip"));
        cancelResetButton.setMessage(pendingChanges ? Text.translatable("gui.cancel") : Text.translatable("controls.reset"));
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

    private void refreshGUI() {
        init(client, width, height);
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
}
