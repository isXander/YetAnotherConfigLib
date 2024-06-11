plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.6.+" apply false

    kotlin("jvm") version "1.9.23" apply false

    id("me.modmuss50.mod-publish-plugin") version "0.5.+" apply false
    id("org.ajoberstar.grgit") version "5.0.+" apply false
}
stonecutter active "1.20.6-fabric" /* [SC] DO NOT EDIT */

stonecutter.configureEach {
    val platform = project.property("loom.platform")

    fun String.propDefined() = project.findProperty(this)?.toString()?.isNotBlank() ?: false
    consts(listOf(
        "fabric" to (platform == "fabric"),
        "forge" to (platform == "forge"),
        "neoforge" to (platform == "neoforge"),
        "forge-like" to (platform == "forge" || platform == "neoforge"),
        "controlify" to "deps.controlify".propDefined(),
        "mod-menu" to "deps.modMenu".propDefined(),
    ))
}

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "mod"
    ofTask("build")
}

stonecutter registerChiseled tasks.register("chiseledReleaseMod", stonecutter.chiseled) {
    group = "mod"
    ofTask("releaseMod")
}

stonecutter registerChiseled tasks.register("chiseledPublishSnapshots", stonecutter.chiseled) {
    group = "mod"
    ofTask("publishAllPublicationsToXanderSnapshotsRepository")
}
