package dev.slne.surf.lobby.jar.command.argument

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

object MaterialArgument {
    @JvmStatic
    fun argument(nodeName: String?): Argument<Material> {
        return CustomArgument<Material, String?>(
            StringArgument(nodeName)
        ) { info: CustomArgumentInfo<String?> ->
            val material = Material.getMaterial(info.input())
            if (material == null || material.isAir || !material.isSolid) {
                throw CustomArgumentException.fromMessageBuilder(
                    CustomArgument.MessageBuilder("Unknown or invalid material: ")
                        .appendArgInput()
                )
            }
            material
        }.replaceSuggestions(ArgumentSuggestions.strings<CommandSender?> { info: SuggestionInfo<CommandSender?>? ->
            Arrays.stream<Material>(
                Material.entries.toTypedArray()
            ).filter { obj: Material -> obj.isSolid }
                .map<String> { obj: Material -> obj.name }
                .toArray<String?> { _Dummy_.__Array__() }
        })
    }
}
