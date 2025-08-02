plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.parkour.BukkitMain")

    authors.add("red")

    foliaSupported(true)
    generateLibraryLoader(false)
}

dependencies {
    api(project(":surf-parkour-api"))
}

tasks.shadowJar {
    archiveFileName = "surf-parkour-addon-rewards-${project.version}.jar"
}

