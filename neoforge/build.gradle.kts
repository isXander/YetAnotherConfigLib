import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.libs

plugins {
    alias(libs.plugins.architectury.loom)
    alias(libs.plugins.shadow)
    alias(libs.plugins.minotaur)
    alias(libs.plugins.cursegradle)
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

    libs.bundles.twelvemonkeys.imageio.let {
        implementation(it)
        include(it)
        forgeRuntimeLibrary(it)
    }
    libs.bundles.quilt.parsers.let {
        implementation(it)
        include(it)
        forgeRuntimeLibrary(it)
    }

    "common"(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
    "shadowCommon"(project(path = ":common", configuration = "transformProductionNeoForge")) { isTransitive = false }
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

        inputs.property("id", modId)
        inputs.property("group", project.group)
        inputs.property("name", modName)
        inputs.property("description", modDescription)
        inputs.property("version", project.version)
        inputs.property("github", githubProject)

        filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
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
        exclude("fabric.mod.json")
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
        versionName.set("${project.version} (NeoForge)")
        versionNumber.set("${project.version}-neoforge")
        versionType.set(if (isBeta) "beta" else "release")
        uploadFile.set(tasks["remapJar"])
        gameVersions.set(listOf("1.20.4", "1.20.3"))
        loaders.set(listOf("neoforge"))
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
                displayName = "[NeoForge] ${project.version}"
            })

            id = curseforgeId
            releaseType = if (isBeta) "beta" else "release"
            addGameVersion("1.20.4")
            addGameVersion("1.20.3")
            addGameVersion("NeoForge")
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
        create<MavenPublication>("neoforge") {
            groupId = "dev.isxander.yacl"
            artifactId = "yet-another-config-lib-neoforge"

            from(components["java"])
        }
    }
}
tasks.findByPath("publishNeoforgePublicationToReleasesRepository")?.let {
    rootProject.tasks["releaseMod"].dependsOn(it)
}
