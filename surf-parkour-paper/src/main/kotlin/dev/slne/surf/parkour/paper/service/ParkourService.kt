package dev.slne.surf.parkour.paper.service

import dev.slne.surf.parkour.paper.model.parkour.Parkour
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import org.jetbrains.annotations.UnmodifiableView
import java.util.*

interface ParkourService {
    val parkours: @UnmodifiableView ObjectSet<Parkour>

    fun createParkour(
        uuid: UUID,
        identifier: String,
        displayName: String,
        boundingBox: BoundingBox,
        world: World,
        respawnLocation: Location
    ): Parkour

    fun triggerFailure(player: Player)
    fun triggerSuccess(player: Player)

    fun isInParkour(playerUuid: UUID): Boolean
    fun isInParkour(player: Player): Boolean

    fun getParkour(playerUuid: UUID): Parkour?
    fun getParkour(player: Player): Parkour?
    fun getParkour(identifier: String): Parkour?

    fun exists(identifier: String): Boolean

    fun loadParkours()

    companion object : ParkourService by ParkourServiceImpl
}