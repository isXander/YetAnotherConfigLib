package dev.isxander.yacl3.config;

import java.lang.reflect.InvocationTargetException;

/**
 * Responsible for handing the actual config data type.
 * Holds the instance along with a final default instance
 * to reference default values for options and should not be changed.
 *
 * Abstract methods to save and load the class, implementations are responsible for
 * how it saves and load.
 *
 * @param <T> config data type
 * @deprecated upgrade to config v2 {@link dev.isxander.yacl3.config.v2.api.ConfigClassHandler}
 */
@Deprecated
public abstract class ConfigInstance<T> {
    private final Class<T> configClass;
    private final T defaultInstance;
    private T instance;

    public ConfigInstance(Class<T> configClass) {
        this.configClass = configClass;

        try {
            this.defaultInstance = this.instance = configClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException(String.format("Could not create default instance of config for %s. Make sure there is a default constructor!", this.configClass.getSimpleName()));
        }
    }

    public abstract void save();
    public abstract void load();

    public T getConfig() {
        return this.instance;
    }

    protected void setConfig(T instance) {
        this.instance = instance;
    }

    public T getDefaults() {
        return this.defaultInstance;
    }

    public Class<T> getConfigClass() {
        return this.configClass;
    }
}
