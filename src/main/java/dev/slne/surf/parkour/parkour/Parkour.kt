package dev.slne.surf.parkour.parkour

import dev.slne.surf.parkour.util.Area
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.UUID

data class Parkour (
    val uuid: UUID,
    val name: String,

    val world: World,
    val area: Area,
    val start: Vector,
    val respawn: Vector,

    val availableMaterials: ObjectSet<Material>,
    val activePlayers: ObjectSet<Player>
) {
}