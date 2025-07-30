import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("dev.kikugie.stonecutter")
    id("dev.isxander.modstitch.base")

    kotlin("jvm")

    id("me.modmuss50.mod-publish-plugin")
    `maven-publish`

    id("org.ajoberstar.grgit")
}

// version stuff
val mcVersion = property("mcVersion")!!.toString()
val mcSemverVersion = stonecutter.current.version
val versionWithoutMC = property("modVersion")!!.toString()

val isAlpha = "alpha" in versionWithoutMC
val isBeta = "beta" in versionWithoutMC

// loader stuff
val isFabric = modstitch.isLoom
val isNeoforge = modstitch.isModDevGradleRegular
val isForge = modstitch.isModDevGradleLegacy
val isForgeLike = modstitch.isModDevGradle
val loader = when {
    isFabric -> "fabric"
    isNeoforge -> "neoforge"
    isForge -> "forge"
    else -> error("Unknown loader")
}

val snapshotVer = "${grgit.branch.current().name.replace('/', '.')}-SNAPSHOT"
if (System.getenv().containsKey("GITHUB_ACTIONS")) {
    version = "$version+$snapshotVer"
}

val testmod by sourceSets.registering {
    compileClasspath += sourceSets.main.get().compileClasspath
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
}

modstitch {
    minecraftVersion = mcVersion
    javaVersion = 21

    parchment {
        prop("parchment.version") { mappingsVersion = it }
        prop("parchment.minecraft") { minecraftVersion = it }
    }

    metadata {
        fun prop(property: String, block: (String) -> Unit) {
            prop(property, ifNull = {""}) { block(it) }
        }

        prop("modId") { modId = it }
        prop("modName") { modName = it }
        modVersion = if (System.getenv().containsKey("GITHUB_ACTIONS")) {
            "$versionWithoutMC+$snapshotVer"
        } else {
            "$versionWithoutMC+${stonecutter.current.project}"
        }
        modGroup = "dev.isxander"
        prop("modDescription") { modDescription = it }
        modLicense = "LGPL-3.0-or-later"
        modAuthor = "isXander"

        prop("githubProject") { replacementProperties.put("github", it) }
        prop("meta.mcDep") { replacementProperties.put("mc", it) }
        prop("meta.loaderDep") { replacementProperties.put("loaderVersion", it) }
        prop("deps.fabricApi") { replacementProperties.put("fapi", it) }
    }

    loom {
        prop("deps.fabricLoader", required = true) { fabricLoaderVersion = it }

        configureLoom {
            runConfigs.all {
                ideConfigGenerated(false)
            }
            runs {
                register("testmodClient") {
                    client()
                    name = "Testmod Client"
                    source(testmod.name)
                    ideConfigGenerated(true)
                    runDir("../../run")
                }
            }

            mixin.useLegacyMixinAp = false
        }
    }

    moddevgradle {
        prop("deps.neoforge") { neoForgeVersion = it }
        prop("deps.forge") { forgeVersion = it }

        configureNeoForge {
            runs {
                register("testmodClient") {
                    client()
                    sourceSet = testmod
                    gameDirectory = layout.projectDirectory.dir("../../run")
                }
            }

            mods {
                register("testmod") {
                    sourceSet(testmod.get())
                }
                register("main") {
                    sourceSet(sourceSets.main.get())
                }
            }
        }
    }

    mixin {
        addMixinsToModManifest = true

        configs.register("yacl")
        if (isFabric) configs.register("yacl-fabric")
    }

    createProxyConfigurations(testmod.get())
}

stonecutter {
    constants {
        put("fabric", modstitch.isLoom)
        put("neoforge", modstitch.isModDevGradleRegular)
        put("forge", modstitch.isModDevGradleLegacy)
        put("forgelike", modstitch.isModDevGradle)
    }

    dependencies {
        put("fapi", (findProperty("deps.fabricApi")?.toString() ?: "0.0.0"))
    }
}

repositories {
    exclusiveContent {
        forRepository { mavenLocal() }
        filter {
            includeGroup("net.fabricmc.fabric-api")
        }
    }
}

dependencies {
    fun Dependency?.jij() = this?.also(::modstitchJiJ)

    prop("deps.mixinExtras") {
        when {
            isFabric -> modstitchImplementation("io.github.llamalad7:mixinextras-fabric:$it").jij()
            isNeoforge -> implementation("io.github.llamalad7:mixinextras-neoforge:$it").jij()
            isForge -> {
                compileOnly("io.github.llamalad7:mixinextras-common:$it")
                implementation("io.github.llamalad7:mixinextras-forge:$it").jij()
            }
            else -> error("Unknown loader")
        }
    }

    fun modDependency(
        id: String,
        artifactGetter: (String) -> String,
        requiredByDependants: Boolean = false,
        supportsRuntime: Boolean = true,
        extra: (Boolean) -> Unit = {}
    ) {
        prop("deps.$id") { modVersion ->
            val noRuntime = prop("deps.$id.noRuntime") { it.toBoolean() } == true
            require(noRuntime || supportsRuntime) { "No runtime is not supported for $id" }

            val configuration = if (requiredByDependants) {
                if (noRuntime) "modstitchModCompileOnlyApi" else "modstitchModApi"
            } else {
                if (noRuntime) "modstitchModCompileOnly" else "modstitchModImplementation"
            }

            configuration(artifactGetter(modVersion))

            extra(!noRuntime)
        }
    }

    if (isFabric) {
        // Fabric API has not been released for 25w31a yet.
        //modDependency("fabricApi", { "net.fabricmc.fabric-api:fabric-api:$it" }, requiredByDependants = true)
        modstitchModImplementation("net.fabricmc.fabric-api:fabric-api-base:0.4.64+local").jij()
        modstitchModImplementation("net.fabricmc.fabric-api:fabric-resource-loader-v0:3.1.11+local").jij()

        modDependency("fabricLangKotlin", { "net.fabricmc:fabric-language-kotlin:${it}" })
    }
    if (isNeoforge) {
        modstitchModRuntimeOnly("thedarkcolour:kotlinforforge-neoforge:${findProperty("deps.kotlinForForge")}")
    }
    if (isForge) {
        modstitchModRuntimeOnly("thedarkcolour:kotlinforforge:${findProperty("deps.kotlinForForge")}")

        compileOnly("org.jetbrains:annotations:20.1.0")
    }

    listOf(
        "imageio:imageio-core",
        "imageio:imageio-webp",
        "imageio:imageio-metadata",
        "common:common-lang",
        "common:common-io",
        "common:common-image",
    ).forEach {
        modstitchApi("com.twelvemonkeys.$it:${findProperty("deps.imageio")}")
            .jij()
    }

    listOf(
        "json",
        "gson"
    ).forEach {
        modstitchApi("org.quiltmc.parsers:$it:${findProperty("deps.quiltParsers")}")
            .jij()
    }

    "testmodImplementation"(sourceSets.main.get().output)
}

