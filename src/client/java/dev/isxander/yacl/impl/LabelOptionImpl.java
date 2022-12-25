package dev.isxander.yacl.impl;

import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl.api.*;
import dev.isxander.yacl.gui.controllers.LabelController;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

@ApiStatus.Internal
public final class LabelOptionImpl implements LabelOption {
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
    public @NotNull Text label() {
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

    @ApiStatus.Internal
    public static final class BuilderImpl implements LabelOption.Builder {
        private final List<Text> lines = new ArrayList<>();

        @Override
        public Builder line(@NotNull Text line) {
            Validate.notNull(line, "`line` must not be null");

            this.lines.add(line);
            return this;
        }

        @Override
        public Builder lines(@NotNull Collection<? extends Text> lines) {
            this.lines.addAll(lines);
            return this;
        }

        @Override
        public LabelOption build() {
            MutableText text = Text.empty();
            Iterator<Text> iterator = lines.iterator();
            while (iterator.hasNext()) {
                text.append(iterator.next());

                if (iterator.hasNext())
                    text.append("\n");
            }

            return new LabelOptionImpl(text);
        }
    }
}
