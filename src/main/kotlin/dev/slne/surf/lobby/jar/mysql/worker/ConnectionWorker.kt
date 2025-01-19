package dev.slne.surf.lobby.jar.mysql.worker

import java.util.concurrent.ExecutorService

interface ConnectionWorker : ExecutorService {
    /**
     * Returns true if in worker thread
     *
     * @return true if in worker thread
     */
    fun checkWorker(): Boolean
}
