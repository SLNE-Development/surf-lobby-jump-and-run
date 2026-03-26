rootProject.name = "surf-parkour"

include("surf-parkour-paper")
include("surf-parkour-api")
include("surf-parkour-core:surf-parkour-core-common")
include("surf-parkour-core:surf-parkour-core-paper")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.slne.dev/repository/maven-public/") { name = "maven-public" }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.slne.surf.surfapi.gradle.settings") version "1.21.11+"
}
include("surf-parkour-microservice")