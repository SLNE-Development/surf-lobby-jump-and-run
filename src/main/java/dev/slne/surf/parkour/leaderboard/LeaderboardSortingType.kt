package dev.slne.surf.parkour.leaderboard

enum class LeaderboardSortingType(val niceName: String) {
    POINTS_HIGHEST("Punkte (Absteigend)"),
    POINTS_LOWEST("Points (Aufsteigend)"),
    HIGHSCORE_HIGHEST("Highscore (Absteigend)"),
    HIGHSCORE_LOWEST("Highscore (Aufsteigend)"),
    NAME("Name (Alphabetisch)"),
}