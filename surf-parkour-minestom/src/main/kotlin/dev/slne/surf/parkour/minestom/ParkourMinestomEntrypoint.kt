package dev.slne.surf.parkour.minestom

import com.google.inject.Inject
import com.google.inject.Provider
import com.google.inject.Singleton
import dev.slne.minestom.lobby.api.instance.LobbyInstance
import dev.slne.minestom.lobby.api.plugin.MinestomPluginEntrypoint
import dev.slne.minestom.lobby.api.plugin.annotation.DataDirectory
import dev.slne.surf.api.minestom.inventory.framework.register
import dev.slne.surf.parkour.core.client.ClientParkourInstance
import dev.slne.surf.parkour.core.client.service.ParkourRunsService
import dev.slne.surf.parkour.core.client.service.ParkourService
import dev.slne.surf.parkour.core.client.service.ParkourTexturesService
import dev.slne.surf.parkour.minestom.menu.view.ParkourActivePlayersView
import dev.slne.surf.parkour.minestom.menu.view.ParkourLeaderboardView
import dev.slne.surf.parkour.minestom.menu.view.ParkourOverviewView
import dev.slne.surf.parkour.minestom.service.ParkourActionBarTask
import net.minestom.server.instance.InstanceContainer
import java.nio.file.Path

@Singleton
class ParkourMinestomEntrypoint @Inject constructor(
    @DataDirectory path: Path,
    @LobbyInstance lobbyInstance: Provider<InstanceContainer>
) : MinestomPluginEntrypoint {

    init {
        dataPath = path
        lobbyInstanceProvider = lobbyInstance
    }

    override suspend fun start() {
        ParkourOverviewView.register()
        ParkourLeaderboardView.register()
        ParkourActivePlayersView.register()

        ClientParkourInstance.clientLoader.onLoad()
        ClientParkourInstance.clientLoader.onEnable()

        ParkourService.loadParkours()
        ParkourRunsService.loadStats()
        ParkourTexturesService.loadTextures()
        ParkourActionBarTask.startUpdating()
    }

    override suspend fun stop() {
        ParkourActionBarTask.stopUpdating()
        ClientParkourInstance.clientLoader.onDisable()
    }

    companion object {
        lateinit var dataPath: Path
            private set

        private lateinit var lobbyInstanceProvider: Provider<InstanceContainer>

        /**
         * The lobby world.
         *
         * The server creates it while it starts up, so this is only readable afterwards - the
         * plugin's entrypoint is built before that happens.
         */
        val lobbyInstance: InstanceContainer get() = lobbyInstanceProvider.get()
    }
}
