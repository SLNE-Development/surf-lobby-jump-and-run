package dev.slne.surf.parkour.api.data

import java.util.*

data class PlayerTextures(
    val playerUuid: UUID,
    val playerName: String,
    val texture: String
) {
    companion object {
        fun empty() = PlayerTextures(
            playerUuid = UUID(0, 0),
            playerName = "#Unknown (Empty)",
            texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTE2M2RhZmFjMWQ5MWE4YzkxZGI1NzZjYWFjNzg0MzM2NzkxYTZlMThkOGY3ZjYyNzc4ZmM0N2JmMTQ2YjYifX19"
        )
    }
}
