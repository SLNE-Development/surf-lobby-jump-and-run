plugins {
    kotlin("jvm") version "1.9.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "net.milocodee"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.slne.dev/repository/maven-public/")
}


dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib"))
    compileOnly("dev.slne.surf:surf-api:1.21.8")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
}

kotlin {
    jvmToolchain(24)
}

tasks {
    shadowJar {
        archiveClassifier.set("")

        // Relocate Caffeine to avoid conflicts
        relocate("com.github.benmanes.caffeine", "net.milocodee.surf.libs.caffeine")

        dependencies {
            include(dependency("com.github.ben-manes.caffeine:caffeine"))
        }
    }

    build {
        dependsOn(shadowJar)
    }

    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}