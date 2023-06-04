plugins {
    alias(libs.plugins.architectury.loom)
    alias(libs.plugins.shadow)
}

architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    silentMojangMappingsLicense()

    accessWidenerPath.set(project(":common").loom.accessWidenerPath)

    forge {
        convertAccessWideners.set(true)
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
    }
}

val common by configurations.registering
val shadowCommon by configurations.registering
configurations.compileClasspath.get().extendsFrom(common.get())
configurations["developmentForge"].extendsFrom(common.get())

val minecraftVersion: String = libs.versions.minecraft.get()

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        mappings("org.quiltmc:quilt-mappings:$minecraftVersion+build.${libs.versions.quilt.mappings.get()}:intermediary-v2")
        officialMojangMappings()
    })
    forge(libs.forge)

    implementation(libs.twelvemonkeys.imageio.core)
    forgeRuntimeLibrary(libs.twelvemonkeys.imageio.core)
    implementation(libs.twelvemonkeys.imageio.webp)
    forgeRuntimeLibrary(libs.twelvemonkeys.imageio.webp)

    "common"(project(path = ":test-common", configuration = "namedElements")) { isTransitive = false }
    implementation(project(path = ":forge", configuration = "namedElements"))

    "common"(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
}

tasks {
    shadowJar {
        exclude("fabric.mod.json")
        exclude("architectury.common.json")

        configurations = listOf(shadowCommon.get())
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        injectAccessWidener.set(true)
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)

        archiveClassifier.set("forge-$minecraftVersion")
    }

    jar {
        archiveClassifier.set("dev")
    }
}
