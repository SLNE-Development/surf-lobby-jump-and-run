import dev.slne.surf.api.gradle.util.registerRequired
import dev.slne.surf.api.gradle.util.registerSoft

plugins {
    id("dev.slne.surf.api.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.parkour.paper.PaperMain")
    generateLibraryLoader(false)

    authors.add("red")

    serverDependencies {
        registerSoft("WorldEdit")
        registerRequired("surf-rabbitmq-paper")
    }
}

repositories {
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    api(projects.surfParkourCore.surfParkourCoreClient)
    api(projects.surfParkourApi)

    compileOnly(libs.worldedit.core) { isTransitive = false }
    compileOnly(libs.worldedit.bukkit) { isTransitive = false }
}