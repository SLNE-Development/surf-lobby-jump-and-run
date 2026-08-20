package dev.slne.surf.parkour.paper.service

import dev.slne.surf.parkour.core.client.service.ParkourActionBarService
import dev.slne.surf.parkour.paper.plugin
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import java.util.concurrent.TimeUnit

object ParkourActionBarTask {
    private lateinit var updateTask: ScheduledTask

    fun startUpdating() {
        if (::updateTask.isInitialized && !updateTask.isCancelled) {
            return
        }

        updateTask = Bukkit.getAsyncScheduler().runAtFixedRate(
            plugin,
            { ParkourActionBarService.sendUpdates() },
            0L,
            ParkourActionBarService.UPDATE_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS
        )
    }

    fun stopUpdating() {
        if (::updateTask.isInitialized && !updateTask.isCancelled) {
            updateTask.cancel()
        }
    }
}
