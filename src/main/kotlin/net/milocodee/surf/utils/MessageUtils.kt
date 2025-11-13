package net.milocodee.surf.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player

object MessageUtils {

    fun Player.sendSuccessMessage(message: String) {
        sendMessage(
            Component.text()
                .append(Component.text("✓ ", NamedTextColor.GREEN, TextDecoration.BOLD))
                .append(Component.text(message, NamedTextColor.GREEN))
                .build()
        )
    }

    fun Player.sendErrorMessage(message: String) {
        sendMessage(
            Component.text()
                .append(Component.text("✗ ", NamedTextColor.RED, TextDecoration.BOLD))
                .append(Component.text(message, NamedTextColor.RED))
                .build()
        )
    }

    fun Player.sendInfoMessage(message: String) {
        sendMessage(
            Component.text()
                .append(Component.text("ⓘ ", NamedTextColor.AQUA, TextDecoration.BOLD))
                .append(Component.text(message, NamedTextColor.GRAY))
                .build()
        )
    }

    fun Player.sendHighscoreMessage(jumps: Int) {
        sendMessage(
            Component.text()
                .append(Component.text("★ ", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.text("Neuer Highscore! ", NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.text("$jumps Sprünge", NamedTextColor.YELLOW))
                .build()
        )
    }
}

object ValidationUtils {

    fun isValidJumpCount(jumps: Int): Boolean {
        return jumps >= 0
    }

    fun isNewHighscore(currentJumps: Int, previousHighscore: Int): Boolean {
        return currentJumps < previousHighscore || previousHighscore == 0
    }
}

object FormatUtils {

    fun formatJumps(jumps: Int): String {
        return when {
            jumps == 0 -> "Keine Sprünge"
            jumps == 1 -> "1 Sprung"
            else -> "$jumps Sprünge"
        }
    }

    fun formatHighscore(highscore: Int): String {
        return when {
            highscore == 0 -> "Kein Highscore"
            else -> formatJumps(highscore)
        }
    }
}