import org.gradle.kotlin.dsl.libs

plugins {
    alias(libs.plugins.architectury.loom)
    alias(libs.plugins.shadow)
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

loom {
    silentMojangMappingsLicense()

    accessWidenerPath.set(project(":common").loom.accessWidenerPath)

    neoForge {

    }

    mods {
        maybeCreate("forge").apply {
            sourceSet(project(":neoforge").sourceSets.main.get())
        }
    }
}

val common by configurations.registering
val shadowCommon by configurations.registering
configurations.compileClasspath.get().extendsFrom(common.get())
configurations["developmentNeoForge"].extendsFrom(common.get())

val minecraftVersion: String = libs.versions.minecraft.get()

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        officialMojangMappings()
        parchment(libs.parchment)
    })
    neoForge(libs.neoforge)

    implementation(libs.twelvemonkeys.imageio.core)
    forgeRuntimeLibrary(libs.twelvemonkeys.imageio.core)
    implementation(libs.twelvemonkeys.imageio.webp)
    forgeRuntimeLibrary(libs.twelvemonkeys.imageio.webp)
    implementation(libs.bundles.quilt.parsers)
    forgeRuntimeLibrary(libs.bundles.quilt.parsers)

    "common"(project(path = ":test-common", configuration = "namedElements")) { isTransitive = false }
    implementation(project(path = ":neoforge", configuration = "namedElements")) { isTransitive = false }

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

        archiveClassifier.set("neoforge-$minecraftVersion")
    }

    jar {
        archiveClassifier.set("dev")
    }
}
