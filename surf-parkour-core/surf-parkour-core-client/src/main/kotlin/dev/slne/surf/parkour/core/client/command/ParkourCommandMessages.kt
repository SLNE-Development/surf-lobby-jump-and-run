package dev.slne.surf.parkour.core.client.command

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.CommonComponents
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.builder.SurfComponentBuilder
import dev.slne.surf.api.core.messages.pagination.Pagination
import dev.slne.surf.parkour.api.data.ParkourStats
import dev.slne.surf.parkour.core.client.message.appendLinePrefix
import dev.slne.surf.parkour.core.client.model.parkour.Parkour
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration

/**
 * Writes the stats overview `/parkour stats` answers with.
 */
fun SurfComponentBuilder.appendPersonalStats(stats: ParkourStats) {
    val averageJumps = stats.totalJumps / stats.totalRuns

    appendNewline()
    primary("Deine Parkour-Statistiken".toSmallCaps(), TextDecoration.BOLD)
    appendNewline()

    appendNewline {
        appendLinePrefix()
        variableKey("Gesamte Läufe:")
        appendSpace()
        variableValue(stats.totalRuns)
    }

    appendNewline {
        appendLinePrefix()
        variableKey("Gesamte Sprünge:")
        appendSpace()
        variableValue(stats.totalJumps)
    }

    appendNewline {
        appendLinePrefix()
        variableKey("Durchschnittliche Sprünge:")
        appendSpace()
        variableValue(averageJumps)
        spacer(" Sprünge")
    }

    appendNewline()

    appendNewline {
        appendLinePrefix()
        variableKey("Höchste Sprunganzahl:")
        appendSpace()
        variableValue(stats.highscore)
        spacer(" Sprünge")
    }
}

/**
 * Renders page [page] of [parkours] the way `/parkour list` shows it.
 */
fun renderParkourList(parkours: Collection<Parkour>, page: Int): Component {
    val pagination = Pagination<Parkour> {
        title {
            primary("Parkour Liste".toSmallCaps(), TextDecoration.BOLD)
        }

        rowRenderer { parkour, _ ->
            listOf(
                buildText {
                    append(CommonComponents.EM_DASH)
                    appendSpace()
                    variableKey(parkour.displayName)
                    appendSpace()
                    spacer("(")
                    variableValue(parkour.players.size)
                    spacer(" Spieler)")
                    hoverEvent(buildText {
                        append(CommonComponents.EM_DASH)
                        appendSpace()
                        variableKey("Identifier:")
                        appendSpace()
                        variableValue(parkour.identifier)
                        appendNewline()
                        append(CommonComponents.EM_DASH)
                        appendSpace()
                        variableKey("Welt:")
                        appendSpace()
                        variableValue(parkour.world)
                    })
                }
            )
        }
    }

    return pagination.renderComponent(parkours, page)
}

/**
 * Announces that the plugin is about to read its config again.
 */
fun SurfComponentBuilder.appendReloadStarted() {
    appendInfoPrefix()
    info("Das Plugin wird neu geladen...")
}

/**
 * Announces that the plugin has read its config again.
 */
fun SurfComponentBuilder.appendReloadFinished() {
    appendSuccessPrefix()
    success("Das Plugin wurde neu geladen.")
}

/**
 * Reports that this server knows no parkour at all.
 */
fun SurfComponentBuilder.appendNoParkourFound() {
    appendErrorPrefix()
    error("Es wurde kein Parkour gefunden.")
}

/**
 * Reports that the player is already running the parkour named [displayName].
 */
fun SurfComponentBuilder.appendAlreadyInParkour(displayName: String) {
    appendErrorPrefix()
    error("Du bist bereits in dem Parkour ")
    variableValue(displayName)
    error(".")
}

/**
 * Reports that the parkour named [displayName] has begun.
 */
fun SurfComponentBuilder.appendParkourStarted(displayName: String) {
    appendSuccessPrefix()
    success("Du hast den Parkour ")
    variableValue(displayName)
    success(" gestartet.")
}

/**
 * Reports that an identifier carrying spaces cannot name a parkour.
 */
fun SurfComponentBuilder.appendIdentifierContainsSpaces() {
    appendErrorPrefix()
    error("Die Kennung darf keine Leerzeichen enthalten.")
}

/**
 * Reports that another parkour already answers to the identifier.
 */
fun SurfComponentBuilder.appendIdentifierTaken() {
    appendErrorPrefix()
    error("Ein Parkour mit dieser Kennung existiert bereits.")
}

/**
 * Reports that the parkour named [displayName] now exists.
 */
fun SurfComponentBuilder.appendParkourCreated(displayName: String) {
    appendSuccessPrefix()
    success("Der Parkour ")
    variableValue(displayName)
    success(" wurde erstellt.")
}

/**
 * Reports that no parkour is open to play right now.
 */
fun SurfComponentBuilder.appendNoParkoursAvailable() {
    appendErrorPrefix()
    error("Es sind derzeit keine Parkours verfügbar.")
}

/**
 * Reports that no parkour answers to [identifier].
 */
fun SurfComponentBuilder.appendUnknownParkour(identifier: String) {
    appendErrorPrefix()
    error("Der Parkour ")
    variableValue(identifier)
    error(" wurde nicht gefunden.")
}
