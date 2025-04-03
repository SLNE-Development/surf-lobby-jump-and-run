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
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(SurfParkour::class.java)

class SurfParkour : SuspendingJavaPlugin() {

    lateinit var blockApi: GlowingBlocks

    var betaMode: Boolean = false

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

        betaMode = plugin.config.getBoolean("beta-mode", false)
    }

    override suspend fun onDisableAsync() {
        DatabaseProvider.saveParkours()
        DatabaseProvider.savePlayers()

        plugin.config.set("beta-mode", betaMode)
        plugin.saveConfig()
    }

    fun getInventoryItem(betaMode: Boolean): ItemStack = buildItem(Material.FIREWORK_ROCKET) {
        displayName(buildText {
            primary("Jump'n Run").decorate(TextDecoration.BOLD)
            if (betaMode) {
                error(" ʙᴇᴛᴀ")
            }
        })

        addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ATTRIBUTES)

        buildLore {
            +Component.empty()
            +Component.text("Endlich ist er da! ", Colors.WHITE).append(Component.text("Der Lobby Parkour.", Colors.GOLD))
            +Component.text("Keine langeweile beim warten mehr!", Colors.WHITE)
            +Component.empty()
            +Component.text("Springe so weit wie möglich und stelle neue Rekorde auf!", Colors.WHITE)
            +Component.text("Klicke mit diesem Item, um ein Menu zu öffnen. Dort", Colors.WHITE)
            +Component.text("kannst du den Parkour starten, deine Statistiken ansehen", Colors.WHITE)
            +Component.text("und vieles mehr!", Colors.WHITE)

            if(betaMode) {
                +Component.empty()
                +Component.text(
                    "Bitte beachte, das der Parkour noch in der Beta-Phase ist.",
                    Colors.GRAY
                )
                    .decorate(TextDecoration.ITALIC)
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
