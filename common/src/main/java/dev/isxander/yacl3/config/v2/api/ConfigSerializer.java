package dev.isxander.yacl3.config.v2.api;

public abstract class ConfigSerializer<T> {
    protected final ConfigClassHandler<T> config;

    public ConfigSerializer(ConfigClassHandler<T> config) {
        this.config = config;
    }

    public abstract void serialize();

    public abstract void deserialize();
}
