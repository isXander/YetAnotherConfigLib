import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm")

    id("dev.architectury.loom")

    id("me.modmuss50.mod-publish-plugin")
    `maven-publish`
    id("org.ajoberstar.grgit")
}

val loader = loom.platform.get().name.lowercase()
val isFabric = loader == "fabric"
val isNeoforge = loader == "neoforge"
val isForge = loader == "forge"
val isForgeLike = isNeoforge || isForge

val mcVersion = findProperty("mcVersion").toString()

group = "dev.isxander"
val versionWithoutMC = "3.6.0"
version = "$versionWithoutMC+${stonecutter.current.project}"

val snapshotVer = "${grgit.branch.current().name.replace('/', '.')}-SNAPSHOT"
if (System.getenv().containsKey("GITHUB_ACTIONS")) {
    version = "$version+$snapshotVer"
}

val isAlpha = "alpha" in version.toString()
val isBeta = "beta" in version.toString()

base {
    archivesName.set(property("modName").toString())
}

val testmod by sourceSets.creating {
    compileClasspath += sourceSets.main.get().compileClasspath
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
}

val accessWidenerName = "yacl.accesswidener"
loom {
    accessWidenerPath.set(rootProject.file("src/main/resources/$accessWidenerName"))

    runConfigs.all {
        ideConfigGenerated(false)
        runDir("../../run")
    }
    runs {
        create("testmodClient") {
            client()
            name = "Testmod Client"
            source(testmod)
            ideConfigGenerated(true)
            runDir("../../run")

            if (isForgeLike) {
                mods {
                    register("main") {
                        sourceSet(sourceSets.main.get())
                    }
                    register("testMod") {
                        sourceSet(testmod)
                    }
                }
            }

            if (isForge) {
                programArgs("-mixin.config", "yacl-test.mixins.json")
            }
        }
    }

    if (isForge) {
        forge {
            convertAccessWideners.set(true)
            mixinConfigs("yacl.mixins.json")
        }
    }

    createRemapConfigurations(testmod)
}

repositories {
    mavenCentral()
    maven("https://maven.terraformersmc.com")
    maven("https://maven.isxander.dev/releases")
    maven("https://maven.isxander.dev/snapshots")
    maven("https://maven.quiltmc.org/repository/release")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") }
        filter { includeGroup("maven.modrinth") }
    }
    exclusiveContent {
        forRepository { maven("https://thedarkcolour.github.io/KotlinForForge/") }
        filter { includeGroup("thedarkcolour") }
    }
    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    fun Dependency?.jij() = this?.let(::include)
    fun Dependency?.forgeRuntime() = this?.takeIf { isForgeLike }?.let { "forgeRuntimeLibrary"(it) }

    minecraft("com.mojang:minecraft:$mcVersion")

    mappings(loom.layered {
        optionalProp("deps.quiltMappings") {
            mappings("org.quiltmc:quilt-mappings:$mcVersion+build.$it:intermediary-v2")
        }
        officialMojangMappings()
    })

    if (isFabric) {
        modImplementation("net.fabricmc:fabric-loader:${findProperty("deps.fabricLoader")}")

        val fapiVersion = property("deps.fabricApi").toString()
        listOf(
            "fabric-resource-loader-v0",
        ).forEach {
            modImplementation(fabricApi.module(it, fapiVersion))
        }
        modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:$fapiVersion")

        modImplementation("net.fabricmc:fabric-language-kotlin:${findProperty("deps.fabricLangKotlin")}")
    }
    if (isNeoforge) {
        "neoForge"("net.neoforged:neoforge:${findProperty("deps.neoforge")}")

        modImplementation("thedarkcolour:kotlinforforge-neoforge:${findProperty("deps.kotlinForForge")}")
    }
    if (isForge) {
        "forge"("net.minecraftforge:forge:${findProperty("deps.forge")}")

        modImplementation("thedarkcolour:kotlinforforge:${findProperty("deps.kotlinForForge")}")

        // enable when it's needed
//        val mixinExtras = findProperty("deps.mixinExtras")
//        compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:$mixinExtras")!!)
//        api("io.github.llamalad7:mixinextras-forge:$mixinExtras").jij()
    }

    listOf(
        "imageio:imageio-core",
        "imageio:imageio-webp",
        "imageio:imageio-metadata",
        "common:common-lang",
        "common:common-io",
        "common:common-image"
    ).forEach {
        implementation("com.twelvemonkeys.$it:${findProperty("deps.imageio")}").jij().forgeRuntime()
    }

    listOf(
        "json",
        "gson"
    ).forEach {
        implementation("org.quiltmc.parsers:$it:${findProperty("deps.quiltParsers")}").jij().forgeRuntime()
    }

    "testmodImplementation"(sourceSets.main.get().output)
}

