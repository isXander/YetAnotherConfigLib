package dev.isxander.yacl.config;

import dev.isxander.yacl.impl.utils.YACLConstants;
import net.minecraft.nbt.*;

import java.awt.*;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Uses {@link net.minecraft.nbt} to serialize and deserialize to and from an NBT file.
 * Data can be written as compressed GZIP or uncompressed NBT.
 *
 * You can optionally provide custom implementations for handling certain classes if the default
 * handling fails with {@link NbtSerializer}
 *
 * @param <T> config data type
 * @deprecated Using NBT for config is not very practical, implementation flawed, does not support upcoming lists.
 */
@Deprecated
@SuppressWarnings("unchecked")
public class NbtConfigInstance<T> extends ConfigInstance<T> {
    private final Path path;
    private final boolean compressed;
    private final NbtSerializerHolder nbtSerializerHolder;

    /**
     * Constructs an instance with compression on
     *
     * @param configClass config data type class
     * @param path file to write nbt to
     */
    public NbtConfigInstance(Class<T> configClass, Path path) {
        this(configClass, path, holder -> holder, true);
    }

    /**
     * @param configClass config data type class
     * @param path file to write nbt to
     * @param serializerHolderBuilder allows you to add custom serializers
     * @param compressed whether to compress the NBT
     */
    public NbtConfigInstance(Class<T> configClass, Path path, UnaryOperator<NbtSerializerHolder> serializerHolderBuilder, boolean compressed) {
        super(configClass);
        this.path = path;
        this.compressed = compressed;
        this.nbtSerializerHolder = serializerHolderBuilder.apply(new NbtSerializerHolder());
    }

    @Override
    public void save() {
        YACLConstants.LOGGER.info("Saving {}...", getConfigClass().getSimpleName());

        NbtCompound nbt;
        try {
            nbt = (NbtCompound) serializeObject(getConfig(), nbtSerializerHolder, field -> field.isAnnotationPresent(ConfigEntry.class));
        } catch (IllegalAccessException e) {
            YACLConstants.LOGGER.error("Failed to convert '{}' -> NBT", getConfigClass().getName(), e);
            return;
        }

        try(FileOutputStream fos = new FileOutputStream(path.toFile())) {
            if (Files.notExists(path))
                Files.createFile(path);

            if (compressed)
                NbtIo.writeCompressed(nbt, fos);
            else
                NbtIo.write(nbt, new DataOutputStream(fos));
        } catch (IOException e) {
            YACLConstants.LOGGER.error("Failed to write NBT to '{}'", path, e);
        }
    }

    @Override
    public void load() {
        if (Files.notExists(path)) {
            save();
            return;
        }

        YACLConstants.LOGGER.info("Loading {}...", getConfigClass().getSimpleName());
        NbtCompound nbt;
        try {
            nbt = compressed ? NbtIo.readCompressed(path.toFile()) : NbtIo.read(path.toFile());
        } catch (IOException e) {
            YACLConstants.LOGGER.error("Failed to read NBT file '{}'", path, e);
            return;
        }

        try {
            setConfig(deserializeObject(nbt, getConfigClass(), nbtSerializerHolder, field -> field.isAnnotationPresent(ConfigEntry.class)));
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            YACLConstants.LOGGER.error("Failed to convert NBT -> '{}'", getConfigClass().getName(), e);
        }
    }

    public Path getPath() {
        return this.path;
    }

    public boolean isUsingCompression() {
        return this.compressed;
    }

    private static NbtElement serializeObject(Object object, NbtSerializerHolder serializerHolder, Predicate<Field> topLevelPredicate) throws IllegalAccessException {
        if (serializerHolder.hasSerializer(object.getClass())) {
            return serializerHolder.serialize(object);
        }
        else if (object instanceof Object[] ol) {
            NbtList nbtList = new NbtList();
            for (Object obj : ol)
                nbtList.add(serializeObject(obj, serializerHolder, field -> true));
            return nbtList;
        } else {
            NbtCompound compound = new NbtCompound();
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) || !topLevelPredicate.test(field))
                    continue;

                System.out.println(field.getName());
                field.setAccessible(true);

