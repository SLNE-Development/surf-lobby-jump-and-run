package dev.slne.surf.lobby.jar.command.argument;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException;
import dev.jorel.commandapi.arguments.CustomArgument.MessageBuilder;
import dev.jorel.commandapi.arguments.StringArgument;
import java.util.Arrays;
import org.bukkit.Material;

public class MaterialArgument {
  public static Argument<Material> argument (String nodeName) {
    return new CustomArgument<>(new StringArgument(nodeName), info -> {
      Material material = Material.getMaterial(info.input());

      if (material == null || material.isAir() || !material.isSolid()) {
        throw CustomArgumentException.fromMessageBuilder(new MessageBuilder("Unknown or invalid material: ").appendArgInput());
      }

      return material;
    }).replaceSuggestions(ArgumentSuggestions.strings(info ->
        Arrays.stream(Material.values()).filter(Material::isSolid).map(Material::name).toArray(String[]::new)
    ));
  }
}
