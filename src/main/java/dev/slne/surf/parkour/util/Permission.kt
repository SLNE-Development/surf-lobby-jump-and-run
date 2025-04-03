package dev.slne.surf.parkour.util

object Permission {
    private const val PREFIX = "surf.parkour"
    private const val PREFIX_COMMAND = "$PREFIX.command"

    const val COMMAND_PARKOUR = PREFIX_COMMAND
    const val COMMAND_PARKOUR_CREATE = "$PREFIX_COMMAND.create"
    const val COMMAND_PARKOUR_REMOVE = "$PREFIX_COMMAND.delete"
    const val COMMAND_PARKOUR_START = "$PREFIX_COMMAND.start"
    const val COMMAND_PARKOUR_TOGGLE = "$PREFIX_COMMAND.toggle"
    const val COMMAND_PARKOUR_STATISTIC = "$PREFIX_COMMAND.stats"
    const val COMMAND_PARKOUR_LIST = "$PREFIX_COMMAND.list"
    const val COMMAND_PARKOUR_BETA = "$PREFIX_COMMAND.beta"

    private const val PREFIX_SETTING = "$PREFIX_COMMAND.setting"
    const val COMMAND_PARKOUR_SETTING = PREFIX_SETTING
    const val COMMAND_PARKOUR_SETTING_AREA = "$PREFIX_SETTING.area"
    const val COMMAND_PARKOUR_SETTING_SPAWN = "$PREFIX_SETTING.spawn"
    const val COMMAND_PARKOUR_SETTING_START = "$PREFIX_SETTING.start"
    const val COMMAND_PARKOUR_SETTING_MATERIAL = "$PREFIX_SETTING.material"
    const val COMMAND_PARKOUR_SETTING_MATERIAL_ADD = "$PREFIX_SETTING.material.add"
    const val COMMAND_PARKOUR_SETTING_MATERIAL_LIST = "$PREFIX_SETTING.material.list"
    const val COMMAND_PARKOUR_SETTING_MATERIAL_REMOVE = "$PREFIX_SETTING.material.remove"
}