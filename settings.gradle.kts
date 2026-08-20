rootProject.name = "surf-parkour"

include("surf-parkour-api")
include("surf-parkour-core:surf-parkour-core-common")
include("surf-parkour-core:surf-parkour-core-client")
include("surf-parkour-paper")
include("surf-parkour-minestom")
include("surf-parkour-microservice")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://reposilite.slne.dev/releases")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.slne.surf.api.gradle.settings") version "+"
}
