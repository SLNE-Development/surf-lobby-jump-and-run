import dev.slne.surf.surfapi.gradle.util.registerRequired
import dev.slne.surf.surfapi.gradle.util.registerSoft

plugins {
    id("io.freefair.lombok") version "8.11"
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

group = "dev.slne"
version = "1.21.4-2.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("me.clip:placeholderapi:2.11.6")

    implementation("dev.hsbrysk:caffeine-coroutines:2.0.0")

    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.21.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.21.0")

    implementation("org.jetbrains.exposed:exposed-core:0.59.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.59.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.59.0")

    implementation("fr.skytasul:glowingentities:1.4.3")

    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.19")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.parkour.SurfParkour")

    generateLibraryLoader(false)

    authors.add("SLNE Development")
    authors.add("Jo_field (Extern)")

    serverDependencies {
        registerRequired("CommandAPI")
        registerSoft("PlaceholderAPI")
    }

    runServer {
        minecraftVersion("1.21.4")

        downloadPlugins {
            modrinth("CommandAPI", "9.7.0")
            modrinth("PlaceholderAPI", "2.11.6")
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    shadowJar {
        archiveFileName.set("surf-parkour-${version}.jar")
    }
}
