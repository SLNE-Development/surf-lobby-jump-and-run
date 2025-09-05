plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.parkour.BukkitMain")

    authors.add("red")

    foliaSupported(false)
    generateLibraryLoader(false)
}

dependencies {
    api(project(":surf-parkour-core"))
    compileOnly(libs.polar.api)
    compileOnly(files("libs/VulcanAPI.jar"))
}

