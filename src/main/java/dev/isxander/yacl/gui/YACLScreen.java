package dev.isxander.yacl.gui;

import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.api.utils.OptionUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class YACLScreen extends Screen {
    public final YetAnotherConfigLib config;
    public int currentCategoryIdx;

    private final Screen parent;

    public OptionListWidget optionList;
    public final List<ButtonWidget> categoryButtons;
    public ButtonWidget finishedSaveButton, cancelResetButton, undoButton;

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
        Dimension<Integer> categoryDim = Dimension.ofInt(padding, padding, columnWidth - padding * 2, 20);
        int idx = 0;
        for (ConfigCategory category : config.categories()) {
            ButtonWidget categoryWidget = new ButtonWidget(
                    categoryDim.x(), categoryDim.y(),
                    categoryDim.width(), categoryDim.height(),
                    category.name(),
                    (btn) -> changeCategory(categoryButtons.indexOf(btn))
            );
            if (idx == currentCategoryIdx)
                categoryWidget.active = false;
            categoryButtons.add(categoryWidget);
            addDrawableChild(categoryWidget);

            idx++;
            categoryDim = categoryDim.moved(0, 21);
        }

        Dimension<Integer> actionDim = Dimension.ofInt(padding, height - padding - 20, columnWidth - padding * 2, 20);
        finishedSaveButton = new ButtonWidget(actionDim.x(), actionDim.y(), actionDim.width(), actionDim.height(), Text.empty(), (btn) -> {
            if (pendingChanges()) {
                OptionUtils.forEachOptions(config, Option::applyValue);
                config.saveFunction().run();
            } else close();
        });
        actionDim = actionDim.moved(0, -22).expanded(-actionDim.width() / 2 - 2, 0);
        cancelResetButton = new ButtonWidget(actionDim.x(), actionDim.y(), actionDim.width(), actionDim.height(), Text.translatable("yacl.gui.cancel"), (btn) -> {
            if (pendingChanges()) {
                OptionUtils.forEachOptions(config, Option::forgetPendingValue);
                close();
            } else {
                OptionUtils.forEachOptions(config, Option::requestSetDefault);
            }

        });
        actionDim = actionDim.moved(actionDim.width() + 4, 0);
        undoButton = new ButtonWidget(actionDim.x(), actionDim.y(), actionDim.width(), actionDim.height(), Text.translatable("yacl.gui.undo"), (btn) -> {
            OptionUtils.forEachOptions(config, Option::forgetPendingValue);
        });

        updateActionAvailability();
        addDrawableChild(finishedSaveButton);
        addDrawableChild(cancelResetButton);
        addDrawableChild(undoButton);

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
    }

    @Override
    public void tick() {
        updateActionAvailability();
    }

    private void changeCategory(int idx) {
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
                return false;
            }
            return true;
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
        return !undoButton.active;
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }
}
