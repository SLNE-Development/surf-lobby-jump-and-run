package dev.slne.surf.parkour.hook

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.regions.CuboidRegion
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox

object WorldEditHook {
    fun isEnabled() = Bukkit.getPluginManager().isPluginEnabled("WorldEdit")

    fun getSelection(player: Player): BoundingBox? {
        val wePlayer = BukkitAdapter.adapt(player)
        val session = WorldEdit.getInstance().sessionManager.get(wePlayer)
        val selection =
            runCatching { session.getSelection(wePlayer.world) }.getOrNull() ?: return null

        return selection.boundingBox.boundingBox()
    }

    private fun CuboidRegion.boundingBox() = BoundingBox(
        minimumPoint.x().toDouble(),
        minimumPoint.y().toDouble(),
        minimumPoint.z().toDouble(),
        maximumPoint.x().toDouble(),
        maximumPoint.y().toDouble(),
        maximumPoint.z().toDouble(),
    )
}