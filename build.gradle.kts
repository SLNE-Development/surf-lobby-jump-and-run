import de.schablinski.gradle.activejdbc.ActiveJDBCInstrumentation
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    java
    publishing

    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("com.gradleup.shadow") version "9.0.0-beta4"
    id("io.freefair.lombok") version "8.11"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("de.schablinski.activejdbc-gradle-plugin") version "2.0.1"
    id("org.hibernate.build.maven-repo-auth") version "3.0.4"

    kotlin("jvm") version "2.1.0"
    kotlin("plugin.noarg") version "2.1.0"
}

group = "dev.slne"
version = "1.21.1-1.0.0-SNAPSHOT"

repositories {
    gradlePluginPortal()
    mavenCentral()

    maven ("https://jitpack.io")

    maven("https://repo.slne.dev/repository/maven-unsafe/") { name = "maven-unsafe" }
    maven("https://repo.papermc.io/repository/maven-public/") { name = "papermc-repo" }

    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("dev.jorel:commandapi-bukkit-core:9.5.2")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("me.frep:vulcan-api:2.0.0")

    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.17")
    implementation("dev.hsbrysk:caffeine-coroutines:1.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.20.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.20.0")
    implementation("com.cjcrafter:foliascheduler:0.6.3")

    paperLibrary("com.zaxxer:HikariCP:5.0.1")
    paperLibrary("org.mariadb.jdbc:mariadb-java-client:3.5.1")
    paperLibrary("com.github.ben-manes.caffeine:caffeine:3.1.8")
    paperLibrary("org.javalite:activejdbc:3.4-j11")
    paperLibrary("org.javalite:activejdbc-kt:3.4-j11")

    activejdbc("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")

    runPaper.folia.registerTask()
}

paper {
    name = "SurfLobbyJumpAndRun"
    main = "dev.slne.surf.lobby.jar.PluginInstance"
    loader = "dev.slne.surf.lobby.jar.PluginLibrariesLoader"
    apiVersion = "1.21"
    authors = listOf("TheBjoRedCraft", "SLNE Development")
    prefix = "SurfLobbyJumpAndRun"
    version = "1.21.1-1.0.0-SNAPSHOT"
    foliaSupported = true

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
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

val instrumentKotlinModels by tasks.register<ActiveJDBCInstrumentation>("instrumentKotlinModels") {
    group = "build"

    classesDir = "${layout.buildDirectory}/classes/kotlin/main"
    outputDir = "${layout.buildDirectory}/classes/kotlin/main"
}

tasks {
    runServer {
        minecraftVersion("1.21.4")

        /*
        downloadPlugins {
            modrinth("CommandAPI", "9.7.0")
            modrinth("FastAsyncWorldEdit", "2.12.3")
            modrinth("PlaceholderAPI", "2.11.6")
        }
         */
    }
    shadowJar {
        archiveClassifier = ""
        mergeServiceFiles()

        relocate(
            "com.github.stefvanschie.inventoryframework",
            "dev.slne.surf.lobby.jar.inventoryframework"
        )

        relocate (
            "com.cjcrafter.foliascheduler",
            "dev.slne.surf.lobby.jar.foliascheduler"
        )

        //instrumentKotlinModels.instrument()
        //dependsOn(instrumentKotlinModels)
    }
}