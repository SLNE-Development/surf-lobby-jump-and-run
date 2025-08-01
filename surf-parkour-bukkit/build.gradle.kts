plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.parkour.BukkitMain")

    authors.add("red")

    foliaSupported(true)
    generateLibraryLoader(false)
}

