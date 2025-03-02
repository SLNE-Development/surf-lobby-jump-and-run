package dev.slne.surf.lobby.jar.database

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.shynixn.mccoroutine.folia.launch
import com.google.gson.Gson
import dev.hsbrysk.caffeine.CoroutineLoadingCache
import dev.hsbrysk.caffeine.buildCoroutine
import dev.slne.surf.lobby.jar.PluginInstance
import dev.slne.surf.lobby.jar.config.PluginConfig
import dev.slne.surf.lobby.jar.player.PlayerData
import dev.slne.surf.lobby.jar.plugin
import dev.slne.surf.lobby.jar.util.MessageBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.bukkit.Location
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import java.util.concurrent.TimeUnit

object DatabaseProvider {
    private val config = PluginConfig.getConfig()
    private val logger = ComponentLogger.logger(this.javaClass)
    private val gson = Gson()

    /**
     * Cache for player data
     */

    private val dataCache: CoroutineLoadingCache<UUID, PlayerData> = Caffeine
        .newBuilder()
        .expireAfterWrite(30L, TimeUnit.DAYS)
        .removalListener<UUID, PlayerData> { uuid, data, _ ->
            plugin.launch {
                if (uuid != null && data != null) {
                    savePlayer(data)
                }
            }
        }
        .buildCoroutine(::loadPlayer)

    /**
     * Cache for parkour data
     */

    private val parkourCache: CoroutineLoadingCache<UUID, PlayerData> = Caffeine
        .newBuilder()
        .expireAfterWrite(30L, TimeUnit.DAYS)
        .removalListener<UUID, PlayerData> { uuid, data, _ ->
            plugin.launch {
                if (uuid != null && data != null) {
                    savePlayer(data)
                }
            }
        }
        .buildCoroutine(::loadPlayer)

    /**
     * Player Data Table
     */

    object Users: Table() {
        val uuid: Column<UUID> = uuid("uuid")
        val name: Column<String> = varchar("name", 16)
        val highScore: Column<Int> = integer("high_score")
        val points: Column<Int> = integer("points")
        val trys: Column<Int> = integer("trys")
        val likesSound: Column<Boolean> = bool("likes_sound")

        override val primaryKey = PrimaryKey(uuid)
    }

    /**
     * Parkour Data Table
     */

    object Parkours: Table() {
        val name: Column<String> = text("name")
        val world: Column<String> = text("world")
        val materials: Column<String> = text("materials")
        val displayName: Column<String> = text("display_name")
        val posOne: Column<String> = text("pos_one")
        val posTwo: Column<String> = text("pos_two")
        val spawn: Column<String> = text("spawn")
        val start: Column<String> = text("start")

        override val primaryKey = PrimaryKey(name)
    }

    /**
     * Connect to the database with the method given in the config
     */

    fun connect() {
        val method = PluginConfig.getConfig().getString("storage-method") ?: "local"

        when (method.lowercase()) {
            "local" -> {
                Class.forName("org.sqlite.JDBC")
                Database.connect("jdbc:sqlite:file:./plugins/SurfParkour/database.db", "org.sqlite.JDBC")
            }

            "external" -> {
                Class.forName("com.mysql.cj.jdbc.Driver")
                Database.connect(
                    url = "jdbc:mysql://${config.getString("database.hostname")}:${config.getInt("database.port")}/${config.getString("database.database")}",
                    driver = "com.mysql.cj.jdbc.Driver",
                    user = config.getString("database.username") ?: return,
                    password = config.getString("database.password") ?: return
                )
            }

            else -> {
                logger.warn(MessageBuilder().withPrefix().info("Unknown storage method \"$method\". Using local storage...").build())

                Class.forName("org.sqlite.JDBC")
                Database.connect("jdbc:sqlite:file:./plugins/SurfParkour/database.db", "org.sqlite.JDBC")
            }
        }

        transaction {
            SchemaUtils.create(Users, Parkours)
        }
    }

    private suspend fun savePlayer(data: PlayerData) {
        withContext(Dispatchers.IO) {
            transaction {
                Users.insert {
                    it[uuid] = data.uuid
                    it[name] = data.name
                    it[highScore] = data.highScore
                    it[points] = data.points
                    it[trys] = data.trys
                    it[likesSound] = data.likesSound
                }
            }
        }
    }

    suspend fun loadPlayer(uuid: UUID): PlayerData? {
        return withContext(Dispatchers.IO) {
            transaction {
                Users.select(Users.uuid eq uuid).map {
                    PlayerData(
                        it[Users.uuid],
                        it[Users.name],
                        it[Users.highScore],
                        it[Users.points],
                        it[Users.trys],
                        it[Users.likesSound]
                    )
                }.firstOrNull()
            }
        }
    }

    suspend fun loadPlayer(name: String): PlayerData? {
        return withContext(Dispatchers.IO) {
            transaction {
                Users.select(Users.name eq name).map {
                    PlayerData(
                        it[Users.uuid],
                        it[Users.name],
                        it[Users.highScore],
                        it[Users.points],
                        it[Users.trys],
                        it[Users.likesSound]
                    )
                }.firstOrNull()
            }
        }
    }

    private fun serializeLocation(location: Location): String {
        return gson.toJson(location)
    }

    private fun deserializeLocation(json: String): Location {
        return gson.fromJson(json, Location::class.java)
    }

    private fun serializeList(list: List<String>): String {
        return gson.toJson(list)
    }

    private fun deserializeList(json: String): List<String> {
        return gson.fromJson(json, Array<String>::class.java).toList()
    }
}