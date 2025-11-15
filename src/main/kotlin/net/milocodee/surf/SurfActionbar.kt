package net.milocodee.surf

import net.milocodee.surf.listener.ParkourListener
import net.milocodee.surf.service.ActionbarService
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(SurfActionbar::class.java)

class SurfActionbar : JavaPlugin() {

    override fun onEnable() {
        ActionbarService.init(this)

        val parkourListener = ParkourListener()
        server.pluginManager.registerEvents(parkourListener, this)

        logger.info("ActionbarPlugin enabled")
    }

    override fun onDisable() {
        ActionbarService.shutdown()
        logger.info("ActionbarPlugin disabled")
    }
}
