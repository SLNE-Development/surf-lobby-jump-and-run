package dev.slne.surf.parkour.leaderboard

import dev.slne.surf.parkour.player.PlayerData

enum class LeaderboardSortingType(val niceName: String, val sort: (MutableList<PlayerData>) -> Unit) {
    POINTS_HIGHEST("Punkte (Absteigend)", { it.sortByDescending { it.points } }),
    POINTS_LOWEST("Punkte (Aufsteigend)", { it.sortBy { it.points } }),
    HIGHSCORE_HIGHEST("Highscore (Absteigend)", { it.sortByDescending { it.highScore } }),
    HIGHSCORE_LOWEST("Highscore (Aufsteigend)", { it.sortBy { it.highScore } }),
    NAME("Name (Alphabetisch)", { it.sortBy { it.name } }),
}