java {
    withSourcesJar()
}

tasks {
    processResources {
        val props = buildMap {
            put("id", findProperty("modId"))
            put("group", project.group)
            put("name", findProperty("modName"))
            put("description", findProperty("modDescription"))
            put("version", project.version)
            put("github", findProperty("githubProject"))

            if (isFabric) {
                put("mc", findProperty("fmj.mcDep"))
            }

            if (isForgeLike) {
                put("mc", findProperty("modstoml.mcDep"))
                put("loaderVersion", findProperty("modstoml.loaderVersion"))
                put("forgeId", findProperty("modstoml.forgeId"))
                put("forgeConstraint", findProperty("modstoml.forgeConstraint"))
            }
        }

        props.forEach(inputs::property)

        if (isFabric) {
            filesMatching("fabric.mod.json") { expand(props) }
            exclude(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml"))
        }
        if (isForgeLike) {
            filesMatching(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml")) { expand(props) }
            exclude("fabric.mod.json")
        }
    }

    val releaseMod by registering {
        group = "mod"

        dependsOn("publishMods")
        dependsOn("publish")
    }

    withType<JavaCompile> {
        options.release.set(findProperty("java.version")!!.toString().toInt())
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = findProperty("java.version")!!.toString()
        }
    }

    remapJar {
        if (isNeoforge) {
            atAccessWideners.add(accessWidenerName)
        }
    }
}

publishMods {
    displayName.set("YetAnotherConfigLib $versionWithoutMC for MC $mcVersion")
    file.set(tasks.remapJar.get().archiveFile)
    changelog.set(
        rootProject.file("changelog.md")
            .takeIf { it.exists() }
            ?.readText()
            ?: "No changelog provided."
    )
    type.set(when {
        isAlpha -> ALPHA
        isBeta -> BETA
        else -> STABLE
    })
    modLoaders.add(loader)

    fun versionList(prop: String) = findProperty(prop)?.toString()
        ?.split(',')
        ?.map { it.trim() }
        ?: emptyList()

    // modrinth and curseforge use different formats for snapshots. this can be expressed globally
    val stableMCVersions = versionList("pub.stableMC")

    val modrinthId: String by project
    if (modrinthId.isNotBlank() && hasProperty("modrinth.token")) {
        modrinth {
            projectId.set(modrinthId)
            accessToken.set(findProperty("modrinth.token")?.toString())
            minecraftVersions.addAll(stableMCVersions)
            minecraftVersions.addAll(versionList("pub.modrinthMC"))

            requires { slug.set("fabric-api") }
        }
    }

    val curseforgeId: String by project
    if (curseforgeId.isNotBlank() && hasProperty("curseforge.token")) {
        curseforge {
            projectId.set(curseforgeId)
            accessToken.set(findProperty("curseforge.token")?.toString())
            minecraftVersions.addAll(stableMCVersions)
            minecraftVersions.addAll(versionList("pub.curseforgeMC"))

            requires { slug.set("fabric-api") }
        }
    }

    val githubProject: String by project
    if (githubProject.isNotBlank() && hasProperty("github.token")) {
        github {
            repository.set(githubProject)
            accessToken.set(findProperty("github.token")?.toString())
            commitish.set(grgit.branch.current().name)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mod") {
            groupId = "dev.isxander"
            artifactId = "yet-another-config-lib"

            from(components["java"])
        }
    }

    repositories {
        val username = "XANDER_MAVEN_USER".let { System.getenv(it) ?: findProperty(it) }?.toString()
        val password = "XANDER_MAVEN_PASS".let { System.getenv(it) ?: findProperty(it) }?.toString()
        if (username != null && password != null) {
            maven(url = "https://maven.isxander.dev/releases") {
                name = "XanderReleases"
                credentials {
                    this.username = username
                    this.password = password
                }
            }

            maven(url = "https://maven.isxander.dev/snapshots") {
                name = "XanderSnapshots"
                credentials {
                    this.username = username
                    this.password = password
                }
            }
        } else {
            println("Xander Maven credentials not satisfied.")
        }
    }
}

fun <T> optionalProp(property: String, block: (String) -> T?): T? =
    findProperty(property)?.toString()?.takeUnless { it.isBlank() }?.let(block)

fun isPropDefined(property: String): Boolean {
    return property(property)?.toString()?.isNotBlank() ?: false
}
