package dev.slne.surf.lobby.jar.mysql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.slne.surf.lobby.jar.mysql.worker.ConnectionWorker
import dev.slne.surf.lobby.jar.mysql.worker.CoreConnectionWorker
import dev.slne.surf.lobby.jar.plugin
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.javalite.activejdbc.Base
import org.javalite.activejdbc.DB
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

object DataSource {

    private val hikariConfig: HikariConfig = HikariConfig()
    private val dataSource: HikariDataSource

    private val workers: ObjectSet<ConnectionWorker> = ObjectOpenHashSet()

    init {
        val config = plugin.config
        val dbType = config.getString("mysql.type", "mariadb")
        val hostname = config.getString("mysql.hostname")
        val port = config.getInt("mysql.port")
        val databaseName = config.getString("mysql.database")

        hikariConfig.apply {
            driverClassName = config.getString("mysql.driver", "org.mariadb.jdbc.Driver")
            poolName = config.getString("mysql.poolName", "surf-lobby-jnr")

            jdbcUrl = "jdbc:$dbType://$hostname:$port/$databaseName"
            username = config.getString("mysql.username")
            password = config.getString("mysql.password")

            addDataSourceProperty(
                "cachePrepStmts",
                config.getBoolean("mysql.cachePreparedStatements", true)
            )
            addDataSourceProperty(
                "prepStmtCacheSize",
                config.getInt("mysql.preparedStatementsCacheSize", 250)
            )
            addDataSourceProperty(
                "prepStmtCacheSqlLimit",
                config.getInt("mysql.preparedStatementsCacheSqlLimit", 2048)
            )

            isAutoCommit = config.getBoolean("mysql.autoCommit", true)
            connectionTimeout = config.getLong("mysql.connectionTimeout", 30000)
            idleTimeout = config.getLong("mysql.idleTimeout", 600000)
            maxLifetime = config.getLong("mysql.maxLifetime", 1800000)
            minimumIdle = config.getInt("mysql.minimumIdle", 10)
            maximumPoolSize = config.getInt("mysql.maximumPoolSize", 10)
        }

        dataSource = HikariDataSource(hikariConfig)
    }

    fun size(): Int {
        return dataSource.maximumPoolSize
    }

    fun open(): DB? {
        if (Base.hasConnection()) {
            return null
        }

        return Base.open(dataSource)
    }

    fun close() {
        if (Base.hasConnection()) {
            Base.close()
        }
    }

    fun createWorker(): ConnectionWorker {
        if (!workers.isEmpty()) {
            return workers.iterator().next()
        }

        var connectionWorker: CoreConnectionWorker? = null

        try {
            connectionWorker = CoreConnectionWorker(this)
        } catch (exception: ExecutionException) {
            throw RuntimeException("Failed to create connection worker", exception)
        }

        workers.add(connectionWorker)

        return connectionWorker
    }

    fun closeAll() {
        workers.forEach(Consumer { worker: ConnectionWorker ->
            worker.shutdown()

            try {
                worker.awaitTermination(10, TimeUnit.SECONDS)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        })

        // Close hikari datasource
        dataSource.close()

        // Close all workers
        workers.clear()

        // Close active jdbc base connection if present
        close()
    }
}
