plugins {
    java
    id("architectury-plugin")
    id("dev.architectury.loom")
    id("io.github.juuxel.loom-quiltflower")

    id("com.github.johnrengelman.shadow")
}

architectury {
    platformSetupLoomIde()
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)
}

val common by configurations.creating
val shadowCommon by configurations.creating
configurations.compileClasspath.get().extendsFrom(common)
configurations.runtimeClasspath.get().extendsFrom(common)

dependencies {
    common(project(path = ":common", configuration = "namedElements")) { isTransitive = false }

}

java {
    withSourcesJar()
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching(listOf("META-INF/mods.toml", "fabric.mod.json")) {
            expand("version" to project.version)
        }
    }

    shadowJar {
        exclude("architectury.common.json")

        configurations = listOf(shadowCommon)
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)
        archiveClassifier.set(null)
    }

    jar {
        archiveClassifier.set("dev")
    }

    named<Jar>("sourcesJar") {
        val commonSources = project(":common").tasks.named<Jar>("sourcesJar").get()
        dependsOn(commonSources)
        from(commonSources.archiveFile.map { zipTree(it) })
    }
}

components.named("java") {
    withGroovyBuilder {
        "withVariantsFromConfiguration"(configurations["shadowRuntimeElements"]) {
            "skip"()
        }
    }
}
//components.java {
//    withVariantsFromConfiguration(project.configurations.shadowRuntimeElements) {
//        skip()
//    }
//}
