package dev.slne.surf.parkour.paper.config

import dev.slne.surf.parkour.paper.plugin
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi

class ParkourConfiguration {
    val configManager: SpongeConfigManager<ParkourConfig>

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

    fun edit(actions: ParkourConfig.() -> Unit) {
        configManager.config = configManager.config.apply { actions() }
        configManager.save()
    }

    fun reload() {
        configManager.reloadFromFile()
    }

    val config get() = configManager.config
}