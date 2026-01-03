package dev.slne.surf.parkour.hook

import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.service.parkourService
import dev.slne.surf.parkour.util.anyOfType
import dev.slne.surf.surfapi.bukkit.api.event.register
import me.frep.vulcan.api.event.VulcanGhostBlockEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.util.Vector

class VulcanHook : Listener {
    @EventHandler
    fun onPlayerGhostBlock(event: VulcanGhostBlockEvent) {
        if (this.isStandingOnJumpBlock(event.player)) {
            event.isCancelled = true
        }
    }

    fun register() {
        if (!Bukkit.getPluginManager().isPluginEnabled("Vulcan")) {
            return
        }

        this.register(plugin)
    }

    private fun isStandingOnJumpBlock(player: Player): Boolean {
        val playerParkour = parkourService.getParkour(player) ?: return false
        val generator = playerParkour.generators[player.uniqueId] ?: return false

        // Get the blocks below the player's feet (checking corners to handle edge cases)
        val blocksBelow = getBlocksBelowPlayer(player)
        
        return generator.blockLocations
            .anyOfType<Vector> { jumpBlock ->
                blocksBelow.any { blockBelow ->
                    jumpBlock.blockX == blockBelow.blockX &&
                    jumpBlock.blockY == blockBelow.blockY &&
                    jumpBlock.blockZ == blockBelow.blockZ
                }
            }
    }

    private fun getBlocksBelowPlayer(player: Player): List<Vector> {
        val location = player.location
        val boundingBox = player.boundingBox
        
        // Check the 4 corners of the player's bounding box at foot level
        val minX = boundingBox.minX
        val maxX = boundingBox.maxX
        val minZ = boundingBox.minZ
        val maxZ = boundingBox.maxZ
        val footY = boundingBox.minY - 0.1 // Slightly below the feet
        
        val blocks = mutableSetOf<Vector>()
        
        // Check all 4 corners
        blocks.add(Vector(minX, footY, minZ).toBlockLocation())
        blocks.add(Vector(maxX, footY, minZ).toBlockLocation())
        blocks.add(Vector(minX, footY, maxZ).toBlockLocation())
        blocks.add(Vector(maxX, footY, maxZ).toBlockLocation())
        
        return blocks.toList()
    }

    private fun Vector.toBlockLocation(): Vector {
        return Vector(blockX.toDouble(), blockY.toDouble(), blockZ.toDouble())
    }
}