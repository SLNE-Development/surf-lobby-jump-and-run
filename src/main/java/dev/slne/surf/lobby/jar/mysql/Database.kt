package dev.slne.surf.lobby.jar.mysql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.slne.surf.lobby.jar.plugin
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import org.bukkit.Bukkit
import org.javalite.activejdbc.Base
import org.javalite.activejdbc.Model
import java.util.*
import javax.sql.DataSource

object Database {
    private const val DRIVER_CLASS_NAME_KEY = "mysql.driver"
    private const val POOL_NAME_KEY = "mysql.poolName"
    private const val URL_KEY = "mysql.url"
    private const val DB_TYPE_KEY = "mysql.type"
    private const val HOSTNAME_KEY = "mysql.hostname"
    private const val PORT_KEY = "mysql.port"
    private const val DATABASE_NAME_KEY = "mysql.database"
    private const val USERNAME_KEY = "mysql.username"
    private const val PASSWORD_KEY = "mysql.password"
    private const val TABLE_KEY = "mysql.table"

    private var driverClassName = "org.mariadb.jdbc.Driver"
    private var poolName = "surf-lobby-jnr"
    private var jdbcUrl = "url"
    private var username = "user"
    private var password = "password"
    private var table = "table"
    private var url = "url"

    private lateinit var dataSource: DataSource

    val highScores: Object2ObjectMap<UUID, Int> get() = getValues("high_score")
    val points: Object2ObjectMap<UUID, Int> get() = getValues("points")

    init {
        val config = plugin.config
        val dbType = config.getString(DB_TYPE_KEY, "mariadb")
        val hostname = config.getString(HOSTNAME_KEY)
        val port = config.getInt(PORT_KEY)
        val databaseName = config.getString(DATABASE_NAME_KEY)

        driverClassName = config.getString(DRIVER_CLASS_NAME_KEY, driverClassName) ?: driverClassName
        poolName = config.getString(POOL_NAME_KEY, poolName) ?: poolName
        url = config.getString(URL_KEY) ?: url

        jdbcUrl = "jdbc:$dbType://$hostname:$port/$databaseName"
        username = config.getString(USERNAME_KEY) ?: username
        password = config.getString(PASSWORD_KEY) ?: password
        table = config.getString(TABLE_KEY) ?: table

        setupDataSource()
    }

    private fun setupDataSource() {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = this@Database.jdbcUrl
            driverClassName = this@Database.driverClassName
            username = this@Database.username
            password = this@Database.password
            poolName = this@Database.poolName
            maximumPoolSize = 10
            minimumIdle = 2
            idleTimeout = 30000
            connectionTimeout = 30000
        }
        dataSource = HikariDataSource(hikariConfig)
    }

    fun createConnection() {
        try {
            Base.open(dataSource)
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

    fun getTrys(uuid: UUID): Int = getValue(uuid, "trys")
    fun getPoints(uuid: UUID): Int = getValue(uuid, "points")
    fun getHighScore(uuid: UUID): Int = getValue(uuid, "high_score")
    fun getSound(uuid: UUID): Boolean = getValue(uuid, "sound") == 1
    fun saveSound(uuid: UUID, value: Boolean?) = saveValue(uuid, "sound", value)
    fun savePoints(uuid: UUID, points: Int?) = saveValue(uuid, "points", points)
    fun saveTrys(uuid: UUID, trys: Int?) = saveValue(uuid, "trys", trys)
    fun saveHighScore(uuid: UUID, highScore: Int?) = saveValue(uuid, "high_score", highScore)

    private fun getValue(uuid: UUID, column: String): Int {
        return try {
            Model.findFirst<JumpAndRunModel>("uuid = ?", uuid.toString())?.getInteger(column) ?: 0
        } catch (e: Exception) {
            Bukkit.getConsoleSender().sendMessage(e.message ?: this.javaClass.toString())
            0
        }
    }

    private fun saveValue(uuid: UUID, column: String, value: Any?) {
        try {
            val model = Model.findFirst("uuid = ?", uuid.toString()) ?: JumpAndRunModel().apply {
                set<Model>("uuid", uuid.toString())
            }
            model.set<Model>(column, value)
            model.saveIt()
        } catch (e: Exception) {
            Bukkit.getConsoleSender().sendMessage(e.message ?: this.javaClass.toString())
        }
    }

    private fun getValues(column: String): Object2ObjectMap<UUID, Int> {
        val values: Object2ObjectMap<UUID, Int> = Object2ObjectOpenHashMap()
        try {
            for (result in Model.findAll<Model>()) {
                values[UUID.fromString(result.getString("uuid"))] = result.getInteger(column)
            }
        } catch (e: Exception) {
            Bukkit.getConsoleSender().sendMessage(e.message ?: this.javaClass.toString())
        }
        return values
    }
}

internal class JumpAndRunModel : Model()