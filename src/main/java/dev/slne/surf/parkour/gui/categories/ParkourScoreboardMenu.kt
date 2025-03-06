package dev.slne.surf.parkour.gui.categories

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.parkour.instance
import dev.slne.surf.parkour.util.MessageBuilder
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player

class ParkourScoreboardMenu (player: Player) : ChestGui(
    5,
    ComponentHolder.of(MessageBuilder().primary("ʙᴇsᴛᴇɴʟɪsᴛᴇ").build().decorate(TextDecoration.BOLD))
) {
    init {
        instance.launch {
            //objectlist
            val parkouScoreboardList = ObjectArrayList<GuiItem>()
            //panes
            val outlinePane = StaticPane(0, 0, 9, 5)
            val pages = PaginatedPane(1, 1, 7, 3)

            setOnGlobalClick { it.isCancelled = true }
            setOnGlobalDrag { it.isCancelled = true }

            addPane(outlinePane)
            addPane(pages)

            show(player)
        }
    }
}