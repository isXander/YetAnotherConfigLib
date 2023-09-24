import org.gradle.jvm.tasks.Jar

plugins {
    alias(libs.plugins.architectury.loom)
    alias(libs.plugins.shadow)
    alias(libs.plugins.minotaur)
    alias(libs.plugins.cursegradle)
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

    listOf(
        "fabric-resource-loader-v0",
    ).forEach { modApi(fabricApi.module(it, libs.versions.fabric.api.get())) }
    modApi(libs.mod.menu)

    libs.bundles.twelvemonkeys.imageio.let {
        implementation(it)
        include(it)
    }
    libs.bundles.quilt.parsers.let {
        implementation(it)
        include(it)
    }

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
        archiveClassifier.set(null as String?)

        from(rootProject.file("LICENSE"))
    }

    named<Jar>("sourcesJar") {
        archiveClassifier.set("dev-sources")
        val commonSources = project(":common").tasks.named<Jar>("sourcesJar")
        dependsOn(commonSources)
        from(commonSources.get().archiveFile.map { zipTree(it) })
    }

    remapSourcesJar {
        archiveClassifier.set("sources")
    }

    jar {
        archiveClassifier.set("dev")
    }
}

components["java"].run {
    if (this is AdhocComponentWithVariants) {
        withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
            skip()
        }
    }
}
val changelogText: String by ext
val isBeta: Boolean by ext

val modrinthId: String by project
if (modrinthId.isNotEmpty()) {
    modrinth {
        token.set(findProperty("modrinth.token")?.toString())
        projectId.set(modrinthId)
        versionName.set("${project.version} (Fabric)")
        versionNumber.set("${project.version}-fabric")
        versionType.set(if (isBeta) "beta" else "release")
        uploadFile.set(tasks["remapJar"])
        gameVersions.set(listOf("1.20", "1.20.1"))
        loaders.set(listOf("fabric", "quilt"))
        changelog.set(changelogText)
        syncBodyFrom.set(rootProject.file("README.md").readText())
    }
}
rootProject.tasks["releaseMod"].dependsOn(tasks["modrinth"])

val curseforgeId: String by project
if (hasProperty("curseforge.token") && curseforgeId.isNotEmpty()) {
    curseforge {
        apiKey = findProperty("curseforge.token")
        project(closureOf<me.hypherionmc.cursegradle.CurseProject> {
            mainArtifact(tasks["remapJar"], closureOf<me.hypherionmc.cursegradle.CurseArtifact> {
                displayName = "[Fabric] ${project.version}"
            })

            id = curseforgeId
            releaseType = if (isBeta) "beta" else "release"
            addGameVersion("1.20")
            addGameVersion("1.20.1")
            addGameVersion("Fabric")
            addGameVersion("Quilt")
            addGameVersion("Java 17")

            changelog = changelogText
            changelogType = "markdown"
        })

        options(closureOf<me.hypherionmc.cursegradle.Options> {
            forgeGradleIntegration = false
            fabricIntegration = false
        })
    }
}
rootProject.tasks["releaseMod"].dependsOn(tasks["curseforge"])

publishing {
    publications {
        create<MavenPublication>("fabric") {
            groupId = "dev.isxander.yacl"
            artifactId = "yet-another-config-lib-fabric"

            from(components["java"])
        }
    }
}
tasks.findByPath("publishFabricPublicationToReleasesRepository")?.let {
    rootProject.tasks["releaseMod"].dependsOn(it)
}
