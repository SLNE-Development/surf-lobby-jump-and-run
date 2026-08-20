package dev.slne.surf.parkour.paper.permission

import dev.slne.surf.api.paper.permission.PermissionRegistry
import dev.slne.surf.parkour.core.client.permission.ParkourPermissions

object ParkourPermissionRegistry : PermissionRegistry() {
    val COMMAND_PARKOUR = create(ParkourPermissions.COMMAND_PARKOUR)
    val COMMAND_PARKOUR_RELOAD = create(ParkourPermissions.COMMAND_PARKOUR_RELOAD)
    val COMMAND_PARKOUR_NAMES = create(ParkourPermissions.COMMAND_PARKOUR_NAMES)
    val COMMAND_PARKOUR_PLAY = create(ParkourPermissions.COMMAND_PARKOUR_PLAY)
    val COMMAND_PARKOUR_CREATE = create(ParkourPermissions.COMMAND_PARKOUR_CREATE)
    val COMMAND_PARKOUR_LIST = create(ParkourPermissions.COMMAND_PARKOUR_LIST)
    val COMMAND_PARKOUR_STATS = create(ParkourPermissions.COMMAND_PARKOUR_STATS)
}
