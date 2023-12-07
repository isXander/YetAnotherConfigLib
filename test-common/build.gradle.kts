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
        officialMojangMappings()
        parchment(libs.parchment)
    })
    modImplementation(libs.fabric.loader)

    implementation(project(path = ":common", configuration = "namedElements"))
}
