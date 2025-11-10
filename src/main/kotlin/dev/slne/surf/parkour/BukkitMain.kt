package dev.slne.surf.parkour

import com.github.retrooper.packetevents.PacketEvents
import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.database.DatabaseManager
import dev.slne.surf.parkour.command.parkourCommand
import dev.slne.surf.parkour.config.ParkourConfiguration
import dev.slne.surf.parkour.database.ParkourRunsTable
import dev.slne.surf.parkour.database.ParkourTable
import dev.slne.surf.parkour.hook.PolarHook
import dev.slne.surf.parkour.hook.VulcanHook
import dev.slne.surf.parkour.listener.*
import dev.slne.surf.parkour.service.parkourService
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.buildLore
import dev.slne.surf.surfapi.bukkit.api.builder.displayName
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

val plugin get() = JavaPlugin.getPlugin(BukkitMain::class.java)

class BukkitMain : SuspendingJavaPlugin() {
    override fun onEnable() {
        FailureListener().register()
        SuccessListener().register()
        ParkourItemListener().register()

        PolarHook().register()
        VulcanHook().register()

        PacketEvents.getAPI().eventManager.registerListener(ParkourPacketListener())
        PacketEvents.getAPI().eventManager.registerListener(PlayerPacketListener())

        parkourCommand()

        establishDatabaseConnection()
        parkourService.loadParkours()
    }

    private fun establishDatabaseConnection() {
        DatabaseManager(plugin.dataPath, plugin.dataPath).databaseProvider.connect()

        transaction {
            SchemaUtils.create(ParkourTable, ParkourRunsTable)
        }
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
                emptyLine()
                line {
                    text("Endlich ist er da! ")
                }
                line {
                    variableValue("Der Lobby Parkour.")
                }
                line {
                    text("Keine Langeweile beim Warten mehr!")
                }
                emptyLine()
                line {
                    text("Springe so weit wie möglich und stelle neue Rekorde auf!")
                }
                line {
                    text("Klicke mit diesem Item, um ein Menu zu öffnen. Dort")
                }
                line {
                    text("kannst du den Parkour starten, deine Statistiken ansehen")
                }
                line {
                    text("und vieles mehr!")
                }
                if (parkourConfig.config.betaMode) {
                    emptyLine()
                    line {
                        spacer(
                            "Bitte beachte, das der Parkour noch in der Beta-Phase ist.",
                            TextDecoration.ITALIC
                        )
                    }
                }
            }
        }
}

val parkourConfig = ParkourConfiguration()

