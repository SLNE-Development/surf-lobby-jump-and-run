import dev.slne.surf.surfapi.gradle.util.registerSoft

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.parkour.paper.PaperMain")
    generateLibraryLoader(false)

    authors.add("red")

    serverDependencies {
        registerSoft("WorldEdit")
    }
}

repositories {
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    api(projects.surfParkourCore.surfParkourCorePaper)
    compileOnly(libs.polar.api)
    compileOnly(files("libs/vulcan-api-v1.jar"))
    api(project(":surf-parkour-api"))

    compileOnly(libs.worldedit.core) { isTransitive = false }
    compileOnly(libs.worldedit.bukkit) { isTransitive = false }
}