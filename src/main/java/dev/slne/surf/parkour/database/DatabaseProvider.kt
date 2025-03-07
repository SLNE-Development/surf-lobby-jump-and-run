package dev.slne.surf.parkour.database

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.gson.Gson
import com.sksamuel.aedile.core.expireAfterWrite
import com.sksamuel.aedile.core.withRemovalListener
import dev.hsbrysk.caffeine.buildCoroutine
import dev.slne.surf.parkour.leaderboard.LeaderboardSortingType
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.player.PlayerData
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.util.Area
import dev.slne.surf.parkour.util.MessageBuilder
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.util.Vector
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.notExists
import kotlin.time.Duration.Companion.days

object DatabaseProvider {
    private val config = plugin.config
    private val logger = ComponentLogger.logger(this.javaClass)
    private val gson = Gson()

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
        .buildCoroutine<UUID, PlayerData>(DatabaseProvider::loadPlayer)

    /**
     * Cache for parkours
     */
    private val parkourList = mutableObjectSetOf<Parkour>()

    /**
     * Player Data Table
     */
    object Users: Table() {
        val uuid = char("uuid", 36)
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

    object Parkours: Table() {
        val uuid = char("uuid", 36)
        val name = text("name")

        val worldUuid = char("world", 36)
        val area = text("area")
        val start = text("start")
        val respawn = text("respawn")

        val availableMaterials = text("available_materials")

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
                Database.connect("jdbc:sqlite:file:${dbFile.absolutePathString()}", "org.sqlite.JDBC")
                logger.info(MessageBuilder().withPrefix().success("Successfully connected to database with sqlite!").build())
            }

            "external" -> {
                Class.forName("com.mysql.cj.jdbc.Driver")
                Database.connect(
                    url = "jdbc:mysql://${config.getString("database.hostname")}:${config.getInt("database.port")}/${config.getString("database.database")}",
                    driver = "com.mysql.cj.jdbc.Driver",
                    user = config.getString("database.username") ?: return,
                    password = config.getString("database.password") ?: return
                )

                logger.info(MessageBuilder().withPrefix().success("Successfully connected to database with mysql!").build())
            }

            else -> {
                logger.warn(MessageBuilder().withPrefix().info("Unknown storage method \"$method\". Using local storage...").build())

                Class.forName("org.sqlite.JDBC")
                val dbFile = plugin.dataPath/"storage.db"

                if (!dbFile.exists()) {
                    dbFile.createDirectories()
                    dbFile.createFile()
                }
                Database.connect("jdbc:sqlite:file:${dbFile.absolutePathString()}", "org.sqlite.JDBC")

                logger.info(MessageBuilder().withPrefix().success("Successfully connected to database with sqlite!").build())
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
        withContext(Dispatchers.IO) {
            transaction {
                Users.replace {
                    it[uuid] = data.uuid.toString()
                    it[name] = data.name
                    it[highScore] = data.highScore
                    it[points] = data.points
                    it[trys] = data.trys
                    it[likesSound] = data.likesSound
                }
            }
        }
    }

    suspend fun savePlayers() {
        withContext(Dispatchers.IO) {
            val begin = System.currentTimeMillis()
            transaction {
                dataCache.synchronous().asMap().values.forEach { data ->
                    Users.replace {
                        it[uuid] = data.uuid.toString()
                        it[name] = data.name
                        it[highScore] = data.highScore
                        it[points] = data.points
                        it[trys] = data.trys
                        it[likesSound] = data.likesSound
                    }
                }
            }

            logger.info(MessageBuilder().withPrefix().info("Saved ${dataCache.asynchronous().asMap().values.size} player-data in ${System.currentTimeMillis() - begin}ms!").build())
        }
    }

    fun invalidate(uuid: UUID) {
        dataCache.synchronous().invalidate(uuid)
    }



