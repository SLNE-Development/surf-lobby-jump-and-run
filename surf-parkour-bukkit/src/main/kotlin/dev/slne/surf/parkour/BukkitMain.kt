package dev.slne.surf.parkour

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.parkour.config.ParkourConfiguration
import dev.slne.surf.parkour.core.service.databaseService
import dev.slne.surf.parkour.core.service.parkourService
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.metrics.Metrics
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.Colors
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(BukkitMain::class.java)

lateinit var metrics: Metrics

class BukkitMain : SuspendingJavaPlugin() {
    override fun onLoad() {
        databaseService.connect(plugin.dataPath)
        databaseService.createTables()
    }

    override suspend fun onEnableAsync() {
        BukkitCommandManager.registerCommands()
        BukkitListenerManager.registerBukkitListeners()
        BukkitListenerManager.registerExternalListeners()

        metrics = Metrics(this, 27168)

        parkourService.fetchParkours(parkourConfig.config.serverUuid)
    }

    override fun onDisable() {
        if (::metrics.isInitialized) {
            metrics.shutdown()
        }

        databaseService.disconnect()
    }

    val inventoryItem
        get() = buildItem(Material.FIREWORK_ROCKET) {
            displayName {
                primary("Jump'n Run", TextDecoration.BOLD)
                if (parkourConfig.config.betaMode) {
                    error(" Beta".toSmallCaps())
                }
            }

            addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ATTRIBUTES)

            buildLore {
                +Component.empty()
                +Component.text("Endlich ist er da! ", Colors.WHITE)
                    .append(Component.text("Der Lobby Parkour.", Colors.GOLD))
                +Component.text("Keine langeweile beim warten mehr!", Colors.WHITE)
                +Component.empty()
                +Component.text(
                    "Springe so weit wie möglich und stelle neue Rekorde auf!",
                    Colors.WHITE
                )
                +Component.text("Klicke mit diesem Item, um ein Menu zu öffnen. Dort", Colors.WHITE)
                +Component.text(
                    "kannst du den Parkour starten, deine Statistiken ansehen",
                    Colors.WHITE
                )
                +Component.text("und vieles mehr!", Colors.WHITE)

                if (parkourConfig.config.betaMode) {
                    +Component.empty()
                    +Component.text(
                        "Bitte beachte, das der Parkour noch in der Beta-Phase ist.",
                        Colors.GRAY
                    )
                        .decorate(TextDecoration.ITALIC)
                }
            }
        }

    val parkourConfig = ParkourConfiguration()
}
