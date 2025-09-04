package dev.slne.surf.parkour.fallback.entity

import dev.slne.surf.parkour.api.model.parkour.statistic.ParkourSpecificStatistic
import dev.slne.surf.parkour.core.model.statistic.CoreParkourSpecificStatistic
import dev.slne.surf.parkour.fallback.table.ParkourStatisticsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ParkourStatisticEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ParkourStatisticEntity>(ParkourStatisticsTable)

    var parkour by ParkourEntity referencedOn ParkourStatisticsTable.parkourId
    val userUuid by ParkourStatisticsTable.userUuid
    var tries by ParkourStatisticsTable.tries
    var failures by ParkourStatisticsTable.failures
    var bestTry by ParkourStatisticsTable.bestTry

    fun toDto = CoreParkourSpecificStatistic(

    )
}