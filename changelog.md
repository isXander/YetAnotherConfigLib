# YetAnotherConfigLib 3.5.0

This build supports the following versions:
- Fabric 1.20.1
- Fabric 1.20.4
- Fabric 1.20.6 (also supports 1.20.5)
- Fabric 1.21
- NeoForge 1.20.6 (also supports 1.20.5)
- NeoForge 1.20.4
- MinecraftForge 1.20.1

## *Experimental* Codec Config

This update brings a new experimental config API that utilises Mojang's Codec for (de)serialization.

```java
public class CodecConfig extends JsonFileCodecConfig/*or*/CodecConfig {
    public static final CodecConfig INSTANCE = new CodecConfig();

    public final ConfigEntry<Integer> myInt =
            register("my_int", 0, Codec.INT);

    public final ReadonlyConfigEntry<InnerCodecConfig> myInnerConfig =
            register("my_inner_config", InnerCodecConfig.INSTANCE);

    public CodecConfig() {
        super(path);
    }
    
    void test() {
        loadFromFile(); // load like this
        saveToFile(); // save like this
        
        // or if you just extend CodecConfig instead of JsonFileConfig:
        JsonElement element = null;
        this.decode(element, JsonOps.INSTANCE); // load
        DataResult<JsonElement> encoded = this.encodeStart(JsonOps.INSTANCE); // save
    }
}
```
or in Kotlin...
```kotlin
object CodecConfig : JsonFileCodecConfig(path) {
    val myInt by register<Int>(0, Codec.INT)
    
    val myInnerConfig by register(InnerCodecConfig)
    
    fun test() {
        loadFromFile()
        saveToFile()
        
        // blah blah blah
    }
}
```

## Rewritten Kotlin DSL

Completely rewrote the Kotlin DSL!

```kotlin
YetAnotherConfigLib("namespace") {
    val category by categories.registering {
        val option by rootOptions.registering<Int> {
            controller = slider(range = 5..10)
            binding(::thisProp, default)
            
            val otherOption by categories["category"]["group"].futureRef<Boolean>()
            otherOption.onReady { it.setAvailable(false) }
        }
        
        // translation key is generated automagically
        val label by rootOptions.registeringLabel
        
        val group by groups.registering {
            val otherOption = options.register<Boolean>("otherOption") {
                controller = tickBox()
            }
        }
    }
}
```

## Changes

- Fix dropdown controllers erroneously showing their dropdown - Crendgrim
- Make cancel/reset and undo buttons public for accessing
- Add compatibility for 1.21
