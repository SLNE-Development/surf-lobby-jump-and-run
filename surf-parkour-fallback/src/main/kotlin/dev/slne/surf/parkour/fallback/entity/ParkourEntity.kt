package dev.slne.surf.parkour.fallback.entity

import dev.slne.surf.parkour.fallback.table.ParkourAreasTable
import dev.slne.surf.parkour.fallback.table.ParkourSpawnsTable
import dev.slne.surf.parkour.fallback.table.ParkourStatisticsTable
import dev.slne.surf.parkour.fallback.table.ParkourTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ParkourEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ParkourEntity>(ParkourTable)

    var name by ParkourTable.name
    var serverUuid by ParkourTable.serverUuid
    val areas by ParkourAreaEntity referrersOn ParkourAreasTable.parkourId
    val spawns by ParkourSpawnEntity referrersOn ParkourSpawnsTable.parkourId
    val statistics by ParkourStatisticEntity referrersOn ParkourStatisticsTable.parkourId
}