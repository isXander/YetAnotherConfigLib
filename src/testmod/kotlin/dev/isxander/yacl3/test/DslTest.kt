package dev.isxander.yacl3.test

import dev.isxander.yacl3.api.OptionFlag
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder
import dev.isxander.yacl3.dsl.*
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation

object Foo {
    var bar = true
    var baz = 0
}

fun kotlinDslGui(parent: Screen?) = YetAnotherConfigLib("namespace") {
    // default title with translation key:
    // `yacl3.config.namespace.title`
    /* NO CODE REQUIRED */

    // or set the title
    title(Component.literal("A cool title"))


    // usual save function
    save {
        // run your save function!
    }

    // get access to an option from the very root of the dsl!
    categories["testCategory"]["testGroup"].getOption("testOption").onReady {
        // do something with it
    }

    val testCategory by categories.registering {
        // default name with translation key:
        // `yacl3.config.namespace.testCategory.testGroup.name`
        /* NO CODE REQUIRED */

        // or set the name
        name { Component.literal("A cool category") }

        // custom tooltip
        tooltipBuilder {
            // add a line like this
            +Component.translatable("somecustomkey")

            // or like this
            text(Component.translatable("somecustomkey"))

            // or like this
            text { Component.translatable("somecustomkey") }
        }

        // you can declare things with strings
        group("testGroup") {
            // default name with translation key:
            // `yacl3.config.namespace.testCategory.testGroup.name`
            /* NO CODE REQUIRED */

            // or set the name
            name { Component.literal("A cool group") }


            // custom description builder:
            descriptionBuilder {
                // blah blah blah
            }

            // default description with translation key:
            // `yacl3.config.namespace.testCategory.testGroup.description.1-5`
            // not compatible with custom description builder
            useDefaultDescription(lines = 5)

            // you can define opts/groups/categories using this delegate syntax
            val testOption by options.registering { // type is automatically inferred from binding
                // default name with translation key:
                // `yacl3.config.namespace.testCategory.testGroup.testOption.name`
                /* NO CODE REQUIRED */

                // custom description builder:
                descriptionBuilder { value -> // changes the desc based on the current value
                    // description with translation key:
                    // `yacl3.config.namespace.testCategory.testGroup.testOption.description.1-5`
                    addDefaultDescription(lines = 5)

                    text { Component.translatable("somecustomkey") }
                    webpImage(ResourceLocation("namespace", "image.png"))
                }

                // KProperties are cool!
                binding(Foo::bar, Foo.bar)

                // you can access other options like this!
                // `options` field is from the enclosing group dsl
                listener { opt, newVal ->
                    options.get<Int>("otherTestOption").onReady { it.setAvailable(newVal) }
                }

                // or even get an access to them before creation
                options.get<Int>("otherTestOption").onReady {
                    // do something with it
                }

                // you can set available with a block
                available { true }

                // regular controller stuff
                // this will be DSLed at some point
                controller(BooleanControllerBuilder::create) {
                    // blah blah blah
                }

                // flags as usual
                flag(OptionFlag.ASSET_RELOAD)
            }

            val otherTestOption by options.registering { // type is automatically inferred from binding
                controller(IntegerSliderControllerBuilder::create) {
                    range(0, 100)
                    step(5)
                }

                binding(Foo::baz, Foo.baz)

                // blah blah blah other stuff
            }
        }
    }
}.generateScreen(parent)
