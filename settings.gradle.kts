pluginManagement {
    plugins {
        kotlin("jvm") version "2.1.0"
    }
}

buildscript {
    repositories {
        gradlePluginPortal()
        maven("https://repo.slne.dev/repository/maven-public/") { name = "maven-public" }
    }
    dependencies {
        classpath("dev.slne.surf:surf-api-gradle-plugin:1.21.4+")
    }
}

rootProject.name = "surf-lobby-jump-and-run"

