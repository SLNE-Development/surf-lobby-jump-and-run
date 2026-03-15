package dev.slne.surf.parkour.paper.permission

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object ParkourPermissionRegistry : PermissionRegistry() {
    const val PREFIX = "surf.parkour"
    const val COMMAND_PREFIX = "$PREFIX.command"

    val COMMAND_PARKOUR = create("$COMMAND_PREFIX.parkour")
    val COMMAND_PARKOUR_RELOAD = create("$COMMAND_PREFIX.parkour.reload")
    val COMMAND_PARKOUR_NAMES = create("$COMMAND_PREFIX.parkour.recreateNames")
    val COMMAND_PARKOUR_PLAY = create("$COMMAND_PREFIX.parkour.play")
    val COMMAND_PARKOUR_CREATE = create("$COMMAND_PREFIX.parkour.create")
    val COMMAND_PARKOUR_LIST = create("$COMMAND_PREFIX.parkour.list")
    val COMMAND_PARKOUR_STATS = create("$COMMAND_PREFIX.parkour.stats")
}