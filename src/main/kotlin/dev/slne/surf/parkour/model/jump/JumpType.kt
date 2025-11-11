package dev.slne.surf.parkour.model.jump

data class JumpType(
    val forward: IntRange,
    val lateral: IntRange,
    val vertical: IntRange
) {
    fun random(): Jump = Jump(
        forward.random(),
        lateral.random(),
        vertical.random()
    )
}