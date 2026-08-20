package dev.slne.surf.parkour.core.client.menu

import dev.slne.surf.api.core.messages.builder.SurfComponentBuilder
import dev.slne.surf.parkour.core.client.message.parkourColored

/**
 * The titles the parkour menus are shown under.
 */
object ParkourMenuTitles {
    const val OVERVIEW = "Parkour"
    const val STATISTICS = "Parkourstatistiken"
}

/**
 * The text a search dialog for player statistics is shown under.
 */
object ParkourSearchTexts {
    fun SurfComponentBuilder.appendSearchTitle() {
        parkourColored("Suche ein Spieler...")
    }

    fun SurfComponentBuilder.appendSearchBody() {
        parkourColored("Gib den Namen eines Spielers ein, um nach Statistiken zu suchen.")
    }
}
