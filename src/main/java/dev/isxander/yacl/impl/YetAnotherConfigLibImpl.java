package dev.isxander.yacl.impl;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.api.utils.OptionUtils;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.serialization.IYACLSerializer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;
import java.util.function.Function;

@ApiStatus.Internal
public class YetAnotherConfigLibImpl implements YetAnotherConfigLib {
    private final Text title;
    private final ImmutableList<ConfigCategory> categories;
    private final IYACLSerializer serializer;
    private final Consumer<YACLScreen> initConsumer;

    public YetAnotherConfigLibImpl(Text title, ImmutableList<ConfigCategory> categories, Function<YetAnotherConfigLib, IYACLSerializer> serializer, Consumer<YACLScreen> initConsumer) {
        this.title = title;
        this.categories = categories;
        this.serializer = serializer.apply(this);
        this.initConsumer = initConsumer;
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
    public IYACLSerializer serializer() {
        return serializer;
    }

    @Override
    public Consumer<YACLScreen> initConsumer() {
        return initConsumer;
    }

    @Override
    public Screen generateScreen(Screen parent) {
        // reusable builder
        OptionUtils.forEachOptions(this, Option::forgetPendingValue);

        return new YACLScreen(this, parent);
    }
}
