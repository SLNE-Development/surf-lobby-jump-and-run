package dev.slne.surf.parkour.minestom.service

import dev.slne.minestom.lobby.api.extension.SchedulerManager
import dev.slne.surf.parkour.core.client.service.ParkourActionBarService
import net.minestom.server.timer.ExecutionType
import net.minestom.server.timer.Task
import net.minestom.server.timer.TaskSchedule

object ParkourActionBarTask {
    private var updateTask: Task? = null

    fun startUpdating() {
        if (updateTask?.isAlive == true) {
            return
        }

        val schedule = TaskSchedule.millis(ParkourActionBarService.UPDATE_INTERVAL_MILLIS)

        updateTask = SchedulerManager.submitTask(
            {
                ParkourActionBarService.sendUpdates()
                schedule
            },
            ExecutionType.TICK_END
        )
    }

    fun stopUpdating() {
        updateTask?.cancel()
        updateTask = null
    }
}
