package dev.slne.surf.parkour.minestom

import com.google.auto.service.AutoService
import dev.slne.surf.parkour.core.client.ClientLoader
import dev.slne.surf.parkour.core.client.ClientParkourInstance
import dev.slne.surf.parkour.core.common.ParkourInstance
import net.kyori.adventure.util.Services

@AutoService(ParkourInstance::class)
class MinestomParkourInstance : ClientParkourInstance, Services.Fallback {
    override val dataPath get() = ParkourMinestomEntrypoint.dataPath
    override val clientLoader by lazy { ClientLoader(dataPath) }
}
