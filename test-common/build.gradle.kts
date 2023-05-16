plugins {
    alias(libs.plugins.architectury.loom)
}

architectury {
    val enabledLoaders = rootProject.properties["loaders"].toString().split(",").map { it.trim() }
    common(enabledLoaders)
}

loom {
    silentMojangMappingsLicense()

    accessWidenerPath.set(project(":common").loom.accessWidenerPath)
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        val qm = libs.versions.quilt.mappings.get()
        if (qm != "0")
            mappings("org.quiltmc:quilt-mappings:${libs.versions.minecraft.get()}+build.${libs.versions.quilt.mappings.get()}:intermediary-v2")
        officialMojangMappings()
    })
    modImplementation(libs.fabric.loader)

    implementation(project(path = ":common", configuration = "namedElements"))
}
