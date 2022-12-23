plugins {
    `minecraft-module`
    `platform-module`
}

architectury {
    fabric()
}

loom {
    project(":common").loom.accessWidenerPath
            .let { if (it.isPresent) accessWidenerPath.set(it) }
}

configurations["developmentFabric"].extendsFrom(configurations["common"])

dependencies {
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)

    shadowCommon(project(path = ":common", configuration = "transformProductionFabric")) { isTransitive = false }
}
