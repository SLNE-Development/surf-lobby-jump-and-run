package dev.slne.surf.parkour.core.client.config

object ParkourConfiguration {
    fun edit(actions: ParkourConfig.() -> Unit) {
        ParkourConfig.getConfig().apply(actions)
        ParkourConfig.save()
    }

    fun reload() {
        ParkourConfig.reloadFromFile()
    }

    val config get() = ParkourConfig.getConfig()
}
