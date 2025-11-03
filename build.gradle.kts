plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.parkour.BukkitMain")
    foliaSupported(true)
    generateLibraryLoader(false)

    authors.add("red")
}