package dev.slne.surf.parkour.database

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.gson.Gson
import com.sksamuel.aedile.core.asLoadingCache
import com.sksamuel.aedile.core.expireAfterWrite
import com.sksamuel.aedile.core.withRemovalListener
import dev.slne.surf.parkour.leaderboard.LeaderboardSortingType
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.player.PlayerData
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.serialization.ParkourSerializationModule
import dev.slne.surf.parkour.util.Area
import dev.slne.surf.parkour.util.MessageBuilder
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import dev.slne.surf.surfapi.core.api.util.toMutableObjectSet
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.util.Vector
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*
import kotlin.io.path.*
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.days


object DatabaseProvider {
    private val config = plugin.config
    private val logger = ComponentLogger.logger(this.javaClass)
    private val gson = Gson()
    private val json = Json {
        ignoreUnknownKeys = true
        serializersModule = SerializersModule {
            ParkourSerializationModule.register(this)
        }
    }

    /**
     * Cache for player data
     */

    private val dataCache = Caffeine.newBuilder()
        .expireAfterWrite(30.days)
        .withRemovalListener { uuid, data, _ ->
            if (uuid != null && data != null) {
                savePlayer(data as PlayerData)
            }
        }
        .asLoadingCache<UUID, PlayerData>(DatabaseProvider::loadPlayer)

    /**
     * Cache for parkours
     */
    private val parkourList = mutableObjectSetOf<Parkour>()

    /**
     * Player Data Table
     */
    object Users : Table() {
        val uuid = char("uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
        val name = char("name", 16)
        val highScore = integer("high_score")
        val points = integer("points")
        val trys = integer("trys")
        val likesSound = bool("likes_sound")

        override val primaryKey = PrimaryKey(uuid)
    }

    /**
     * Parkour Data Table
     */

    object Parkours : Table() {
        val uuid = char("uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
        val name = text("name")

        val world = char("world", 36).transform(
            wrap = { Bukkit.getWorld(UUID.fromString(it)) ?: Bukkit.getWorlds().first() },
            unwrap = { it.uid.toString() }
        )
        val area = text("area").transform(
            wrap = { json.decodeFromString<Area>(it) },
            unwrap = { json.encodeToString(it) }
        )
        val start = text("start").transform(
            wrap = { json.decodeFromString<Vector>(it) },
            unwrap = { json.encodeToString(it) }
        )
        val respawn = text("respawn").transform(
            wrap = { json.decodeFromString<Vector>(it) },
            unwrap = { json.encodeToString(it) }
        )

        val availableMaterials =
            text("available_materials").transform( // TODO: 08.03.2025 10:15 - check set serialization
                wrap = { json.decodeFromString<MutableSet<Material>>(it) },
                unwrap = { json.encodeToString(it) }
        )

        override val primaryKey = PrimaryKey(uuid)
    }

    /**
     * Connect to the database with the method given in the config
     */
    suspend fun connect() {
        val method = config.getString("storage-method") ?: "local"

        when (method.lowercase()) {
            "local" -> {
                Class.forName("org.sqlite.JDBC")
                val dbFile = plugin.dataPath / "storage.db"

                if (dbFile.notExists()) {
                    dbFile.createDirectories()
                    dbFile.createFile()
                }
                Database.connect(
                    "jdbc:sqlite:file:${dbFile.absolutePathString()}",
                    "org.sqlite.JDBC"
                )
                logger.info(
                    MessageBuilder().withPrefix()
                        .success("Successfully connected to database with sqlite!").build()
                )
            }

            "external" -> {
                Class.forName("com.mysql.cj.jdbc.Driver")
                Database.connect(
                    url = "jdbc:mysql://${config.getString("database.hostname")}:${config.getInt("database.port")}/${
                        config.getString(
                            "database.database"
                        )
                    }",
                    driver = "com.mysql.cj.jdbc.Driver",
                    user = config.getString("database.username") ?: return,
                    password = config.getString("database.password") ?: return
                )

                logger.info(
                    MessageBuilder().withPrefix()
                        .success("Successfully connected to database with mysql!").build()
                )
            }

            else -> {
                logger.warn(
                    MessageBuilder().withPrefix()
                        .info("Unknown storage method \"$method\". Using local storage...").build()
                )

                Class.forName("org.sqlite.JDBC")
                val dbFile = plugin.dataPath / "storage.db"

                if (!dbFile.exists()) {
                    dbFile.createDirectories()
                    dbFile.createFile()
                }
                Database.connect(
                    "jdbc:sqlite:file:${dbFile.absolutePathString()}",
                    "org.sqlite.JDBC"
                )

                logger.info(
                    MessageBuilder().withPrefix()
                        .success("Successfully connected to database with sqlite!").build()
                )
            }
        }

