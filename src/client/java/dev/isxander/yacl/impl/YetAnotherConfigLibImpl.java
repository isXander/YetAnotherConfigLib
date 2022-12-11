package dev.isxander.yacl.impl;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.impl.utils.YACLConstants;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.function.Consumer;

public final class YetAnotherConfigLibImpl implements YetAnotherConfigLib {
    private final Text title;
    private final ImmutableList<ConfigCategory> categories;
    private final Runnable saveFunction;
    private final Consumer<YACLScreen> initConsumer;

    private boolean generated = false;

    public YetAnotherConfigLibImpl(Text title, ImmutableList<ConfigCategory> categories, Runnable saveFunction, Consumer<YACLScreen> initConsumer) {
        this.title = title;
        this.categories = categories;
        this.saveFunction = saveFunction;
        this.initConsumer = initConsumer;
    }

    @Override
    public Screen generateScreen(Screen parent) {
        if (generated)
            throw new UnsupportedOperationException("To prevent memory leaks, you should only generate a Screen once per instance. Please re-build the instance to generate another GUI.");

        YACLConstants.LOGGER.info("Generating YACL screen");
        generated = true;
        return new YACLScreen(this, parent);
    }

    @Override
    public Text title() {
        return title;
    }

    @Override
    public ImmutableList<ConfigCategory> categories() {
        return categories;
    }

    @Override
    public Runnable saveFunction() {
        return saveFunction;
    }

    @Override
    public Consumer<YACLScreen> initConsumer() {
        return initConsumer;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (YetAnotherConfigLibImpl) obj;
        return Objects.equals(this.title, that.title) &&
                Objects.equals(this.categories, that.categories) &&
                Objects.equals(this.saveFunction, that.saveFunction) &&
                Objects.equals(this.initConsumer, that.initConsumer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, categories, saveFunction, initConsumer);
    }

    @Override
    public String toString() {
        return "YetAnotherConfigLibImpl[" +
                "title=" + title + ", " +
                "categories=" + categories + ", " +
                "saveFunction=" + saveFunction + ", " +
                "initConsumer=" + initConsumer + ']';
    }

}
