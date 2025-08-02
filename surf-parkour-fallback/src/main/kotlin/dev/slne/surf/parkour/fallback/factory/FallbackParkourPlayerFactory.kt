package dev.slne.surf.parkour.fallback.factory

import com.google.auto.service.AutoService
import dev.slne.surf.parkour.api.entity.ParkourPlayer
import dev.slne.surf.parkour.core.entity.CoreParkourPlayer
import dev.slne.surf.parkour.core.factory.ParkourPlayerFactory
import net.kyori.adventure.util.Services
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

@AutoService(ParkourPlayerFactory::class)
class FallbackParkourPlayerFactory : ParkourPlayerFactory, Services.Fallback {
    override fun createPlayer(
        uuid: UUID,
        name: String
    ) = CoreParkourPlayer(uuid, name)

    override fun from(player: Player) = CoreParkourPlayer(player.uniqueId, player.name)
    override fun from(offlinePlayer: OfflinePlayer) =
        CoreParkourPlayer(offlinePlayer.uniqueId, offlinePlayer.name ?: "Unknown")

    override fun from(name: String): ParkourPlayer? {
        val player = Bukkit.getPlayer(name) ?: return null
        return CoreParkourPlayer(player.uniqueId, player.name)
    }

    override fun from(uuid: UUID): ParkourPlayer? {
        val player = Bukkit.getPlayer(uuid) ?: return null
        return CoreParkourPlayer(player.uniqueId, player.name)
    }
}