        newSuspendedTransaction {
            SchemaUtils.create(
                Users,
                Parkours
            )
        }
    }

    fun updatePlayerData(data: PlayerData) {
        dataCache.put(data.uuid, data)
    }

    private suspend fun savePlayer(data: PlayerData) {
        newSuspendedTransaction(Dispatchers.IO) {
            replaceUser(data)
        }
    }

    suspend fun savePlayers() {
        val duration = measureTimeMillis {
            newSuspendedTransaction(Dispatchers.IO) {
//                dataCache.synchronous().asMap().values.forEach { data ->
//                    replaceUser(data)
//                }

                val values = dataCache.asMap().values
                val result =
                    Users.batchReplace(
                        values,
                        false
                    ) { // TODO: 08.03.2025 09:34 - verify that batchReplace works
                        this[Users.uuid] = it.uuid
                        this[Users.name] = it.name
                        this[Users.highScore] = it.highScore
                        this[Users.points] = it.points
                        this[Users.trys] = it.trys
                        this[Users.likesSound] = it.likesSound
                    }
            }
        }

        logger.info(
            MessageBuilder().withPrefix().info(
                "Saved ${
                    dataCache.asMap().values.size
                } player-data in ${duration}ms!"
            ).build()
        )
    }

    private fun replaceUser(data: PlayerData) {
        Users.replace {
            it[uuid] = data.uuid
            it[name] = data.name
            it[highScore] = data.highScore
            it[points] = data.points
            it[trys] = data.trys
            it[likesSound] = data.likesSound
        }
    }

    fun invalidate(uuid: UUID) {
        dataCache.invalidate(uuid)
    }

    suspend fun loadPlayer(uuid: UUID) = newSuspendedTransaction(Dispatchers.IO) {
        Users.selectAll()
            .where { Users.uuid eq uuid }
            .singleOrNull()
            ?.let {
                PlayerData(
                    it[Users.uuid],
                    it[Users.name],
                    it[Users.highScore],
                    it[Users.points],
                    it[Users.trys],
                    it[Users.likesSound]
                )
            } ?: PlayerData(uuid, name = Bukkit.getOfflinePlayer(uuid).name ?: "Unknown")
    }


    suspend fun fetchParkours() {
        val parkours = mutableObjectSetOf<Parkour>()

        val duration = measureTimeMillis {
            newSuspendedTransaction(Dispatchers.IO) {
                Parkours.selectAll().mapTo(parkours) {
                    Parkour(
                        uuid = it[Parkours.uuid],
                        name = it[Parkours.name],
                        world = it[Parkours.world],
                        area = it[Parkours.area],
                        start = it[Parkours.start],
                        respawn = it[Parkours.respawn],
                        availableMaterials = it[Parkours.availableMaterials].toMutableObjectSet()
                    )
                }
            }
        }

        logger.info(
            MessageBuilder()
                .withPrefix()
                .info("Fetched ${parkours.size} parkours in ${duration}ms!")
                .build()
        )

        this.parkourList.addAll(parkours)
    }

    suspend fun saveParkours() {
        val duration = measureTimeMillis {
            newSuspendedTransaction(Dispatchers.IO) {
                Parkours.deleteAll()
                Parkours.batchInsert(parkourList, false) {
                    this[Parkours.uuid] = it.uuid
                    this[Parkours.name] = it.name
                    this[Parkours.world] = it.world
                    this[Parkours.area] = it.area
                    this[Parkours.start] = it.start
                    this[Parkours.respawn] = it.respawn
                    this[Parkours.availableMaterials] = it.availableMaterials
                }
            }
        }

        logger.info(
            MessageBuilder()
                .withPrefix()
                .info("Saved ${parkourList.size} parkours in ${duration}ms!")
                .build()
        )
    }

    suspend fun getEveryPlayerData(sortType: LeaderboardSortingType): ObjectList<PlayerData> {
        val uuids = mutableObjectSetOf<UUID>()

        newSuspendedTransaction(Dispatchers.IO) {
            Users.select(Users.uuid).mapTo(uuids) { it[Users.uuid] }
        }

        uuids.addAll(dataCache.asMap().keys)
        val playerDataList = mutableObjectListOf(dataCache.getAll(uuids).values)
        sortType.sort(playerDataList)

        return playerDataList
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

    suspend fun getPlayerData(uuid: UUID): PlayerData {
        return dataCache.get(uuid)
    }

    fun getParkours(): ObjectSet<Parkour> {
        return this.parkourList
    }
}