package dev.slne.surf.parkour.api.model

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player

interface Jump {
    fun generate(previous: Block?, player: Player, material: Material): Block
}