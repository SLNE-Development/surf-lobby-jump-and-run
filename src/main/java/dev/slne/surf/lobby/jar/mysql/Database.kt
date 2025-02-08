package dev.slne.surf.lobby.jar.mysql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.slne.surf.lobby.jar.config.PluginConfig
import org.bukkit.Bukkit
import java.sql.Connection
import java.sql.SQLException
import java.util.*
import javax.sql.DataSource
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

object Database {
    private const val DRIVER_CLASS_NAME_KEY = "mysql.driver"
    private const val DB_TYPE_KEY = "mysql.type"
    private const val HOSTNAME_KEY = "mysql.hostname"
    private const val PORT_KEY = "mysql.port"
    private const val DATABASE_NAME_KEY = "mysql.database"
    private const val USERNAME_KEY = "mysql.username"
    private const val PASSWORD_KEY = "mysql.password"
    private const val TABLE_KEY = "mysql.table"

    private var driverClassName = "org.mariadb.jdbc.Driver"
    private var jdbcUrl = "url"
    private var username = "user"
    private var password = "password"
    private var table = "table"

    private lateinit var dataSource: DataSource

    val highScores: Object2ObjectMap<UUID, Int> get() = getValues("high_score")
    val points: Object2ObjectMap<UUID, Int> get() = getValues("points")

    private fun setupDataSource() {
        val config = PluginConfig.getConfig()
        val dbType = config.getString(DB_TYPE_KEY, "mariadb")
        val hostname = config.getString(HOSTNAME_KEY)
        val port = config.getInt(PORT_KEY)
        val databaseName = config.getString(DATABASE_NAME_KEY)

        driverClassName = config.getString(DRIVER_CLASS_NAME_KEY, driverClassName) ?: driverClassName

        jdbcUrl = "jdbc:$dbType://$hostname:$port/$databaseName"
        username = config.getString(USERNAME_KEY) ?: username
        password = config.getString(PASSWORD_KEY) ?: password
        table = config.getString(TABLE_KEY) ?: table

        val hikariConfig = HikariConfig().apply {
            jdbcUrl = this@Database.jdbcUrl
            driverClassName = this@Database.driverClassName
            username = this@Database.username
            password = this@Database.password
            maximumPoolSize = 10
            minimumIdle = 2
            idleTimeout = 30000
            connectionTimeout = 30000
        }
        dataSource = HikariDataSource(hikariConfig)
    }

    fun createConnection() {
        try {
            setupDataSource()
            createTableIfNotExists()
        } catch (e: Exception) {
            Bukkit.getConsoleSender().sendMessage(e.message ?: this.javaClass.toString())
        }
    }

    fun closeConnection() {
        if (dataSource is HikariDataSource) {
            (dataSource as HikariDataSource).close()
        }
    }

    private fun getConnection(): Connection {
        return dataSource.connection
    }

    private fun createTableIfNotExists() {
        getConnection().use { connection ->
            val statement = connection.createStatement()
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS $table (
                    uuid VARCHAR(36) PRIMARY KEY,
                    points INT DEFAULT 0,
                    high_score INT DEFAULT 0,
                    trys INT DEFAULT 0,
                    sound BOOLEAN DEFAULT TRUE
                )
            """.trimIndent())
        }
    }

    @Throws(SQLException::class)
    fun getPoints(uuid: UUID): Int {
        getConnection().use { connection ->
            val statement = connection.prepareStatement("SELECT points FROM $table WHERE uuid = ?")
            statement.setString(1, uuid.toString())
            val resultSet = statement.executeQuery()
            return if (resultSet.next()) {
                resultSet.getInt("points")
            } else {
                0
            }
        }
    }

    @Throws(SQLException::class)
    fun getHighScore(uuid: UUID): Int {
        getConnection().use { connection ->
            val statement = connection.prepareStatement("SELECT high_score FROM $table WHERE uuid = ?")
            statement.setString(1, uuid.toString())
            val resultSet = statement.executeQuery()
            return if (resultSet.next()) {
                resultSet.getInt("high_score")
            } else {
                0
            }
        }
    }

    @Throws(SQLException::class)
    fun getTrys(uuid: UUID): Int {
        getConnection().use { connection ->
            val statement = connection.prepareStatement("SELECT trys FROM $table WHERE uuid = ?")
            statement.setString(1, uuid.toString())
            val resultSet = statement.executeQuery()
            return if (resultSet.next()) {
                resultSet.getInt("trys")
            } else {
                0
            }
        }
    }

    @Throws(SQLException::class)
    fun getSound(uuid: UUID): Boolean {
        getConnection().use { connection ->
            val statement = connection.prepareStatement("SELECT sound FROM $table WHERE uuid = ?")
            statement.setString(1, uuid.toString())
            val resultSet = statement.executeQuery()
            return if (resultSet.next()) {
                resultSet.getBoolean("sound")
            } else {
                true
            }
        }
    }

    @Throws(SQLException::class)
    fun saveSound(uuid: UUID, sound: Boolean) {
        getConnection().use { connection ->
            val statement = connection.prepareStatement("UPDATE $table SET sound = ? WHERE uuid = ?")
            statement.setBoolean(1, sound)
            statement.setString(2, uuid.toString())
            statement.executeUpdate()
        }
    }

    @Throws(SQLException::class)
    fun saveTrys(uuid: UUID, trys: Int) {
        getConnection().use { connection ->
            val statement = connection.prepareStatement("UPDATE $table SET trys = ? WHERE uuid = ?")
            statement.setInt(1, trys)
            statement.setString(2, uuid.toString())
            statement.executeUpdate()
        }
    }

    @Throws(SQLException::class)
    fun savePoints(uuid: UUID, points: Int) {
        getConnection().use { connection ->
            val statement = connection.prepareStatement("UPDATE $table SET points = ? WHERE uuid = ?")
            statement.setInt(1, points)
            statement.setString(2, uuid.toString())
            statement.executeUpdate()
        }
    }

    @Throws(SQLException::class)
    fun saveHighScore(uuid: UUID, highScore: Int) {
        getConnection().use { connection ->
            val statement = connection.prepareStatement("UPDATE $table SET high_score = ? WHERE uuid = ?")
            statement.setInt(1, highScore)
            statement.setString(2, uuid.toString())
            statement.executeUpdate()
        }
    }

    private fun getValues(column: String): Object2ObjectMap<UUID, Int> {
        val values: Object2ObjectMap<UUID, Int> = Object2ObjectOpenHashMap()
        getConnection().use { connection ->
            val statement = connection.prepareStatement("SELECT uuid, $column FROM $table")
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                values[UUID.fromString(resultSet.getString("uuid"))] = resultSet.getInt(column)
            }
        }
        return values
    }
}