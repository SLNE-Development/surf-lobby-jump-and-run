package dev.slne.surf.parkour.core.client.model.jump

/**
 * The range of jumps one kind of parkour step may produce.
 */
data class JumpType(
    val forward: IntRange,
    val lateral: IntRange,
    val vertical: IntRange
) {
    /**
     * Returns a random jump within this type's ranges.
     */
    fun randomJump() = Jump(
        forward.random(),
        lateral.random(),
        vertical.random()
    )
}
