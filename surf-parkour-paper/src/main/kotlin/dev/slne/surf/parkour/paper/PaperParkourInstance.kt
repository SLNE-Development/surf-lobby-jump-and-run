package dev.slne.surf.parkour.paper

import com.google.auto.service.AutoService
import dev.slne.surf.parkour.core.client.ClientLoader
import dev.slne.surf.parkour.core.client.ClientParkourInstance
import dev.slne.surf.parkour.core.common.ParkourInstance
import net.kyori.adventure.util.Services

@AutoService(ParkourInstance::class)
class PaperParkourInstance : ClientParkourInstance, Services.Fallback {
    override val dataPath = plugin.dataPath
    override val clientLoader = ClientLoader(dataPath)
}
