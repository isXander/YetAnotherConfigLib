plugins {
    alias(libs.plugins.architectury.loom)
    alias(libs.plugins.shadow)
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    silentMojangMappingsLicense()

    accessWidenerPath.set(project(":common").loom.accessWidenerPath)
}

val common by configurations.registering
val shadowCommon by configurations.registering
configurations.compileClasspath.get().extendsFrom(common.get())
configurations["developmentFabric"].extendsFrom(common.get())

val minecraftVersion = libs.versions.minecraft.get()

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        val qm = libs.versions.quilt.mappings.get()
        if (qm != "0")
            mappings("org.quiltmc:quilt-mappings:${libs.versions.minecraft.get()}+build.${libs.versions.quilt.mappings.get()}:intermediary-v2")
        officialMojangMappings()
    })
    modImplementation(libs.fabric.loader)

    "common"(project(path = ":test-common", configuration = "namedElements")) { isTransitive = false }
    implementation(project(path = ":fabric", configuration = "namedElements"))

    "common"(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
}

tasks {
    shadowJar {
        exclude("architectury.common.json")

        configurations = listOf(shadowCommon.get())
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        injectAccessWidener.set(true)
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)

        archiveClassifier.set("fabric-$minecraftVersion")
    }

    jar {
        archiveClassifier.set("dev")
    }
}
