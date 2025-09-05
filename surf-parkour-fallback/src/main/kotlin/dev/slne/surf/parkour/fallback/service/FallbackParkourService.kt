package dev.slne.surf.parkour.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.parkour.api.model.parkour.Parkour
import dev.slne.surf.parkour.api.model.parkour.ParkourCreationData
import dev.slne.surf.parkour.core.factory.parkourFactory
import dev.slne.surf.parkour.core.registry.parkourRegistry
import dev.slne.surf.parkour.core.service.ParkourService
import dev.slne.surf.parkour.fallback.entity.ParkourAreaEntity
import dev.slne.surf.parkour.fallback.entity.ParkourEntity
import dev.slne.surf.parkour.fallback.entity.ParkourSpawnEntity
import dev.slne.surf.parkour.fallback.model.FallbackParkour
import dev.slne.surf.parkour.fallback.table.ParkourAreasTable
import dev.slne.surf.parkour.fallback.table.ParkourSpawnsTable
import dev.slne.surf.parkour.fallback.table.ParkourTable
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.util.Services
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@AutoService(ParkourService::class)
class FallbackParkourService : ParkourService, Services.Fallback {
    override fun createParkour(parkourData: ParkourCreationData): Parkour {
        val parkour = parkourFactory.createParkour(parkourData)
        parkourRegistry.registerParkour(parkour)

        return parkour
    }

    override fun deleteParkour(parkour: Parkour) {
        parkour.players.forEach {
            Bukkit.getPlayer(it)?.teleportAsync(parkour.spawnLocation)
        }
    }

    override fun getParkour(player: ParkourPlayer) =
        parkourRegistry.getParkours().find { it.players.contains(player.uuid) }

    override fun getParkour(name: String) = parkourRegistry.getParkour(name)
    override fun getParkour(uuid: UUID) = parkourRegistry.getParkour(uuid)
    override fun inParkour(player: ParkourPlayer) =
        parkourRegistry.getParkours().any { it.players.contains(player.uuid) }

    override fun getParkours() = parkourRegistry.getParkours()
    override suspend fun pushParkour(parkour: Parkour, serverUuid: UUID) =
        newSuspendedTransaction(Dispatchers.IO) {
            val parkourEntity = ParkourEntity.new {
                uuid = parkour.uuid
                name = parkour.name
                this.serverUuid = serverUuid
            }

            ParkourSpawnEntity.new {
                this.parkour = parkourEntity
                world = parkour.spawnLocation.world.uid
                x = parkour.spawnLocation.x
                y = parkour.spawnLocation.y
                z = parkour.spawnLocation.z
                yaw = parkour.spawnLocation.yaw
                pitch = parkour.spawnLocation.pitch
            }

            ParkourAreaEntity.new {
                this.parkour = parkourEntity
                world = parkour.corner1.world.uid
                firstX = parkour.corner1.x
                firstY = parkour.corner1.y
                firstZ = parkour.corner1.z
                secondX = parkour.corner2.x
                secondY = parkour.corner2.y
                secondZ = parkour.corner2.z
            }
            return@newSuspendedTransaction
        }

    override suspend fun fetchParkours(serverUuid: UUID) = newSuspendedTransaction(Dispatchers.IO) {
        ParkourEntity.find(ParkourTable.serverUuid eq serverUuid).map {
            val spawnEntity = it.spawn
            val areaEntity = it.area

            FallbackParkour(
                it.uuid,
                it.name,
                mutableObjectSetOf(),
                mutableObject2ObjectMapOf(),
                mutableObject2ObjectMapOf(),
                spawnEntity.firstOrNull()?.toDto()
                    ?: error("Parkour ${it.name} has no spawn defined"),
                areaEntity.firstOrNull()?.toDto()?.firstLocation
                    ?: error("Parkour ${it.name} has no area defined"),
                areaEntity.firstOrNull()?.toDto()?.secondLocation
                    ?: error("Parkour ${it.name} has no area defined")
            )
        }.forEach {
            parkourRegistry.registerParkour(it)
        }
    }

    override suspend fun onFailure(
        parkour: Parkour,
        player: ParkourPlayer
    ) = parkour.onFailure(player)

    override suspend fun onSuccess(
        parkour: Parkour,
        player: ParkourPlayer,
        index: Int
    ) = parkour.onSuccess(player, index)

    override fun createTable() {
        transaction {
            SchemaUtils.create(ParkourAreasTable, ParkourTable, ParkourSpawnsTable)
        }
    }
}