package dev.slne.surf.parkour.fallback.entity

import dev.slne.surf.parkour.core.model.CoreParkourStatistic
import dev.slne.surf.parkour.fallback.table.ParkourStatisticsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ParkourStatisticEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ParkourStatisticEntity>(ParkourStatisticsTable)

    var parkour by ParkourEntity referencedOn ParkourStatisticsTable.parkourId
    var userUuid by ParkourStatisticsTable.userUuid
    var time by ParkourStatisticsTable.time
    var jumps by ParkourStatisticsTable.jumps

    fun toDto() = CoreParkourStatistic(
        userUuid,
        parkour.toDto(),
        time,
        jumps
    )
}