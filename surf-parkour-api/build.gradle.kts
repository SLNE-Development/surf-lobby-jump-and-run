import dev.slne.surf.api.gradle.util.slneReleases

plugins {
    id("dev.slne.surf.api.gradle.core")
}

publishing {
    repositories {
        slneReleases()
    }
}