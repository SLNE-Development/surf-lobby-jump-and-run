package dev.slne.surf.parkour.core.client.command

import dev.slne.surf.api.core.service.PlayerLookupService
import dev.slne.surf.parkour.api.data.PlayerTextures
import dev.slne.surf.parkour.core.client.service.ParkourRunsService
import dev.slne.surf.parkour.core.client.service.ParkourTexturesService
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Looks up the name of everyone who has parkour stats but no stored name yet and stores it.
 *
 * Progress is reported to [sender] every ten players, so a long-running recreation stays visible.
 */
suspend fun recreateMissingPlayerNames(sender: Audience) {
    val start = System.currentTimeMillis()

    val textureUuids = ParkourTexturesService.textures.map { it.playerUuid }.toSet()
    val missingNames = ParkourRunsService.stats
        .filter { it.playerUuid !in textureUuids }

    val total = missingNames.size
    sender.sendMessage(Component.text("Starting recreation for $total players"))

    var processed = 0

    missingNames.forEach { stats ->
        val uuid = stats.playerUuid
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
                Component.text("Progress: $processed/$total (${processed * 100 / total}%) | ETA: $eta")
            )
        }
    }

    val totalTime = (System.currentTimeMillis() - start)
        .toDuration(DurationUnit.MILLISECONDS)

    sender.sendMessage(Component.text("Finished. Processed $processed players in $totalTime"))
}
