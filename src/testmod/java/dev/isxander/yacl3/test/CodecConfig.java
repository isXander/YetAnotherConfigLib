package dev.isxander.yacl3.test;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.isxander.yacl3.config.v3.ConfigEntry;
import dev.isxander.yacl3.config.v3.JsonFileCodecConfig;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;

public class CodecConfig extends JsonFileCodecConfig {
    public static final CodecConfig INSTANCE = new CodecConfig();

    public final ConfigEntry<Integer> myInt =
            register("my_int", 0, Codec.INT);

    public final ConfigEntry<String> myString =
            register("my_string", "default", Codec.STRING);

    public final ConfigEntry<ResourceLocation> myIdentifier =
            register("my_identifier", YACLPlatform.rl("test"), ResourceLocation.CODEC);

    public final ConfigEntry<Component> myText =
            register("my_text", Component.literal("Hello"), ComponentSerialization.CODEC);

    public final ConfigEntry<InnerCodecConfig> myInnerConfig =
            register("my_inner_config", InnerCodecConfig.INSTANCE, InnerCodecConfig.INSTANCE);

    public static class InnerCodecConfig extends dev.isxander.yacl3.config.v3.CodecConfig<InnerCodecConfig> {
        public static final InnerCodecConfig INSTANCE = new InnerCodecConfig();
    }

    public CodecConfig() {
        super(YACLPlatform.getConfigDir().resolve("codec_config.json"));
    }

    void test() {
        loadFromFile(); // load like this
        saveToFile(); // save like this

        this.myInt.get();
        this.myInt.set(5);
        this.myInt.defaultValue();

        // or if you just extend Config instead of JsonFileConfig:
        JsonElement element = null;
        this.decode(element, JsonOps.INSTANCE); // load
        DataResult<JsonElement> encoded = this.encodeStart(JsonOps.INSTANCE); // save
    }
}
