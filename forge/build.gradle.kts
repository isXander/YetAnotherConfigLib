import org.gradle.jvm.tasks.Jar

plugins {
    alias(libs.plugins.architectury.loom)
    alias(libs.plugins.shadow)
    alias(libs.plugins.unified.publishing)
}

architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    silentMojangMappingsLicense()

    accessWidenerPath.set(project(":common").loom.accessWidenerPath)

    forge {
        mixinConfig("yacl.mixins.json")

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

    "common"(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
    "shadowCommon"(project(path = ":common", configuration = "transformProductionForge")) { isTransitive = false }
}

java {
    withSourcesJar()
}

tasks {
    processResources {
        val modId: String by rootProject
        val modName: String by rootProject
        val modDescription: String by rootProject
        val githubProject: String by rootProject
        val majorForge = libs.versions.forge.get().substringAfter('-').split('.').first()

        inputs.property("id", modId)
        inputs.property("group", project.group)
        inputs.property("name", modName)
        inputs.property("description", modDescription)
        inputs.property("version", project.version)
        inputs.property("github", githubProject)
        inputs.property("major_forge", majorForge)

        filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
            expand(
                "id" to modId,
                "group" to project.group,
                "name" to modName,
                "description" to modDescription,
                "version" to project.version,
                "github" to githubProject,
                "major_forge" to majorForge,
            )
        }
    }

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

        archiveClassifier.set("forge")
    }

    named<Jar>("sourcesJar") {
        archiveClassifier.set("dev-sources")
        val commonSources = project(":common").tasks.named<Jar>("sourcesJar")
        dependsOn(commonSources)
        from(commonSources.get().archiveFile.map { zipTree(it) })
    }

    remapSourcesJar {
        archiveClassifier.set("forge-sources")
    }

    jar {
        archiveClassifier.set("dev")
    }
}

components["java"].withGroovyBuilder {
    "withVariantsFromConfiguration"(configurations["shadowRuntimeElements"]) {
        "skip"()
    }
}

unifiedPublishing {
    project {
        displayName.set("${project.version} (Forge)")
        releaseType.set("release")
        gameVersions.set(listOf("1.19.3", "1.19.4"))
        gameLoaders.set(listOf("forge"))
        changelog.set(file("changelogs/${project.version}.md").takeIf { it.exists() }?.readText() ?: "No changelog provided.")

        mainPublication(tasks.remapJar.get())
        secondaryPublication(tasks.remapSourcesJar.get().archiveFile)

        val modrinthId: String? by rootProject
        if (modrinthId?.isNotEmpty() == true) {
            modrinth {
                token.set(findProperty("modrinth.token")?.toString() ?: "Modrinth publishing token not found")
                id.set(modrinthId)
                version.set("${project.version}-forge")
            }
        }

        val curseforgeId: String? by rootProject
        if (curseforgeId?.isNotEmpty() == true) {
            curseforge {
                token.set(findProperty("curseforge.token")?.toString() ?: "Curseforge publishing token not found")
                id.set(curseforgeId)
                gameVersions.add("Java 17")
            }
        }
    }
}

rootProject.tasks["releaseMod"].dependsOn(tasks["publishUnified"])
