package dev.slne.surf.parkour.fallback.entity

import dev.slne.surf.parkour.fallback.table.ParkourAreasTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ParkourAreaEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ParkourAreaEntity>(ParkourAreasTable)

    var parkour by ParkourEntity referencedOn ParkourAreasTable.parkourId
    var world by ParkourAreasTable.world
    var firstX by ParkourAreasTable.firstX
    var firstY by ParkourAreasTable.firstY
    var firstZ by ParkourAreasTable.firstZ
    var secondX by ParkourAreasTable.secondX
    var secondY by ParkourAreasTable.secondY
    var secondZ by ParkourAreasTable.secondZ
}