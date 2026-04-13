package dev.slne.surf.parkour.paper.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.service.PlayerLookupService
import dev.slne.surf.api.paper.command.executors.anyExecutorSuspend
import dev.slne.surf.parkour.api.data.PlayerTextures
import dev.slne.surf.parkour.core.paper.service.ParkourRunsService
import dev.slne.surf.parkour.core.paper.service.ParkourTexturesService
import dev.slne.surf.parkour.paper.permission.ParkourPermissionRegistry
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun CommandAPICommand.parkourCreateNamesCommand() = subcommand("reCreateNames") {
    withPermission(ParkourPermissionRegistry.COMMAND_PARKOUR_NAMES)
    anyExecutorSuspend { sender, _ ->
        val start = System.currentTimeMillis()

        val textureUuids = ParkourTexturesService.textures.map { it.playerUuid }.toSet()
        val missingNames = ParkourRunsService.stats
            .map { it.playerUuid }
            .filter { it !in textureUuids }

        val total = missingNames.size
        sender.sendMessage("Starting recreation for $total players")

        var processed = 0

        missingNames.forEach { uuid ->
            val name = PlayerLookupService.getUsername(uuid) ?: return@forEach

            ParkourTexturesService.saveTexture(
                PlayerTextures(uuid, name, "")
            )

            processed++

            if (processed % 10 == 0 || processed == total) {
                val elapsed = System.currentTimeMillis() - start
                val avgPerEntry = elapsed.toDouble() / processed
                val remaining = total - processed
                val etaMs = (avgPerEntry * remaining).toLong()

                val eta = etaMs.toDuration(DurationUnit.MILLISECONDS)

                sender.sendMessage(
                    "Progress: $processed/$total (${processed * 100 / total}%) | ETA: $eta"
                )
            }
        }

        val totalTime = (System.currentTimeMillis() - start)
            .toDuration(DurationUnit.MILLISECONDS)

        sender.sendMessage("Finished. Processed $processed players in $totalTime")
    }
}
