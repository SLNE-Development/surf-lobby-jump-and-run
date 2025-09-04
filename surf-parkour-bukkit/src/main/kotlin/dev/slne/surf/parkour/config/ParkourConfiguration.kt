package dev.slne.surf.parkour.config

import dev.slne.surf.parkour.plugin
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi

class ParkourConfiguration {
    private val configManager: SpongeConfigManager<ParkourConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            ParkourConfig::class.java,
            plugin.dataPath,
            "config.yml"
        )
        configManager =
            surfConfigApi.getSpongeConfigManagerForConfig(ParkourConfig::class.java)
        reload()
    }

    fun reload() {
        configManager.reloadFromFile()
    }

    val config get() = configManager.config
}