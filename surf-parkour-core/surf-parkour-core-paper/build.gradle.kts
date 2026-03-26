import dev.slne.surf.microservice.gradle.plugin.rabbit.RabbitModule

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-raw")
    id("dev.slne.surf.microservice")
}

surfRawPaperApi {
    withCoreCommon()
}

surfMicroservice {
    withRabbitModule(RabbitModule.CLIENT_API)
}

dependencies {
    api(projects.surfParkourCore.surfParkourCoreCommon)
}