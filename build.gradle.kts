
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    java
    publishing

    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("com.gradleup.shadow") version "9.0.0-beta4"
    id("io.freefair.lombok") version "8.11"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("org.hibernate.build.maven-repo-auth") version "3.0.4"

    kotlin("jvm") version "2.1.0"
    kotlin("plugin.noarg") version "2.1.0"
}

group = "dev.slne"
version = "1.21.4-1.2.0-SNAPSHOT"

val pluginName = "SurfParkour"
val internalPluginName = "surf-parkour"

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
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("dev.jorel:commandapi-bukkit-core:9.5.2")
    compileOnly("me.clip:placeholderapi:2.11.6")

    implementation("dev.hsbrysk:caffeine-coroutines:1.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.21.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.21.0")
    implementation("com.sksamuel.aedile:aedile-core:2.0.3")

    implementation("org.jetbrains.exposed:exposed-core:0.59.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.59.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.59.0")
}

paper {
    name = pluginName
    main = "dev.slne.surf.parkour.SurfParkour"
    apiVersion = "1.21.4"
    authors = listOf("TheBjoRedCraft", "SLNE Development")
    prefix = pluginName
    version = "${project.version}"
    foliaSupported = false

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
        archiveFileName.set("${internalPluginName}-${version}.jar")
    }
}