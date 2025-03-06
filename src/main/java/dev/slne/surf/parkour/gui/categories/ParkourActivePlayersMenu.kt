package dev.slne.surf.parkour.gui.categories

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import dev.slne.surf.parkour.instance
import dev.slne.surf.parkour.util.MessageBuilder
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player

class ParkourActivePlayersMenu (player: Player) : ChestGui(
    5,
    ComponentHolder.of(MessageBuilder().primary("ᴀᴋᴛɪᴠᴇ sᴘɪᴇʟᴇʀ").build().decorate(TextDecoration.BOLD))
) {
    init {
        instance.launch {
            //objectlist
            val items = ObjectArrayList<GuiItem>()
            //panes


            show(player)
        }
    }
}