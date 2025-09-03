plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.parkour.BukkitMain")

    authors.add("red")
    authors.add("Jo_field (Extern)")

    foliaSupported(true)
    generateLibraryLoader(false)
}

dependencies {
    api(project(":surf-parkour-core"))
    compileOnly(libs.polar.api)
    compileOnly(files("libs/VulcanAPI.jar"))
}

