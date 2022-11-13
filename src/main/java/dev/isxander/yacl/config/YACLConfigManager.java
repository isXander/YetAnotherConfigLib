package dev.isxander.yacl.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple storage of config instances ({@link ConfigInstance}) for ease of access.
 */
@SuppressWarnings("unchecked")
public class YACLConfigManager {
    private static final Map<Class<?>, ConfigInstance<?>> configs = new HashMap<>();

    /**
     * Registers and loads a config instance
     *
     * @param configInstance config to register
     * @param <T> config data type
     */
    public static <T> void register(ConfigInstance<T> configInstance) {
        configs.put(configInstance.getConfigClass(), configInstance);
        configInstance.load();
    }

    /**
     * Retrieves config data for a certain config.
     * <p>
     * Shorthand of {@code YACLConfigManager.getConfigInstance(configClass).getConfig()}
     *
     * @param configClass config data to get
     * @return config data
     * @param <T> config data type
     */
    public static <T> T getConfigData(Class<T> configClass) {
        return ((ConfigInstance<T>) configs.get(configClass)).getConfig();
    }

    /**
     * Retrieves the config instance for a certain config.
     *
     * @param configClass config data type instance is bound to
     * @return config instance
     * @param <T> config data type
     */
    public static <T> ConfigInstance<T> getConfigInstance(Class<T> configClass) {
        return (ConfigInstance<T>) configs.get(configClass);
    }

    /**
     * Very similar to {@link YACLConfigManager#getConfigInstance(Class)} but can retrieve
     * a certain implementation of {@link ConfigInstance}
     *
     * @param configClass config data type is bound to
     * @return config instance
     * @param <T> config data type
     * @param <U> config instance type
     */
    public static <T, U extends ConfigInstance<T>> U getConfigInstanceType(Class<T> configClass) {
        return (U) configs.get(configClass);
    }
}