val releaseModVersion by tasks.registering {
    group = "yacl/versioned"

    dependsOn("publishMods")

    if (!project.publishMods.dryRun.get()) {
        dependsOn("publishModPublicationToXanderReleasesRepository")
    }
}
createActiveTask(releaseModVersion)

val finalJarTasks = listOf(
    modstitch.finalJarTask
)
val buildAndCollect by tasks.registering(Copy::class) {
    group = "yacl/versioned"

    finalJarTasks.forEach { jar ->
        dependsOn(jar)
        from(jar.flatMap { it.archiveFile })
    }

    into(rootProject.layout.buildDirectory.dir("finalJars"))
}
createActiveTask(buildAndCollect)

publishMods {
    dryRun.set(false)

    displayName.set("$versionWithoutMC for $loader $mcVersion")

    file = modstitch.finalJarTask.flatMap { it.archiveFile }

    fun versionList(prop: String) = findProperty(prop)?.toString()
        ?.split(',')
        ?.map { it.trim() }
        ?: emptyList()

    // modrinth and curseforge use different formats for snapshots. this can be expressed globally
    val stableMCVersions = versionList("pub.stableMC")

    changelog = rootProject.file("changelog.md").readText()
    type = when {
        isAlpha -> ALPHA
        isBeta -> BETA
        else -> STABLE
    }

    modLoaders.add(loader)

    val modrinthId: String by project
    if (modrinthId.isNotBlank() && hasProperty("modrinth.token")) {
        modrinth {
            projectId.set(modrinthId)
            accessToken.set(findProperty("modrinth.token")?.toString())
            minecraftVersions.addAll(stableMCVersions)
            minecraftVersions.addAll(versionList("pub.modrinthMC"))

            announcementTitle = "Download $mcVersion for ${loader.replaceFirstChar { it.uppercase() }} from Modrinth"

            if (isFabric) {
                requires { slug.set("fabric-api") }
            }
        }
    }

    val curseforgeId: String by project
    if (curseforgeId.isNotBlank() && hasProperty("curseforge.token")) {
        curseforge {
            projectId = curseforgeId
            projectSlug = findProperty("curseforgeSlug")?.toString() ?: error("curseforgeSlug property not found")
            accessToken = findProperty("curseforge.token")?.toString()
            minecraftVersions.addAll(stableMCVersions)
            minecraftVersions.addAll(versionList("pub.curseMC"))

            announcementTitle = "Download $mcVersion for ${loader.replaceFirstChar { it.uppercase() }} from CurseForge"

            if (isFabric) {
                requires { slug.set("fabric-api") }
            }
        }
    }
}
publishing {
    publications {
        register<MavenPublication>("mod") {
            groupId = "dev.isxander"
            artifactId = "yet-another-config-lib"
            version = modstitch.metadata.modVersion.get()

            from(components["java"])
        }
    }

    repositories {
        val username = prop("XANDER_MAVEN_USER") { it }
        val password = prop("XANDER_MAVEN_PASS") { it }
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

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget = modstitch.javaVersion.map { JvmTarget.fromTarget(it.toString()) }
        }

        dependsOn("stonecutterGenerate")
    }
}

tasks.named("generateModMetadata") {
    dependsOn("stonecutterGenerate")
}
modstitch.moddevgradle {
    modstitch.onEnable {
        tasks.named("createMinecraftArtifacts") {
            dependsOn("stonecutterGenerate")
        }
    }
}

fun <T> prop(property: String, required: Boolean = false, ifNull: () -> String? = { null }, block: (String) -> T?): T? {
    return ((System.getenv(property) ?: findProperty(property)?.toString())
        ?.takeUnless { it.isBlank() }
        ?: ifNull())
        .let { if (required && it == null) error("Property $property is required") else it }
        ?.let(block)
}

fun createActiveTask(
    taskProvider: TaskProvider<*>? = null,
    taskName: String? = null,
    internal: Boolean = false
): String {
    val taskExists = taskProvider != null || taskName!! in tasks.names
    val task = taskProvider ?: taskName?.takeIf { taskExists }?.let { tasks.named(it) }
    val taskName = when {
        taskProvider != null -> taskProvider.name
        taskName != null -> taskName
        else -> error("Either taskProvider or taskName must be provided")
    }
    val activeTaskName = "${taskName}Active"

    if (stonecutter.current.isActive) {
        rootProject.tasks.register(activeTaskName) {
            group = "yacl${if (internal) "/versioned" else ""}"

            task?.let { dependsOn(it) }
        }
    }

    return activeTaskName
}
