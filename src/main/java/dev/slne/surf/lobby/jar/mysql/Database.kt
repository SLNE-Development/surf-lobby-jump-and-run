package dev.slne.surf.lobby.jar.mysql

import dev.slne.surf.lobby.jar.config.PluginConfig
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import org.bukkit.Bukkit
import org.javalite.activejdbc.Base
import org.javalite.activejdbc.Model
import java.util.*

object Database {
    fun createConnection() {
        try {
            val url = PluginConfig.config().getString("mysql.url")
            val user = PluginConfig.config().getString("mysql.user")
            val password = PluginConfig.config().getString("mysql.password")

            Base.open("com.mysql.jdbc.Driver", url, user, password)

            createTable()
        } catch (e: Exception) {
            Bukkit.getConsoleSender().sendMessage(e.message ?: this.javaClass.toString())
        }
    }

    private fun createTable() {
        val query = """
            CREATE TABLE IF NOT EXISTS jumpandrun (
                uuid VARCHAR(36) NOT NULL PRIMARY KEY,
                points INT DEFAULT 0,
                trys INT DEFAULT 0,
                sound TINYINT(1) DEFAULT TRUE,
                high_score INT DEFAULT 0
            )
            """.trimIndent()

        try {
            Base.exec(query)
        } catch (e: Exception) {
            Bukkit.getConsoleSender().sendMessage(e.message ?: this.javaClass.toString())
        }
    }

    fun closeConnection() {
        if (Base.hasConnection()) {
            Base.close()
        }
    }

    fun getTrys(uuid: UUID): Int? {
        var trys: Int? = null

        try {
            val result = Model.findFirst<JumpAndRunModel>("uuid = ?", uuid.toString())
            if (result != null) {
                trys = result.getInteger("trys")
            }
        } catch (e: Exception) {
            Bukkit.getConsoleSender().sendMessage(e.message ?: this.javaClass.toString())
        }

        return trys
    }

    fun getPoints(uuid: UUID): Int? {
        var points: Int? = null

        try {
            val result = Model.findFirst<JumpAndRunModel>("uuid = ?", uuid.toString())
            if (result != null) {
                points = result.getInteger("points")
            }
        } catch (e: Exception) {
            Bukkit.getConsoleSender().sendMessage(e.message ?: this.javaClass.toString())
        }

        return points
    }

    fun getHighScore(uuid: UUID): Int? {
        var highScore: Int? = null

        try {
            val result = Model.findFirst<JumpAndRunModel>("uuid = ?", uuid.toString())
            if (result != null) {
                highScore = result.getInteger("high_score")
            }
        } catch (e: Exception) {
            Bukkit.getConsoleSender().sendMessage(e.message ?: this.javaClass.toString())
        }

        return highScore
    }

    fun getSound(uuid: UUID): Boolean {
        var sound = true

        try {
            val result = Model.findFirst<JumpAndRunModel>("uuid = ?", uuid.toString())
            if (result != null) {
                sound = result.getBoolean("sound")
            }
        } catch (e: Exception) {
            Bukkit.getConsoleSender().sendMessage(e.message ?: this.javaClass.toString())
        }

        return sound
    }

    fun saveSound(uuid: UUID, value: Boolean?) {
        try {
            var model = Model.findFirst<JumpAndRunModel>("uuid = ?", uuid.toString())
            if (model == null) {
                model = JumpAndRunModel()
                model.set<Model>("uuid", uuid.toString())
            }

            model.set<Model>("sound", value)
            model.saveIt()
        } catch (e: Exception) {
            Bukkit.getConsoleSender().sendMessage(e.message ?: this.javaClass.toString())
        }
    }

    fun savePoints(uuid: UUID, points: Int?) {
        try {
            var model = Model.findFirst<JumpAndRunModel>("uuid = ?", uuid.toString())
            if (model == null) {
                model = JumpAndRunModel()
                model.set<Model>("uuid", uuid.toString())
            }

            model.set<Model>("points", points)
            model.saveIt()
        } catch (e: Exception) {
            Bukkit.getConsoleSender().sendMessage(e.message ?: this.javaClass.toString())
        }
    }

    fun saveTrys(uuid: UUID, trys: Int?) {
        try {
            var model = Model.findFirst<JumpAndRunModel>("uuid = ?", uuid.toString())
            if (model == null) {
                model = JumpAndRunModel()
                model.set<Model>("uuid", uuid.toString())
            }

            model.set<Model>("trys", trys)
            model.saveIt()
        } catch (e: Exception) {
            Bukkit.getConsoleSender().sendMessage(e.message ?: this.javaClass.toString())
        }
    }

    fun saveHighScore(uuid: UUID, highScore: Int?) {
        try {
            var model = Model.findFirst<JumpAndRunModel>("uuid = ?", uuid.toString())
            if (model == null) {
                model = JumpAndRunModel()
                model.set<Model>("uuid", uuid.toString())
            }

            model.set<Model>("high_score", highScore)
            model.saveIt()
        } catch (e: Exception) {
            Bukkit.getConsoleSender().sendMessage(e.message ?: this.javaClass.toString())
        }
    }

    val highScores: Object2ObjectMap<UUID, Int> get() {
            val highScores: Object2ObjectMap<UUID, Int> = Object2ObjectOpenHashMap()

            try {
                for (result in Model.findAll<Model>()) {
                    highScores[UUID.fromString(result.getString("uuid"))] =
                        result.getInteger("high_score")
                }
            } catch (e: Exception) {
                Bukkit.getConsoleSender().sendMessage(e.message ?: this.javaClass.toString())
            }
            return highScores
        }

    val points: Object2ObjectMap<UUID, Int> get() {
            val points: Object2ObjectMap<UUID, Int> = Object2ObjectOpenHashMap()

            try {
                for (result in Model.findAll<Model>()) {
                    points[UUID.fromString(result.getString("uuid"))] = result.getInteger("points")
                }
            } catch (e: Exception) {
                Bukkit.getConsoleSender().sendMessage(e.message ?: this.javaClass.toString())
            }
            return points
        }
}

internal class JumpAndRunModel : Model()
