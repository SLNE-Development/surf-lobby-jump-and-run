package dev.slne.surf.parkour.gui.categories

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.database.DatabaseProvider
import dev.slne.surf.parkour.gui.ParkourMenu
import dev.slne.surf.parkour.gui.RedirectType
import dev.slne.surf.parkour.util.ItemBuilder
import dev.slne.surf.parkour.util.MessageBuilder
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player

class ParkourSelectMenu(private val redirect: RedirectType) :
    ChestGui(5, ComponentHolder.of(MessageBuilder().primary("ᴘᴀʀᴋᴏᴜʀ ᴡÄʜʟᴇɴ").build().decorate(TextDecoration.BOLD))) {

    private val outlinePane = StaticPane(0, 0, 9, 5)
    private val pages = PaginatedPane(1, 1, 7, 3)
    private val items = ObjectArrayList<GuiItem>()

    private val outlineItem =
        GuiItem(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(Component.text(" ")).build())
    private val menuButton = GuiItem(
        ItemBuilder(Material.BARRIER).setName(MessageBuilder().primary("Hautmenü").build())
            .addLoreLine(MessageBuilder().info("Klicke, um zum Hautmenü zurückzukehren!").build()).build()
    ) {

        ParkourMenu(it.whoClicked as? Player ?: return@GuiItem)
    }
    private val backButton = GuiItem(
        ItemBuilder(Material.ARROW).setName(MessageBuilder().error("Vorherige Seite").build())
            .addLoreLine(MessageBuilder().info("Klicke, um die Seite zu wechseln!").build()).build()
    ) {
        if (pages.page > 0) {
            pages.page -= 1
            update()
        }
    }

    private val continueButton = GuiItem(
        ItemBuilder(Material.ARROW).setName(MessageBuilder().success("Nächste Seite").build())
            .addLoreLine(MessageBuilder().info("Klicke, um die Seite zu wechseln!").build()).build()
    ) {
        if (pages.page < pages.pages - 1) {
            pages.page += 1
            update()
        }
    }

    init {
        for (parkour in DatabaseProvider.getParkours()) {
            items.add(
                GuiItem(
                    ItemBuilder(Material.COMPASS)
                        .setName(MessageBuilder(parkour.name).build())
                        .addLoreLine(MessageBuilder().info("Klicke, um den Parkour auszuwählen.").build())
                        .build()
                ) {
                    val player = it.whoClicked as? Player ?: return@GuiItem

                    when (redirect) {
                        RedirectType.MAIN -> {
                            ParkourMenu(player)
                        }

                        RedirectType.PARKOUR_ACTIVES -> {
                            if (parkour.activePlayers.isEmpty()) {
                                ParkourGeneralFailureMenu(
                                    player,
                                    MessageBuilder().error("Es sind keine Spieler in diesem Parkour.")
                                )
                                return@GuiItem
                            }
                            ParkourActivePlayersMenu(player, parkour)
                        }

                        RedirectType.START_PARKOUR -> {
                            instance.launch {
                                parkour.startParkour(player)
                            }
                        }
                    }
                }
            )
        }
        pages.populateWithGuiItems(items)

        for (x in 0 until 9) {
            outlinePane.addItem(outlineItem, x, 0)
            if (x == 2 || x == 4 || x == 6) {
                continue
            }
            outlinePane.addItem(outlineItem, x, 4)
        }

        for (y in 1 until 4) {
            outlinePane.addItem(outlineItem, 0, y)
            outlinePane.addItem(outlineItem, 8, y)
        }

        outlinePane.addItem(menuButton, 4, 4)

        addPane(outlinePane)
        addPane(pages)
        
        setOnGlobalClick { event ->
            event.isCancelled = true
                    RedirectType.START_PARKOUR -> {
                        plugin.launch {
                            parkour.startParkour(player)
                        }
                    }
                }
            })
        }
        setOnGlobalDrag { event ->
            event.isCancelled = true
        }
        update()
    }

    override fun update() {

        if (pages.page > 0) {
            outlinePane.addItem(backButton, 2, 4)
        } else {
            outlinePane.addItem(outlineItem, 2, 4)
        }

        if (pages.page < pages.pages - 1) {
            outlinePane.addItem(continueButton, 6, 4)
        } else {
            outlinePane.addItem(outlineItem, 6, 4)
        }
        super.update()
    }
}
