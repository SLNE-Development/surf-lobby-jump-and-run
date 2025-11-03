plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

version = findProperty("version") as String
group = "dev.slne.surf.parkour"

surfPaperPluginApi {
    mainClass("dev.slne.surf.parkour.BukkitMain")
    foliaSupported(true)
    generateLibraryLoader(false)

    authors.add("red")
}