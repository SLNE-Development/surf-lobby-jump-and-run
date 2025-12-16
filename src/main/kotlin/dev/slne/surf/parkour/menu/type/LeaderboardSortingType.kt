package dev.slne.surf.parkour.menu.type

import dev.slne.surf.parkour.model.parkour.PersonalParkourSummary

enum class LeaderboardSortingType(
    val displayName: String,
    val sort: (MutableList<PersonalParkourSummary>) -> Unit
) {
    POINTS_HIGHEST("Sprünge (Absteigend)", { stats -> stats.sortByDescending { it.totalJumps } }),
    POINTS_LOWEST("Sprünge (Aufsteigend)", { stats -> stats.sortBy { it.totalJumps } }),
    HIGHSCORE_HIGHEST(
        "Highscore (Absteigend)",
        { stats -> stats.sortByDescending { it.bestJumps } }),
    HIGHSCORE_LOWEST("Highscore (Aufsteigend)", { stats -> stats.sortBy { it.bestJumps } }),
    NAME("Name (Alphabetisch)", { stats -> stats.sortBy { it.name.lowercase() } });

    fun next() = entries.let { it[(ordinal + 1) % it.size] }
    fun previous() = entries.let { it[(ordinal - 1 + it.size) % it.size] }
}