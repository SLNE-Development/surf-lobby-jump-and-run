import dev.slne.surf.surfapi.gradle.util.registerRequired
import dev.slne.surf.surfapi.gradle.util.registerSoft

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

group = "dev.slne.surf.parkour"
version = "1.21.4-2.0.0-SNAPSHOT"

dependencies {
    api(libs.surf.database)
    paperLibrary(libs.glowingentities)
    paperLibrary(libs.packetevents.spigot)

    compileOnly(files("libs/VulcanAPI.jar"))
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.parkour.SurfParkour")

    authors.add("red")
    authors.add("Jo_field (Extern)")

    serverDependencies {
        registerRequired("packetevents")
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
