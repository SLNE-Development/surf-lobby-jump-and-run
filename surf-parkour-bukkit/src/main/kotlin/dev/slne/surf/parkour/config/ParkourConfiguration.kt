package dev.slne.surf.parkour.config

import dev.slne.surf.parkour.plugin
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi

class ParkourConfiguration {
    private val configManager: SpongeConfigManager<GeneralParkourConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            GeneralParkourConfig::class.java,
            plugin.dataPath,
            "general.yml"
        )
        configManager =
            surfConfigApi.getSpongeConfigManagerForConfig(GeneralParkourConfig::class.java)
        reload()
    }

    fun reload() {
        configManager.reloadFromFile()
    }

    val config get() = configManager.config
}