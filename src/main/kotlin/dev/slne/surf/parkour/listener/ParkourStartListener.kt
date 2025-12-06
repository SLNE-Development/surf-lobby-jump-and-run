package dev.slne.surf.parkour.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.parkour.model.parkour.Parkour
import dev.slne.surf.parkour.plugin
import dev.slne.surf.surfapi.core.api.messages.adventure.playSound
import org.bukkit.Sound
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

object ParkourStartListener : Listener {
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player

        if (!event.hasChangedBlock()) {
            return
        }

        val to = event.to
        val toCorrected = to.block.getRelative(BlockFace.DOWN).location

        Parkour.all().forEach {
            if (it.startLocation.blockX == toCorrected.blockX &&
                it.startLocation.blockY == toCorrected.blockY &&
                it.startLocation.blockZ == toCorrected.blockZ &&
                it.world.name == toCorrected.world?.name
            ) {
                plugin.launch {
                    it.start(player.uniqueId)
                    player.playSound(true) {
                        type(Sound.ENTITY_PLAYER_LEVELUP)
                        volume(1.0f)
                        pitch(1.0f)
                    }
                }
            }
        }
    }
}