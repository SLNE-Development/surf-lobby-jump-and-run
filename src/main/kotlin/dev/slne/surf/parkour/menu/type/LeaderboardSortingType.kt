package dev.slne.surf.parkour.menu.type

import dev.slne.surf.parkour.model.parkour.PersonalParkourSummary

enum class LeaderboardSortingType(
    val displayName: String,
    val sort: (MutableList<PersonalParkourSummary>) -> Unit
) {
    POINTS_HIGHEST("Punkte (Absteigend)", { it.sortByDescending { it.totalJumps } }),
    POINTS_LOWEST("Punkte (Aufsteigend)", { it.sortBy { it.totalJumps } }),
    HIGHSCORE_HIGHEST("Highscore (Absteigend)", { it.sortByDescending { it.bestJumps } }),
    HIGHSCORE_LOWEST("Highscore (Aufsteigend)", { it.sortBy { it.bestJumps } }),
    NAME("Name (Alphabetisch)", { it.sortBy { it.name } }), ;

    fun next() = entries.let { it[(ordinal + 1) % it.size] }
    fun previous() = entries.let { it[(ordinal - 1 + it.size) % it.size] }
}