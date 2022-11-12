package dev.isxander.yacl.config;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class YACLConfigManager {
    private static final Map<Class<?>, ConfigInstance<?>> configs = new HashMap<>();

    public static <T> void register(ConfigInstance<T> configInstance) {
        configs.put(configInstance.getConfigClass(), configInstance);
        configInstance.load();
    }

    public static <T> T getConfigData(Class<T> configClass) {
        return ((ConfigInstance<T>) configs.get(configClass)).getConfig();
    }

    public static <T> ConfigInstance<T> getConfigInstance(Class<T> configClass) {
        return (ConfigInstance<T>) configs.get(configClass);
    }

    public static <T, I extends ConfigInstance<T>> I getConfigInstanceType(Class<T> configClass) {
        return (I) configs.get(configClass);
    }
}
