import dev.slne.surf.surfapi.gradle.util.registerRequired

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

version = findProperty("version") as String
group = "dev.slne.surf.parkour"

surfPaperPluginApi {
    mainClass("dev.slne.surf.parkour.BukkitMain")
    generateLibraryLoader(false)

    authors.add("red")

    serverDependencies {
        registerRequired("FastAsyncWorldEdit")
    }
}

repositories {
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly(libs.polar.api)
    compileOnly(files("libs/vulcan-api-v1.jar"))

    api(libs.surf.database)

    compileOnly(libs.worldedit.core) { isTransitive = false }
    compileOnly(libs.worldedit.bukkit) { isTransitive = false }
}