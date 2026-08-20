package dev.slne.surf.parkour.core.client.menu

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.builder.SurfComponentBuilder
import dev.slne.surf.parkour.api.data.ParkourStats
import dev.slne.surf.parkour.core.client.message.parkourColored
import dev.slne.surf.parkour.core.client.util.formatMillis
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration

/**
 * The names and lore the parkour menus render, shared by every platform.
 */
object ParkourMenuContent {
    val ownStatsName = primaryHeading("Deine Statistiken")
    val leaderboardName = primaryHeading("Bestenliste")
    val startName = primaryHeading("Parkour starten")
    val activePlayersName = primaryHeading("Aktive Spieler")
    val closeName = primaryHeading("Schließen")
    val backName = primaryHeading("Zurück")

    val previousPageName = buildText { parkourColored("Vorherige Seite") }
    val nextPageName = buildText { parkourColored("Nächste Seite") }
    val searchName = buildText { parkourColored("Suchen") }
    val sortName = buildText { parkourColored("Sortieren") }

    val unknownPlayerName: Component = buildText {
        parkourColored("#Unbekannt", TextDecoration.BOLD)
    }

    /**
     * The hint shown wherever player skins are involved.
     */
    val skinNotice: Component = buildText {
        error("Derzeit werden Spieler-Skins nicht geladen. ")
    }

    val leaderboardLore = listOf(Component.empty(), skinNotice)

    /**
     * The name of the player [playerName] belongs to, as the active-players menu writes it.
     */
    fun activePlayerName(playerName: String?): Component =
        if (playerName == null) unknownPlayerName else buildText {
            parkourColored(playerName, TextDecoration.BOLD)
        }

    /**
     * The name of a leaderboard entry belonging to [playerName].
     */
    fun leaderboardEntryName(playerName: String): Component = buildText {
        parkourColored(playerName)
    }

    /**
     * The lore of a player's entry in the active-players menu.
     */
    fun activePlayerLore(currentJumps: Int) = buildLore {
        emptyLine()
        line {
            parkourColored("Aktueller Lauf".toSmallCaps(), TextDecoration.BOLD)
        }
        line {
            spacer("-")
            appendSpace()
            parkourColored("Sprünge: ")
            variableValue(currentJumps)
        }
    }

    /**
     * The lore of the player's own stats in the overview menu.
     */
    fun ownStatsLore(stats: ParkourStats) = buildLore {
        emptyLine()
        line {
            parkourColored("Parkourstatistiken".toSmallCaps(), TextDecoration.BOLD)
        }
        appendStatLines(stats)
    }

    /**
     * The lore of a leaderboard entry ranked [rank] under [sortType].
     */
    fun leaderboardEntryLore(
        stats: ParkourStats,
        rank: Int,
        sortType: ParkourLeaderboardSortType
    ) = buildLore {
        emptyLine()
        line {
            parkourColored("Platzierung".toSmallCaps(), TextDecoration.BOLD)
            appendSpace()
            spacer("(${sortType.label})")
        }
        line {
            spacer("-")
            appendSpace()
            parkourColored("Rang: ")
            variableValue("#$rank")
        }
        emptyLine()
        line {
            parkourColored("Parkourstatistiken".toSmallCaps(), TextDecoration.BOLD)
        }
        appendStatLines(stats)
        emptyLine()
        +skinNotice
    }

    /**
     * The lore of the sort button, marking [selected] as the sorting in use.
     */
    fun sortLore(selected: ParkourLeaderboardSortType) = buildLore {
        emptyLine()
        line { parkourColored("Sortierung".toSmallCaps(), TextDecoration.BOLD) }

        ParkourLeaderboardSortType.entries.forEach { type ->
            line {
                if (selected == type) {
                    appendSpace()
                    spacer("-")
                    appendSpace()
                    parkourColored(type.label)
                } else {
                    spacer("-")
                    appendSpace()
                    white(type.label)
                }
            }
        }
    }

    private fun LoreBuilder.appendStatLines(stats: ParkourStats) {
        line {
            spacer("-")
            appendSpace()
            parkourColored("Highscore: ")
            variableValue(stats.highscore)
        }
        line {
            spacer("-")
            appendSpace()
            parkourColored("Versuche: ")
            variableValue(stats.totalRuns)
        }
        line {
            spacer("-")
            appendSpace()
            parkourColored("Gesamtsprünge: ")
            variableValue(stats.totalJumps)
        }
        line {
            spacer("-")
            appendSpace()
            parkourColored("Durchschnittliche Zeit: ")
            variableValue(formatMillis(stats.averageTime))
        }
    }

    private fun primaryHeading(text: String): Component = buildText {
        primary(text.toSmallCaps(), TextDecoration.BOLD)
    }
}

/**
 * Collects the lines of an item's lore.
 */
class LoreBuilder {
    private val lines = mutableListOf<Component>()

    /**
     * Adds a line built from [block].
     */
    fun line(block: SurfComponentBuilder.() -> Unit) {
        lines.add(SurfComponentBuilder(block))
    }

    /**
     * Adds [this] as a line.
     */
    operator fun Component.unaryPlus() {
        lines.add(this)
    }

    /**
     * Adds an empty line.
     */
    fun emptyLine() {
        lines.add(Component.empty())
    }

    internal fun build(): List<Component> = lines
}

/**
 * Builds the lines of an item's lore, so both platforms can hand them to their own item builder.
 */
fun buildLore(block: LoreBuilder.() -> Unit): List<Component> = LoreBuilder().apply(block).build()
