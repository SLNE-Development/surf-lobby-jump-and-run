import dev.slne.surf.surfapi.gradle.util.registerSoft

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.parkour.paper.BukkitMain")
    generateLibraryLoader(false)

    authors.add("red")

    serverDependencies {
        registerSoft("WorldEdit")
        registerSoft("surf-settings-paper")
    }
}

repositories {
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly(libs.polar.api)
    compileOnly(files("libs/vulcan-api-v1.jar"))

    api("dev.slne.surf:surf-database-r2dbc:1.0.0-SNAPSHOT")
    api(project(":surf-parkour-api"))

    compileOnlyApi("dev.slne.surf.settings:surf-settings-api:+")

    compileOnly(libs.worldedit.core) { isTransitive = false }
    compileOnly(libs.worldedit.bukkit) { isTransitive = false }
}