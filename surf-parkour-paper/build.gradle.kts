import dev.slne.surf.surfapi.gradle.util.registerSoft

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.parkour.paper.BukkitMain")
    generateLibraryLoader(false)

    authors.add("red")

    withSurfDatabaseR2dbc("1.3.0", "dev.slne.surf.parkour.paper.database")

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
    api(project(":surf-parkour-api"))

    compileOnly("dev.slne.surf.settings:surf-settings-api:1.21.11-2.0.0-SNAPSHOT")

    compileOnly(libs.worldedit.core) { isTransitive = false }
    compileOnly(libs.worldedit.bukkit) { isTransitive = false }
}