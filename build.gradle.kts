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

dependencies {
    compileOnly(libs.polar.api)
    compileOnly(files("libs/vulcan-api-v1.jar"))
}