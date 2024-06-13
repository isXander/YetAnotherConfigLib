package dev.isxander.yacl3.test

import com.mojang.serialization.Codec
import dev.isxander.yacl3.api.OptionFlag
import dev.isxander.yacl3.config.v3.JsonFileCodecConfig
import dev.isxander.yacl3.config.v3.register
import dev.isxander.yacl3.dsl.*
import dev.isxander.yacl3.platform.YACLPlatform
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.resources.ResourceLocation

object CodecConfigKt : JsonFileCodecConfig<CodecConfigKt>(YACLPlatform.getConfigDir().resolve("codec_config_kt.json")) {
    val myInt by register<Int>(0, Codec.INT)

    val myString by register<String>("default", Codec.STRING)

    val myBoolean by register<Boolean>(false, Codec.BOOL)

    val myIdentifier by register<ResourceLocation>(YACLPlatform.rl("test"), ResourceLocation.CODEC)

    val myText by register<Component>(Component.literal("Hello, World!"), ComponentSerialization.CODEC)

    init {
        if (!loadFromFile()) {
            saveToFile()
        }
    }

    fun generateConfigScreen(lastScreen: Screen?) = YetAnotherConfigLib("namespace") {
        // default title with translation key:
        // `yacl3.config.namespace.title`
        /* NO CODE REQUIRED */

        // or set the title
        title(Component.literal("A cool title"))


        // usual save function
        save {
            // run your save function!
            saveToFile()
        }

        // get access to an option from the very root of the dsl!
        categories["testCategory"]["testGroup"].futureRef<String>("myIntOption").onReady {
            // do something with it
        }

        val testCategory by categories.registering {
            // default name with translation key:
            // `yacl3.config.namespace.testCategory.testGroup.name`
            /* NO CODE REQUIRED */

            // or set the name
            name { Component.literal("A cool category") }

            // custom tooltip
            tooltip {
                // add a line like this
                +Component.translatable("somecustomkey")

                // or like this
                text(Component.translatable("somecustomkey"))

                // or like this
                text { Component.translatable("somecustomkey") }
            }

            // creates a label with the id `testLabel`
            val testLabel by rootOptions.registeringLabel

            // you can declare things with strings
            groups.register("testGroup") {
                // default name with translation key:
                // `yacl3.config.namespace.testCategory.testGroup.name`
                /* NO CODE REQUIRED */

                // or set the name
                name { Component.literal("A cool group") }


                // custom description builder:
                descriptionBuilder {
                    // default description with translation key:
                    // `yacl3.config.namespace.testCategory.testGroup.description.1-5`
                    // not compatible with custom description builder
                    addDefaultText(lines = 5)
                }

                // you can define opts/groups/categories using this delegate syntax
                val myIntOption by options.registering<Int> { // type is automatically inferred from binding
                    // default name with translation key:
                    // `yacl3.config.namespace.testCategory.testGroup.testOption.name`
                    /* NO CODE REQUIRED */

                    // custom description builder:
                    descriptionBuilderDyn { value -> // changes the desc based on the current value
                        // description with translation key:
                        // `yacl3.config.namespace.testCategory.testGroup.testOption.description.1-5`
                        addDefaultText(lines = 5)

                        text { Component.translatable("somecustomkey") }
                        webpImage(YACLPlatform.rl("namespace", "image.png"))
                    }

                    // Codecs!
                    binding = myInt.asBinding()

                    // you can access other options like this!
                    // `options` field is from the enclosing group dsl
                    listener { opt, newVal ->
                        options.futureRef<String>("myString").onReady {

                        }
                    }

                    // or use a delegated property to create a reference to an option
                    val myStringOption by options.ref<String>() // nullable

                    // you can set available with a property
                    available = true
                    // ...or a block
                    available { true }

                    // cool custom controller functions
                    controller = slider(range = 5..10, step = 1)

                    // flags as usual
                    flag(OptionFlag.ASSET_RELOAD)
                }

                // codec config api automatically sets binding and name
                val myStringOption = options.register(myString) {
                    controller = stringField()
                }

                val myBooleanOption = options.register(myBoolean) {
                    // custom formatters for these cool controller functions
                    controller = textSwitch { bool -> Component.literal(bool.toString()) }
                }
            }
        }
    }.generateScreen(lastScreen)
}
