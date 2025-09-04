package dev.slne.surf.parkour.fallback.entity

import dev.slne.surf.parkour.fallback.table.ParkourSpawnsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ParkourSpawnEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ParkourSpawnEntity>(ParkourSpawnsTable)

    var parkour by ParkourEntity referencedOn ParkourSpawnsTable.parkourId
    var world by ParkourSpawnsTable.world
    var x by ParkourSpawnsTable.x
    var y by ParkourSpawnsTable.y
    var z by ParkourSpawnsTable.z
    var yaw by ParkourSpawnsTable.yaw
    var pitch by ParkourSpawnsTable.pitch

    fun toDto() = org.bukkit.Location(
        org.bukkit.Bukkit.getWorld(world) ?: error("World $world not found"),
        x, y, z, yaw, pitch
    )
}