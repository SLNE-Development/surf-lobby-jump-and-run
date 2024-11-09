import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("java")
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("com.gradleup.shadow") version "8.3.0"
    id ("io.freefair.lombok") version "8.10"
}

group = "dev.slne"
version = "1.21.1-1.0.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }

    maven ("https://repo.codemc.org/repository/maven-public/")
    maven ("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly ("dev.jorel:commandapi-bukkit-core:9.5.2")
    compileOnly ("me.clip:placeholderapi:2.11.6")

    implementation ("com.github.stefvanschie.inventoryframework:IF:0.10.17")
    implementation ("com.zaxxer:HikariCP:5.0.1")
    implementation ("mysql:mysql-connector-java:8.0.33")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
}


paper {
    name = "SurfLobbyJumpAndRun"
    main = "dev.slne.surf.lobby.jar.PluginInstance"
    apiVersion = "1.21"
    authors = listOf("SLNE Development", "TheBjoRedCraft")
    prefix = "SurfLobbyJumpAndRun"
    version = "1.21.1-1.0.0-SNAPSHOT"


    serverDependencies {
        register("CommandAPI") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }

        register("PlaceholderAPI") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
    }
}

tasks.shadowJar {
    archiveClassifier = ""

    relocate("com.github.stefvanschie.inventoryframework", "dev.slne.surf.lobby.jar.inventoryframework")
}