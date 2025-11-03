package dev.slne.surf.parkour.fallback.entity

import dev.slne.surf.parkour.fallback.model.FallbackParkour
import dev.slne.surf.parkour.fallback.table.ParkourAreasTable
import dev.slne.surf.parkour.fallback.table.ParkourSpawnsTable
import dev.slne.surf.parkour.fallback.table.ParkourStatisticsTable
import dev.slne.surf.parkour.fallback.table.ParkourTable
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ParkourEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ParkourEntity>(ParkourTable)

    var uuid by ParkourTable.uuid
    var name by ParkourTable.name
    var serverUuid by ParkourTable.serverUuid
    val area by ParkourAreaEntity referrersOn ParkourAreasTable.parkourId
    val spawn by ParkourSpawnEntity referrersOn ParkourSpawnsTable.parkourId
    val statistics by ParkourStatisticEntity referrersOn ParkourStatisticsTable.parkourId

    fun toDto() = FallbackParkour(
        uuid,
        name,
        mutableObjectSetOf(),
        mutableObject2ObjectMapOf(),
        mutableObject2ObjectMapOf(),
        spawn.firstOrNull()?.toDto() ?: error("Parkour $name has no spawn defined"),
        area.firstOrNull()?.toDto() ?: error("Parkour $name has no area defined")
    )
}