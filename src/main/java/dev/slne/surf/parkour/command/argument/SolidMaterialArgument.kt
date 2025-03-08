package dev.slne.surf.parkour.command.argument

import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentInfoParser
import dev.jorel.commandapi.arguments.StringArgument
import org.bukkit.Material

class SolidMaterialArgument(nodeName: String): CustomArgument<Material, String> (StringArgument(nodeName), CustomArgumentInfoParser { info: CustomArgumentInfo<String> ->
        val material = Material.getMaterial(info.input())

        if (material == null || material.isAir || !material.isBlock || !material.isSolid) {
            throw CustomArgumentException.fromAdventureComponent(
                dev.slne.surf.parkour.util.MessageBuilder().withPrefix().error("Das Material ").variableValue(info.input()).error("ist kein Block oder existiert nicht.").build()
            )
        }

        material
    })