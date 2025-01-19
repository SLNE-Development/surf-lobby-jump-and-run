package dev.slne.surf.lobby.jar.mysql.worker

import dev.slne.surf.lobby.jar.mysql.DataSource
import org.javalite.activejdbc.Base
import java.sql.SQLException
import java.util.concurrent.ScheduledThreadPoolExecutor

class CoreConnectionWorker(
    private val dataSource: DataSource
) : ScheduledThreadPoolExecutor(dataSource.size()), ConnectionWorker {

    override fun beforeExecute(thread: Thread, runnable: Runnable) {
        dataSource.open()
    }

    override fun afterExecute(runnable: Runnable, throwable: Throwable) {
        dataSource.close()
    }

    override fun checkWorker(): Boolean {
        if (!Base.hasConnection()) {
            return false
        }

        val connection = Base.connection()

        return try {
            connection != null && !connection.isClosed
        } catch (e: SQLException) {
            false
        }
    }
}

