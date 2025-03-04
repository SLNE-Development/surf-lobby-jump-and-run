package dev.slne.surf.parkour.command.argument

import dev.jorel.commandapi.SuggestionInfo
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentInfo
import dev.jorel.commandapi.arguments.StringArgument
import org.bukkit.Material
import org.bukkit.command.CommandSender
import java.util.*
import java.util.stream.Collectors

object MaterialArgument {
    @JvmStatic
    fun argument(nodeName: String?): Argument<Material> {
        return CustomArgument (StringArgument(nodeName)) { info: CustomArgumentInfo<String?> ->
            val material = Material.getMaterial(info.input())

            if (material == null || material.isAir || !material.isSolid) {
                throw CustomArgumentException.fromMessageBuilder(CustomArgument.MessageBuilder("Unknown or invalid material: ").appendArgInput())
            }

            material
        }.replaceSuggestions(ArgumentSuggestions.strings<CommandSender?> { _: SuggestionInfo<CommandSender?>? ->
            Material.entries.filter { it.isSolid }.map { it.name }.toTypedArray()
        })
    }
}
