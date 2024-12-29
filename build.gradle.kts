import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    java

    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("com.gradleup.shadow") version "9.0.0-beta4"
    id("io.freefair.lombok") version "8.11"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "dev.slne"
version = "1.21.1-1.0.0-SNAPSHOT"

repositories {
    mavenCentral()

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

    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.17")
    paperLibrary("com.zaxxer:HikariCP:5.0.1")
    paperLibrary("mysql:mysql-connector-java:8.0.33")
    paperLibrary("com.github.ben-manes.caffeine:caffeine:3.1.8")
    paperLibrary("org.javalite:activejdbc:3.5-j11")
}


paper {
    name = "SurfLobbyJumpAndRun"
    main = "dev.slne.surf.lobby.jar.PluginInstance"
    loader = "dev.slne.surf.lobby.jar.PluginLibrariesLoader"
    apiVersion = "1.21"
    authors = listOf("TheBjoRedCraft", "SLNE Development")
    prefix = "SurfLobbyJumpAndRun"
    version = "1.21.1-1.0.0-SNAPSHOT"

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
            hangar("CommandAPI", "9.7.0")
            hangar("PlaceholderAPI", "2.11.6")
        }
    }
    shadowJar {
        archiveClassifier = ""

        relocate(
            "com.github.stefvanschie.inventoryframework",
            "dev.slne.surf.lobby.jar.inventoryframework"
        )
    }
}