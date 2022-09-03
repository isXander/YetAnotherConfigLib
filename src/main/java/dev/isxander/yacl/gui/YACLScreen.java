package dev.isxander.yacl.gui;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.api.utils.OptionUtils;
import dev.isxander.yacl.impl.YACLConstants;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
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
    public ButtonWidget finishedSaveButton, cancelResetButton, undoButton;

    public Text saveButtonMessage;
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
        Dimension<Integer> categoryDim = Dimension.ofInt(width / 3 / 2, padding, columnWidth - padding * 2, 20);
        int idx = 0;
        for (ConfigCategory category : config.categories()) {
            CategoryWidget categoryWidget = new CategoryWidget(
                    this,
                    category,
                    categoryDim.x() - categoryDim.width() / 2, categoryDim.y(),
                    categoryDim.width(), categoryDim.height()
            );
            if (idx == currentCategoryIdx)
                categoryWidget.active = false;
            categoryButtons.add(categoryWidget);
            addDrawableChild(categoryWidget);

            idx++;
            categoryDim.move(0, 21);
        }

        Dimension<Integer> actionDim = Dimension.ofInt(width / 3 / 2, height - padding - 20, columnWidth - padding * 2, 20);
        finishedSaveButton = new ButtonWidget(actionDim.x() - actionDim.width() / 2, actionDim.y(), actionDim.width(), actionDim.height(), Text.empty(), (btn) -> {
            saveButtonMessage = null;

            if (pendingChanges()) {
                OptionUtils.forEachOptions(config, Option::applyValue);
                OptionUtils.forEachOptions(config, option -> {
                    if (option.changed()) {
                        YACLConstants.LOGGER.error("Option '{}' was saved as '{}' but the changes don't seem to have applied.", option.name().getString(), option.pendingValue());
                        setSaveButtonMessage(Text.translatable("yocl.gui.fail_apply").formatted(Formatting.RED));
                    }
                });
                config.saveFunction().run();
            } else close();
        });
        actionDim.expand(-actionDim.width() / 2 - 2, 0).move(-actionDim.width() / 2 - 2, -22);
        cancelResetButton = new ButtonWidget(actionDim.x() - actionDim.width() / 2, actionDim.y(), actionDim.width(), actionDim.height(), Text.translatable("yacl.gui.cancel"), (btn) -> {
            if (pendingChanges()) {
                OptionUtils.forEachOptions(config, Option::forgetPendingValue);
                close();
            } else {
                OptionUtils.forEachOptions(config, Option::requestSetDefault);
            }

        });
        actionDim.move(actionDim.width() + 4, 0);
        undoButton = new ButtonWidget(actionDim.x() - actionDim.width() / 2, actionDim.y(), actionDim.width(), actionDim.height(), Text.translatable("yacl.gui.undo"), (btn) -> {
            OptionUtils.forEachOptions(config, Option::forgetPendingValue);
        });

        updateActionAvailability();
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

        for (CategoryWidget categoryWidget : categoryButtons) {
            if (categoryWidget.hoveredTicks > YACLConstants.HOVER_TICKS) {
                renderOrderedTooltip(matrices, categoryWidget.wrappedDescription, mouseX, mouseY);
            }
        }
    }

    @Override
    public void tick() {
        updateActionAvailability();

        if (saveButtonMessage != null) {
            if (saveButtonMessageTime > 140) {
                saveButtonMessage = null;
                saveButtonMessageTime = 0;
            } else {
                saveButtonMessageTime++;
                finishedSaveButton.setMessage(saveButtonMessage);
            }
        }
    }

    private void setSaveButtonMessage(Text message) {
        saveButtonMessage = message;
        saveButtonMessageTime = 0;
    }

    public void changeCategory(int idx) {
        int currentIndex = 0;
        for (ButtonWidget categoryWidget : categoryButtons) {
            categoryWidget.active = currentIndex != idx;
            currentIndex++;
        }
        currentCategoryIdx = idx;
        refreshGUI();
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

    private void updateActionAvailability() {
        boolean pendingChanges = pendingChanges();

        undoButton.active = pendingChanges;
        finishedSaveButton.setMessage(pendingChanges ? Text.translatable("yacl.gui.save") : Text.translatable("yacl.gui.finished"));
        cancelResetButton.setMessage(pendingChanges ? Text.translatable("yacl.gui.cancel") : Text.translatable("yacl.gui.reset"));
    }

    private void refreshGUI() {
        init(client, width, height);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (pendingChanges()) {
            setSaveButtonMessage(finishedSaveButton.getMessage().copy().formatted(Formatting.GREEN, Formatting.BOLD));
            return false;
        }
        return true;
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}
