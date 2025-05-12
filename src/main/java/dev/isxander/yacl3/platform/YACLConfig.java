package dev.isxander.yacl3.platform;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;

public class YACLConfig {
    public static final ConfigClassHandler<YACLConfig> HANDLER = ConfigClassHandler.createBuilder(YACLConfig.class)
            .id(YACLPlatform.rl("config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("yacl.json5"))
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry(comment = "Show the flashing colour picker hint (auto disables after first use)")
    public boolean showColorPickerIndicator = true;

    @SerialEntry(comment = "Load .webp and .gif during Minecraft resource reload instead of on-demand (can decrease startup time)")
    public boolean preloadComplexImageFormats = false;
}
