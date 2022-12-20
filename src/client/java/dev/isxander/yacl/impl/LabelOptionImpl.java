package dev.isxander.yacl.impl;

import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl.api.*;
import dev.isxander.yacl.gui.controllers.LabelController;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public class LabelOptionImpl implements LabelOption {
    private final Text label;
    private final Text name = Text.literal("Label Option");
    private final Text tooltip = Text.empty();
    private final LabelController labelController;
    private final Binding<Text> binding;

    public LabelOptionImpl(Text label) {
        this.label = label;
        this.labelController = new LabelController(this);
        this.binding = Binding.immutable(label);
    }

    @Override
    public Text label() {
        return label;
    }

    @Override
    public @NotNull Text name() {
        return name;
    }

    @Override
    public @NotNull Text tooltip() {
        return tooltip;
    }

    @Override
    public @NotNull Controller<Text> controller() {
        return labelController;
    }

    @Override
    public @NotNull Binding<Text> binding() {
        return binding;
    }

    @Override
    public boolean available() {
        return true;
    }

    @Override
    public void setAvailable(boolean available) {
        throw new UnsupportedOperationException("Label options cannot be disabled.");
    }

    @Override
    public @NotNull Class<Text> typeClass() {
        return Text.class;
    }

    @Override
    public @NotNull ImmutableSet<OptionFlag> flags() {
        return ImmutableSet.of();
    }

    @Override
    public boolean changed() {
        return false;
    }

    @Override
    public @NotNull Text pendingValue() {
        return label;
    }

    @Override
    public void requestSet(Text value) {

    }

    @Override
    public boolean applyValue() {
        return false;
    }

    @Override
    public void forgetPendingValue() {

    }

    @Override
    public void requestSetDefault() {

    }

    @Override
    public boolean isPendingValueDefault() {
        return true;
    }

    @Override
    public boolean canResetToDefault() {
        return false;
    }

    @Override
    public void addListener(BiConsumer<Option<Text>, Text> changedListener) {

    }
}
