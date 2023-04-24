import org.gradle.jvm.tasks.Jar

plugins {
    alias(libs.plugins.architectury.loom)
    alias(libs.plugins.shadow)
    alias(libs.plugins.unified.publishing)
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
        mappings("org.quiltmc:quilt-mappings:$minecraftVersion+build.${libs.versions.quilt.mappings.get()}:intermediary-v2")
        officialMojangMappings()
    })
    modImplementation(libs.fabric.loader)

    listOf(
        "fabric-resource-loader-v0"
    ).forEach { modApi(fabricApi.module(it, libs.versions.fabric.api.get())) }
    modApi(libs.mod.menu)

    "common"(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
    "shadowCommon"(project(path = ":common", configuration = "transformProductionFabric")) { isTransitive = false }
}

java {
    withSourcesJar()
}

tasks {
    processResources {
        val modId: String by project
        val modName: String by project
        val modDescription: String by project
        val githubProject: String by project

        inputs.property("id", modId)
        inputs.property("group", project.group)
        inputs.property("name", modName)
        inputs.property("description", modDescription)
        inputs.property("version", project.version)
        inputs.property("github", githubProject)

        filesMatching("fabric.mod.json") {
            expand(
                "id" to modId,
                "group" to project.group,
                "name" to modName,
                "description" to modDescription,
                "version" to project.version,
                "github" to githubProject,
            )
        }
    }

    shadowJar {
        exclude("architectury.common.json")

        configurations = listOf(shadowCommon.get())
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        injectAccessWidener.set(true)
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)

        archiveClassifier.set("fabric")
    }

    named<Jar>("sourcesJar") {
        archiveClassifier.set("dev-sources")
        val commonSources = project(":common").tasks.named<Jar>("sourcesJar")
        dependsOn(commonSources)
        from(commonSources.get().archiveFile.map { zipTree(it) })
    }

    remapSourcesJar {
        archiveClassifier.set("fabric-sources")
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

val changelogText: String by ext

unifiedPublishing {
    project {
        displayName.set("${project.version} (Fabric)")
        releaseType.set("release")
        gameVersions.set(listOf("1.19.3", "1.19.4"))
        gameLoaders.set(listOf("fabric", "quilt"))
        changelog.set(changelogText)

        mainPublication(tasks.remapJar.get())
        secondaryPublication(tasks.remapSourcesJar.get().archiveFile)

        val modrinthId: String? by rootProject
        if (modrinthId?.isNotEmpty() == true) {
            modrinth {
                token.set(findProperty("modrinth.token")?.toString() ?: "Modrinth publishing token not found")
                id.set(modrinthId)
                version.set("${project.version}-fabric")
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

publishing {
    publications {
        create<MavenPublication>("fabric") {
            groupId = "dev.isxander.yacl"
            artifactId = "yet-another-config-lib-fabric"

            from(components["java"])
            artifact(tasks.remapSourcesJar.get())
        }
    }
}
tasks.findByPath("publishFabricPublicationToReleasesRepository")?.let {
    rootProject.tasks["releaseMod"].dependsOn(it)
}