    suspend fun loadPlayer(uuid: UUID): PlayerData {
        return withContext(Dispatchers.IO) {
            transaction {
                val result = Users.selectAll().where(Users.uuid.eq(uuid.toString())).firstOrNull() ?: return@transaction PlayerData(uuid, name = Bukkit.getOfflinePlayer(uuid).name ?: "Unknown")

                return@transaction PlayerData(
                    UUID.fromString(result[Users.uuid]),
                    result[Users.name],
                    result[Users.highScore],
                    result[Users.points],
                    result[Users.trys],
                    result[Users.likesSound]
                )
            }
        }
    }


    suspend fun fetchParkours() {
        val parkours = ObjectArraySet<Parkour>()

        withContext(Dispatchers.IO) {
            val begin = System.currentTimeMillis()

            transaction {
                Parkours.selectAll().map { it ->
                    parkours.add(
                        Parkour(
                            uuid = UUID.fromString(it[Parkours.uuid]),
                            name = it[Parkours.name],
                            world = Bukkit.getWorld(UUID.fromString(it[Parkours.worldUuid]))
                                ?: Bukkit.getWorlds().first(),
                            area = Area.fromString(it[Parkours.area]),
                            start = deserializeVector(it[Parkours.start]),
                            respawn = deserializeVector(it[Parkours.respawn]),
                            availableMaterials = ObjectArraySet(deserializeList(it[Parkours.availableMaterials]).map { Material.valueOf(it) }),
                            activePlayers = ObjectArraySet()
                        )
                    )
                }
            }

            logger.info(MessageBuilder().withPrefix().info("Fetched ${parkours.size} parkours in ${System.currentTimeMillis() - begin}ms!").build())
        }

        this.parkourList.addAll(parkours)
    }

    suspend fun saveParkours() {
        withContext(Dispatchers.IO) {
            val begin = System.currentTimeMillis()
            transaction {
                Parkours.deleteAll()

                parkourList.forEach { parkour ->
                    Parkours.insert { it ->
                        it[uuid] = parkour.uuid.toString()
                        it[name] = parkour.name
                        it[worldUuid] = parkour.world.uid.toString()
                        it[area] = parkour.area.toString()
                        it[start] = serializeVector(parkour.start)
                        it[respawn] = serializeVector(parkour.respawn)
                        it[availableMaterials] = serializeList(parkour.availableMaterials.map { it.name })
                    }
                }
            }

            logger.info(MessageBuilder().withPrefix().info("Saved ${parkourList.size} parkours in ${System.currentTimeMillis() - begin}ms!").build())
        }
    }

    suspend fun getEveryPlayerData(sortType: LeaderboardSortingType): ObjectList<PlayerData> {
        return withContext(Dispatchers.IO) {
            val uuids = ObjectArraySet<UUID>()

            transaction {
                uuids.addAll(Users.select(Users.uuid).map { UUID.fromString(it[Users.uuid]) })
            }

            for (mutableEntry in dataCache.synchronous().asMap()) {
                uuids.add(mutableEntry.key)
            }



            val playerDataList = uuids.map { async { getPlayerData(it) } }.awaitAll().toMutableList()

            when (sortType) {
                LeaderboardSortingType.POINTS_HIGHEST -> playerDataList.sortByDescending { it.points }
                LeaderboardSortingType.POINTS_LOWEST -> playerDataList.sortBy { it.points }
                LeaderboardSortingType.HIGHSCORE_HIGHEST -> playerDataList.sortByDescending { it.highScore }
                LeaderboardSortingType.HIGHSCORE_LOWEST -> playerDataList.sortBy { it.highScore }
                LeaderboardSortingType.NAME -> playerDataList.sortBy { it.name }
            }

            return@withContext ObjectArrayList(playerDataList)
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

    suspend fun getPlayerData(uuid: UUID): PlayerData {
        return dataCache.get(uuid)
    }

    fun getParkours(): ObjectSet<Parkour> {
        return this.parkourList
    }
}