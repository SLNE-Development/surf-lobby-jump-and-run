package dev.slne.surf.parkour.database

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.asLoadingCache
import com.sksamuel.aedile.core.expireAfterWrite
import com.sksamuel.aedile.core.withRemovalListener
import dev.slne.surf.parkour.leaderboard.LeaderboardSortingType
import dev.slne.surf.parkour.parkour.Parkour
import dev.slne.surf.parkour.player.PlayerData
import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.serialization.ParkourSerializationModule
import dev.slne.surf.parkour.util.Area
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import dev.slne.surf.surfapi.core.api.util.toMutableObjectSet
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.util.Vector
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.minutes


object DatabaseProvider {
    private val log = logger()
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
        .expireAfterWrite(30.minutes)
        .withRemovalListener { uuid, data, _ ->
            if (uuid != null && data != null) {
                savePlayer(data as PlayerData)
            }
        }
        .asLoadingCache(DatabaseProvider::loadPlayer)

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

        val availableMaterials = text("available_materials").transform(
            wrap = { json.decodeFromString<MutableSet<Material>>(it) },
            unwrap = { json.encodeToString(it) }
        )

        override val primaryKey = PrimaryKey(uuid)
    }

    suspend fun connect() {
        val dbFile = File(plugin.dataFolder, "storage.db")

        if(!dbFile.exists()) {
            try {
                dbFile.parentFile.mkdirs()

                withContext(Dispatchers.IO) {
                    dbFile.createNewFile()
                }

            } catch (ex: IOException) {
                error("An error occurred while creating database file.")
            }
        }

        dev.slne.surf.database.DatabaseProvider(plugin.dataPath, dbFile.parentFile.toPath())
            .connect()

        newSuspendedTransaction {
            SchemaUtils.create(
                Users, Parkours
            )
        }
    }

    fun updatePlayerData(data: PlayerData) {
        dataCache.put(data.uuid, data)
    }

    private suspend fun savePlayer(data: PlayerData) {
        newSuspendedTransaction {
            Users.replace {
                it[uuid] = data.uuid
                it[name] = data.name
                it[highScore] = data.highScore
                it[points] = data.points
                it[trys] = data.trys
                it[likesSound] = data.likesSound
            }
        }
    }

    suspend fun savePlayers() {
        val duration = measureTimeMillis {
            newSuspendedTransaction(Dispatchers.IO) {

                val values = dataCache.asMap().values

                Users.batchReplace(
                    values,
                    false
                ) {
                    this[Users.uuid] = it.uuid
                    this[Users.name] = it.name
                    this[Users.highScore] = it.highScore
                    this[Users.points] = it.points
                    this[Users.trys] = it.trys
                    this[Users.likesSound] = it.likesSound
                }

            }
        }

        log.atInfo().log("Saved %d player-data in %dms!", dataCache.asMap().values.size, duration)
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

        log.atInfo().log("Fetched %d parkours in %dms!", parkours.size, duration)

        this.parkourList.addAll(parkours)
    }

    suspend fun saveParkours() {
        val duration = measureTimeMillis {
            newSuspendedTransaction(Dispatchers.IO) {
                Parkours.deleteAll()

                parkourList.forEach { parkour ->
                    Parkours.insert {
                        it[uuid] = parkour.uuid
                        it[name] = parkour.name
                        it[world] = parkour.world
                        it[area] = parkour.area
                        it[start] = parkour.start
                        it[respawn] = parkour.respawn
                        it[availableMaterials] = parkour.availableMaterials
                    }
                }
            }
        }

        log.atInfo()
            .log("Saved %d parkours in %dms!", parkourList.size, duration)
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

    suspend fun getPlayerData(uuid: UUID): PlayerData {
        return dataCache.get(uuid)
    }

    fun getParkours(): ObjectSet<Parkour> {
        return this.parkourList
    }
}