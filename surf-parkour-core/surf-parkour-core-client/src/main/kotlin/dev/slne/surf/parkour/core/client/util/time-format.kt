package dev.slne.surf.parkour.core.client.util

/**
 * This duration in milliseconds, written as the largest units it covers, e.g. `1h 2m 3s`.
 *
 * Units that contribute nothing are left out; a duration below one second reads as `0s`.
 */
val Long.formattedDuration: String
    get() {
        val totalSeconds = this / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        val parts = mutableListOf<String>()

        if (hours > 0) parts += "${hours}h"
        if (minutes > 0) parts += "${minutes}m"
        if (seconds > 0 || parts.isEmpty()) parts += "${seconds}s"

        return parts.joinToString(" ")
    }

/**
 * Writes [time] milliseconds with a fixed two digits per unit, e.g. `01h 02m 03s`.
 *
 * Units above the largest one that contributes are left out.
 */
fun formatMillis(time: Long): String {
    val totalSeconds = time / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return when {
        hours > 0 -> "%02dh %02dm %02ds".format(hours, minutes, seconds)
        minutes > 0 -> "%02dm %02ds".format(minutes, seconds)
        else -> "%02ds".format(seconds)
    }
}
