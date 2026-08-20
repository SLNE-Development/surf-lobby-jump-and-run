package dev.slne.surf.parkour.core.client.permission

/**
 * Platform-neutral registry of all permission node strings used by surf-parkour.
 */
object ParkourPermissions {
    const val PREFIX = "surf.parkour"
    const val COMMAND_PREFIX = "$PREFIX.command"

    const val COMMAND_PARKOUR = "$COMMAND_PREFIX.parkour"
    const val COMMAND_PARKOUR_RELOAD = "$COMMAND_PREFIX.parkour.reload"
    const val COMMAND_PARKOUR_NAMES = "$COMMAND_PREFIX.parkour.recreateNames"
    const val COMMAND_PARKOUR_PLAY = "$COMMAND_PREFIX.parkour.play"
    const val COMMAND_PARKOUR_CREATE = "$COMMAND_PREFIX.parkour.create"
    const val COMMAND_PARKOUR_LIST = "$COMMAND_PREFIX.parkour.list"
    const val COMMAND_PARKOUR_STATS = "$COMMAND_PREFIX.parkour.stats"
}
