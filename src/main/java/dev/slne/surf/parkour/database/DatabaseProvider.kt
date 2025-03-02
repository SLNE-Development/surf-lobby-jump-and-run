package dev.slne.surf.parkour.database

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.shynixn.mccoroutine.bukkit.launch
import com.google.gson.Gson
import dev.hsbrysk.caffeine.CoroutineLoadingCache
import dev.hsbrysk.caffeine.buildCoroutine
import dev.slne.surf.parkour.config.PluginConfig
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.player.PlayerData
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.util.Area
import dev.slne.surf.parkour.util.MessageBuilder
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.bukkit.Bukkit
import org.bukkit.Material

import org.bukkit.util.Vector
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
        .buildCoroutine(DatabaseProvider::loadPlayer)

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
        .buildCoroutine(DatabaseProvider::loadPlayer)

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
        val uuid: Column<UUID> = uuid("uuid")
        val name: Column<String> = text("name")

        val world: Column<String> = text("world")
        val area: Column<String> = text("area")
        val start: Column<String> = text("start")
        val respawn: Column<String> = text("respawn")

        val availableMaterials: Column<String> = text("available_materials")

        override val primaryKey = PrimaryKey(uuid)
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
            SchemaUtils.create(
                Users,
                Parkours
            )
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

    suspend fun fetchParkours(): ObjectSet<Parkour> {
        val parkours = ObjectArraySet<Parkour>()

        withContext(Dispatchers.IO) {
            transaction {
                Parkours.selectAll().map {
                    parkours.add(
                        Parkour(
                            uuid = UUID.fromString(it[Parkours.uuid].toString()),
                            name = it[Parkours.name],
                            world = Bukkit.getWorld(it[Parkours.world]) ?: Bukkit.getWorlds().first(),
                            area = Area.fromString(it[Parkours.area]),
                            start = deserializeVector(it[Parkours.start]),
                            respawn = deserializeVector(it[Parkours.respawn]),
                            availableMaterials = ObjectArraySet(deserializeList(it[Parkours.availableMaterials]).map { Material.valueOf(it) }),
                            activePlayers = ObjectArraySet()
                        )
                    )
                }
            }
        }

        return parkours
    }

    suspend fun saveParkour(parkour: Parkour) {
        withContext(Dispatchers.IO) {
            transaction {
                Parkours.insert {
                    it[uuid] = parkour.uuid
                    it[name] = parkour.name
                    it[world] = parkour.world.name
                    it[area] = parkour.area.toString()
                    it[start] = serializeVector(parkour.start)
                    it[respawn] = serializeVector(parkour.respawn)
                    it[availableMaterials] = serializeList(parkour.availableMaterials.map { it.name })
                }
            }
        }
    }

    private fun serializeVector(vector: Vector): String {
        return gson.toJson(vector)
    }

    private fun deserializeVector(json: String): Vector {
        return gson.fromJson(json, Vector::class.java)
    }

    private fun serializeList(list: List<String>): String {
        return gson.toJson(list)
    }

    private fun deserializeList(json: String): List<String> {
        return gson.fromJson(json, Array<String>::class.java).toList()
    }
}