                String key = toCamelCase(field.getName());
                NbtElement value = serializeObject(field.get(object), serializerHolder, f -> true);
                compound.put(key, value);
            }

            return compound;
        }
    }

    private static <T> T deserializeObject(NbtElement element, Class<T> type, NbtSerializerHolder serializerHolder, Predicate<Field> topLevelPredicate) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (serializerHolder.hasSerializer(type))
            return serializerHolder.get(type).deserialize(element, type);
        else if (type == Array.class) {
            List<Object> list = new ArrayList<>();

            Class<?> arrayType = Array.newInstance(type.getComponentType(), 0).getClass();
            NbtList nbtList = (NbtList) element;
            for (NbtElement nbtElement : nbtList) {
                list.add(deserializeObject(nbtElement, arrayType, serializerHolder, field -> true));
            }

            return (T) list.toArray();
        } else {
            if (!(element instanceof NbtCompound compound))
                throw new IllegalStateException("Cannot deserialize " + type.getName());

            T object = type.getConstructor().newInstance();
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) || !topLevelPredicate.test(field))
                    continue;

                field.setAccessible(true);
                String key = toCamelCase(field.getName());
                if (!compound.contains(key))
                    continue;
                field.set(object, deserializeObject(compound.get(key), field.getType(), serializerHolder, f -> true));
            }

            return object;
        }
    }

    /* shamelessly stolen from gson */
    private static String toCamelCase(String name) {
        StringBuilder translation = new StringBuilder();
        for (int i = 0, length = name.length(); i < length; i++) {
            char character = name.charAt(i);
            if (Character.isUpperCase(character) && translation.length() != 0) {
                translation.append('_');
            }
            translation.append(character);
        }
        return translation.toString().toLowerCase(Locale.ENGLISH);
    }

    public static class NbtSerializerHolder {
        private final Map<Class<?>, NbtSerializer<?>> serializerMap = new HashMap<>();

        private NbtSerializerHolder() {
            register(boolean.class, NbtSerializer.<Boolean, NbtByte>simple(b -> b ? NbtByte.ONE : NbtByte.ZERO, nbt -> nbt.byteValue() != 0));
            register(Boolean.class, NbtSerializer.<Boolean, NbtByte>simple(b -> b ? NbtByte.ONE : NbtByte.ZERO, nbt -> nbt.byteValue() != 0));
            register(int.class, NbtSerializer.simple(NbtInt::of, NbtInt::intValue));
            register(Integer.class, NbtSerializer.simple(NbtInt::of, NbtInt::intValue));register(int[].class, NbtSerializer.simple(NbtIntArray::new, NbtIntArray::getIntArray));
            register(float.class, NbtSerializer.simple(NbtFloat::of, NbtFloat::floatValue));
            register(Float.class, NbtSerializer.simple(NbtFloat::of, NbtFloat::floatValue));
            register(double.class, NbtSerializer.simple(NbtDouble::of, NbtDouble::doubleValue));
            register(Double.class, NbtSerializer.simple(NbtDouble::of, NbtDouble::doubleValue));
            register(short.class, NbtSerializer.simple(NbtShort::of, NbtShort::shortValue));
            register(Short.class, NbtSerializer.simple(NbtShort::of, NbtShort::shortValue));
            register(byte.class, NbtSerializer.simple(NbtByte::of, NbtByte::byteValue));
            register(Byte.class, NbtSerializer.simple(NbtByte::of, NbtByte::byteValue));
            register(byte[].class, NbtSerializer.simple(NbtByteArray::new, NbtByteArray::getByteArray));
            register(long.class, NbtSerializer.simple(NbtLong::of, NbtLong::longValue));
            register(Long.class, NbtSerializer.simple(NbtLong::of, NbtLong::longValue));
            register(long[].class, NbtSerializer.simple(NbtLongArray::new, NbtLongArray::getLongArray));
            register(String.class, NbtSerializer.simple(NbtString::of, NbtString::asString));
            register(Enum.class, NbtSerializer.simple(e -> NbtString.of(e.name()), (nbt, type) -> Arrays.stream(type.getEnumConstants()).filter(e -> e.name().equals(nbt.asString())).findFirst().orElseThrow()));

            register(Color.class, new ColorSerializer());
        }

        public <T> NbtSerializerHolder register(Class<T> clazz, NbtSerializer<T> serializer) {
            serializerMap.put(clazz, serializer);
            return this;
        }

        public <T> NbtSerializer<T> get(Class<T> clazz) {
            return (NbtSerializer<T>) search(clazz).findFirst().orElseThrow().getValue();
        }

        public boolean hasSerializer(Class<?> clazz) {
            return search(clazz).findAny().isPresent();
        }

        public NbtElement serialize(Object object) {
            return ((NbtSerializer<Object>) get(object.getClass())).serialize(object);
        }

        private Stream<Map.Entry<Class<?>, NbtSerializer<?>>> search(Class<?> type) {
            return serializerMap.entrySet().stream().filter(entry -> entry.getKey().isAssignableFrom(type));
        }
    }

    public interface NbtSerializer<T> {
        NbtElement serialize(T object);

        T deserialize(NbtElement element, Class<T> type);

        static <T, U extends NbtElement> NbtSerializer<T> simple(Function<T, U> serializer, Function<U, T> deserializer) {
            return simple(serializer, (nbt, type) -> deserializer.apply(nbt));
        }

        static <T, U extends NbtElement> NbtSerializer<T> simple(Function<T, U> serializer, BiFunction<U, Class<T>, T> deserializer) {
            return new NbtSerializer<>() {
                @Override
                public NbtElement serialize(T object) {
                    return serializer.apply(object);
                }

                @Override
                public T deserialize(NbtElement element, Class<T> type) {
                    return deserializer.apply((U) element, type);
                }
            };
        }
    }

    public static class ColorSerializer implements NbtSerializer<Color> {

        @Override
        public NbtElement serialize(Color object) {
            return NbtInt.of(object.getRGB());
        }

        @Override
        public Color deserialize(NbtElement element, Class<Color> type) {
            return new Color(((NbtInt) element).intValue(), true);
        }
    }
}
