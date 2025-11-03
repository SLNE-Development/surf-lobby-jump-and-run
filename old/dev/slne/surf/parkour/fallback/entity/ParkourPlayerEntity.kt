package dev.slne.surf.parkour.fallback.entity

import dev.slne.surf.parkour.fallback.table.ParkourPlayerTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ParkourPlayerEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ParkourPlayerEntity>(ParkourPlayerTable)

    var uuid by ParkourPlayerTable.uuid
    var name by ParkourPlayerTable.name
}