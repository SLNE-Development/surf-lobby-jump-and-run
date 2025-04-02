package dev.slne.surf.parkour


import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.registerSuspendingEvents
import dev.slne.surf.parkour.command.ParkourCommand
import dev.slne.surf.parkour.command.subcommand.ParkourStatsCommand
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.listener.PlayerConnectionListener
import dev.slne.surf.parkour.listener.PlayerInteractListener
import dev.slne.surf.parkour.listener.PlayerMoveListener
import dev.slne.surf.parkour.listener.PlayerParkourListener
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import fr.skytasul.glowingentities.GlowingBlocks
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(SurfParkour::class.java)

class SurfParkour : SuspendingJavaPlugin() {

    lateinit var blockApi: GlowingBlocks

    override suspend fun onEnableAsync() {
        this.saveDefaultConfig()
        this.blockApi = GlowingBlocks(this)

        ParkourCommand("parkour").register()
        ParkourStatsCommand("stats").register()

        Bukkit.getPluginManager().registerEvents(PlayerParkourListener(), this)
        Bukkit.getPluginManager().registerEvents(PlayerConnectionListener(), this)
        Bukkit.getPluginManager().registerEvents(PlayerInteractListener(), this)
        //Bukkit.getPluginManager().registerEvents(PlayerMoveListener(), this) The Start Location is currently not implemented, so the listener is not needed

        DatabaseProvider.connect()
        DatabaseProvider.fetchParkours()

    }

    override suspend fun onDisableAsync() {
        DatabaseProvider.saveParkours()
        DatabaseProvider.savePlayers()
    }

    companion object {
        val clickItem = buildItem(Material.FIREWORK_ROCKET) {
            displayName(text("Jump'n Run"))

            buildLore {
                +Component.empty()
                +text("Parkour Informationen", Colors.INFO)

                line {
                    spacer("   - ")
                    info("Parkour starten")
                }

                line {
                    spacer("   - ")
                    info("Leaderboard ansehen")
                }

                line {
                    spacer("   - ")
                    info("Einstellungen verwalten")
                }
            }
        }
    }
}

inline fun Audience.send(message: SurfComponentBuilder.() -> Unit) {
    sendMessage(buildText {
        appendPrefix()
        message()
    })
}
