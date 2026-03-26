import dev.slne.surf.microservice.gradle.plugin.rabbit.RabbitModule

plugins {
    id("dev.slne.surf.surfapi.gradle.standalone")
    id("dev.slne.surf.microservice")
}

dependencies {
    api(projects.surfParkourCore.surfParkourCoreCommon)
}

surfStandaloneApi {
    withSurfDatabaseR2dbc("1.3.0", "dev.slne.surf.parkour.libs.database")
}

surfMicroservice {
    withMicroserviceApi()
    withRabbitModule(RabbitModule.SERVER_API, true)
}