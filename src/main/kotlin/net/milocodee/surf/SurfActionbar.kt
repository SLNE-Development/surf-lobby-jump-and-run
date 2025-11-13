package net.milocodee.surf

import net.milocodee.surf.listener.ParkourListener
import org.bukkit.plugin.java.JavaPlugin

class ActionbarPlugin : JavaPlugin() {

    private lateinit var actionbarDisplay: ActionbarDisplay
    private lateinit var parkourListener: ParkourListener

    override fun onEnable() {
        actionbarDisplay = ActionbarDisplay(this)
        ActionbarDisplay.initialize(actionbarDisplay)

        parkourListener = ParkourListener(actionbarDisplay)
        server.pluginManager.registerEvents(parkourListener, this)

        logger.info("ActionbarPlugin enabled!")
    }

    override fun onDisable() {
        if (this::actionbarDisplay.isInitialized) {
            actionbarDisplay.cleanup()
        }

        logger.info("ActionbarPlugin disabled!")
    }
}
