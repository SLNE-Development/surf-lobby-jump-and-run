package dev.slne.surf.lobby.jar.mysql.worker

import dev.slne.surf.lobby.jar.mysql.DataSource
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext

object ConnectionWorkers {
    private val worker = DataSource.createWorker()
    private val workerDispatcher = worker.asCoroutineDispatcher()

    suspend fun <T> async(supplier: () -> T): T = withContext(workerDispatcher) {
        try {
            supplier()
        } catch (ex: Throwable) {
            throw ex
        }
    }

    suspend fun asyncVoid(runnable: () -> Unit) = withContext(workerDispatcher) {
        try {
            runnable()
        } catch (ex: Throwable) {
            throw ex
        }
    }
}