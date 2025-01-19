package dev.slne.surf.lobby.jar.mysql

import dev.slne.surf.lobby.jar.mysql.worker.ConnectionWorkers

object Database {

    // Ungerne. Wir erstellen Tables immer selber, damit es nicht dazu kommt, dass ein Table spontan neu erstellt wird. Ja IF NOT EXISTS ist vorhanden, aber
    // wenn wir uns entscheiden, dass der Table mal umbenannt werden soll, wird trotzdem direkt bei der nächsten DB operation von deinem Plugin
    // der Table neu erstellt. Das kann zu Problemen führen.
//    private fun createTable() {
//        val query = """
//            CREATE TABLE IF NOT EXISTS jumpandrun (
//                uuid VARCHAR(36) NOT NULL PRIMARY KEY,
//                points INT DEFAULT 0,
//                trys INT DEFAULT 0,
//                sound TINYINT(1) DEFAULT TRUE,
//                high_score INT DEFAULT 0
//            )
//            """.trimIndent()
//
//        try {
//            Base.exec(query)
//        } catch (e: Exception) {
//            Bukkit.getConsoleSender().sendMessage(e.message ?: this.javaClass.toString())
//        }
//    }

    suspend fun getHighsccores(count: Int = 10) = ConnectionWorkers.async {
        JumpAndRunPlayerModel.findAll().sortedByDescending { it.highScore }.take(count)
            .associate { it.uuid to it.highScore }
    }

    suspend fun getPoints(count: Int = 10) = ConnectionWorkers.async {
        JumpAndRunPlayerModel.findAll().sortedByDescending { it.points }.take(count)
            .associate { it.uuid to it.points }
    }
}