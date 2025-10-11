buildscript {
    repositories {
        gradlePluginPortal()
        maven("https://repo.slne.dev/repository/maven-public/") { name = "maven-public" }
    }
    dependencies {
        classpath("dev.slne.surf:surf-api-gradle-plugin:1.21.10+")
    }
}

allprojects {
    group = "dev.slne.surf.parkour"
    version = "1.21.10-2.0.0-SNAPSHOT"
}