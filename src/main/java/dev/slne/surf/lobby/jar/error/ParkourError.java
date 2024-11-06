package dev.slne.surf.lobby.jar.error;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;

@Builder
@Getter
@Setter
@Accessors(fluent = true)
public class ParkourError {
  private final String reason;
  private final String identifier;
  private final Player player;
}
