import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    java
    publishing

    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("com.gradleup.shadow") version "9.0.0-beta4"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("org.hibernate.build.maven-repo-auth") version "3.0.4"

    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"

    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    kotlin("plugin.serialization") version "2.1.0"
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

group = "dev.slne"
version = "1.21.1-1.0.0-SNAPSHOT"

repositories {
    gradlePluginPortal()
    mavenCentral()

    maven("https://repo.slne.dev/repository/maven-unsafe/") { name = "maven-unsafe" }
    maven("https://repo.papermc.io/repository/maven-public/") { name = "papermc-repo" }

    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("dev.jorel:commandapi-bukkit-core:9.5.2")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.0-SNAPSHOT")
    compileOnly("me.frep:vulcan-api:2.0.0")

    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.17")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.20.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.20.0")

    paperLibrary("org.jetbrains.kotlin:kotlin-reflect")
    paperLibrary("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    paperLibrary("org.springframework.boot:spring-boot-starter-data-jpa")
    paperLibrary("org.mariadb.jdbc:mariadb-java-client:3.5.1")
    paperLibrary("com.github.ben-manes.caffeine:caffeine:3.1.8")
    paperLibrary("dev.hsbrysk:caffeine-coroutines:1.2.0")
    paperLibrary("com.fasterxml.jackson.module:jackson-module-kotlin")

    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
}

paper {
    name = "SurfLobbyJumpAndRun"
    main = "dev.slne.surf.lobby.jar.PluginInstance"
    loader = "dev.slne.surf.lobby.jar.PluginLibrariesLoader"
    apiVersion = "1.21"
    authors = listOf("TheBjoRedCraft", "SLNE Development")
    prefix = "SurfLobbyJumpAndRun"
    version = "1.21.1-1.0.0-SNAPSHOT"
    foliaSupported = false

    generateLibrariesJson = true

    serverDependencies {
        register("CommandAPI") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }

        register("PlaceholderAPI") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }

        register("WorldEdit") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    runServer {
        minecraftVersion("1.21.1")

        downloadPlugins {
            modrinth("CommandAPI", "9.7.0")
            modrinth("FastAsyncWorldEdit", "2.12.3")
            hangar("PlaceholderAPI", "2.11.6")
        }

//        runPaper.folia.registerTask()
    }
    shadowJar {
        archiveClassifier = ""
        mergeServiceFiles()

        relocate(
            "com.github.stefvanschie.inventoryframework",
            "dev.slne.surf.lobby.jar.inventoryframework"
        )
    }
}