plugins {
    id("dev.slne.surf.surfapi.gradle.paper-raw")
}

dependencies {
    api(project(":surf-parkour-api"))
    runtimeOnly(project(":surf-parkour-fallback"))
}