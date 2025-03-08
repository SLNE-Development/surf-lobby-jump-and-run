import dev.slne.surf.surfapi.gradle.util.registerSoft

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

group = "dev.slne"
version = "1.21.4-2.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation("org.jetbrains.exposed:exposed-core:0.60.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.60.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.60.0")

    implementation("fr.skytasul:glowingentities:1.4.3")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.parkour.SurfParkour")

    generateLibraryLoader(false)

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
