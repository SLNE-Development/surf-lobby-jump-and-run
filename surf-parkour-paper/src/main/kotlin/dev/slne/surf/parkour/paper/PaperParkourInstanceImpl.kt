package dev.slne.surf.parkour.paper

import com.google.auto.service.AutoService
import dev.slne.surf.parkour.core.common.ParkourInstance
import dev.slne.surf.parkour.core.paper.PaperLoader
import dev.slne.surf.parkour.core.paper.PaperParkourInstance
import net.kyori.adventure.util.Services

@AutoService(ParkourInstance::class)
class PaperParkourInstanceImpl : PaperParkourInstance, Services.Fallback {
    override val paperLoader = PaperLoader(plugin.dataPath)
}