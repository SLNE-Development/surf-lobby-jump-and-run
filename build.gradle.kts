import dev.slne.surf.surfapi.gradle.util.registerRequired
import dev.slne.surf.surfapi.gradle.util.registerSoft

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

group = "dev.slne.surf.jnr"
version = "1.21.4-2.0.0-SNAPSHOT"

dependencies {
    implementation(libs.bundles.exposed) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib")
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
        exclude("org.jetbrains.kotlinx", "kotlinx-coroutines-core")
        exclude("org.slf4j", "slf4j-api")
    }

    api(libs.surf.database)

    paperLibrary(libs.glowingentities)
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.parkour.SurfParkour")

    authors.add("SLNE Development")
    authors.add("Jo_field (Extern)")

    serverDependencies {
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

kotlin {
    jvmToolchain(21)
}

tasks {
    shadowJar {
        archiveFileName.set("surf-parkour-${version}.jar")
    }
}
