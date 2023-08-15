package dev.isxander.yacl3.config.v2.api;

/**
 * The base class for config serializers,
 * offering a method to save and load.
 * @param <T>
 */
public abstract class ConfigSerializer<T> {
    protected final ConfigClassHandler<T> config;

    public ConfigSerializer(ConfigClassHandler<T> config) {
        this.config = config;
    }

    /**
     * Saves all fields in the config class.
     * This can be done any way as it's abstract, but most
     * commonly it is saved to a file.
     */
    public abstract void save();

    /**
     * Loads all fields in the config class.
     */
    public abstract void load();
}
