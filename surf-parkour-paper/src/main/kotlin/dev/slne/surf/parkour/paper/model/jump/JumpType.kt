package dev.slne.surf.parkour.paper.model.jump

data class JumpType(
    val forward: IntRange,
    val lateral: IntRange,
    val vertical: IntRange
)