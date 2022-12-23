plugins {
    `minecraft-module`
    `platform-module`
}

architectury {
    forge()
}

loom {
    project(":common").loom.accessWidenerPath
            .let { if (it.isPresent) accessWidenerPath.set(it) }

    forge {
        convertAccessWideners.set(true)

        if (accessWidenerPath.isPresent)
            extraAccessWideners.add(accessWidenerPath.get().asFile.name)
    }
}

configurations["developmentForge"].extendsFrom(configurations["common"])

dependencies {
    forge(libs.forge)

    shadowCommon(project(path = ":common", configuration = "transformProductionForge")) { isTransitive = false }
}
