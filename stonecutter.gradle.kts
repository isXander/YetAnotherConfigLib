plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.20.5-fabric" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "mod"
    ofTask("build")
}

stonecutter registerChiseled tasks.register("chiseledPublishMods", stonecutter.chiseled) {
    group = "mod"
    ofTask("releaseMod")
}

stonecutter registerChiseled tasks.register("chiseledPublishSnapshots", stonecutter.chiseled) {
    group = "mod"
    ofTask("publishAllPublicationsToXanderSnapshotsRepository")
}
