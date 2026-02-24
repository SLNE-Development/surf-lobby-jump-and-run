package dev.slne.surf.parkour.paper.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.batchInsert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.parkour.paper.database.table.ParkourPlayerTexturesTable
import dev.slne.surf.parkour.paper.database.table.ParkourRunsTable
import dev.slne.surf.parkour.paper.service.parkourService
import dev.slne.surf.surfapi.bukkit.api.command.executors.anyExecutorSuspend
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import java.util.*

fun parkourDebugDatabaseCommand() = commandTree("debugparkourdb") {
    withPermission("surf.parkour.command.debugdb")

    literalArgument("clear") {
        anyExecutorSuspend { executor, _ ->
            suspendTransaction {
                ParkourRunsTable.deleteAll()
                ParkourPlayerTexturesTable.deleteAll()
            }

            executor.sendText {
                appendSuccessPrefix()
                success("Successfully deleted all parkour runs and player textures from the database.")
            }
        }
    }

    literalArgument("insert") {
        integerArgument("userAmount") {
            integerArgument("runAmount") {
                anyExecutorSuspend { executor, args ->
                    val userAmount: Int by args
                    val runAmount: Int by args
                    val parkour = parkourService.getParkours().first()

                    val random = kotlin.random.Random(System.nanoTime())

                    val users = List(userAmount) { UUID.randomUUID() }

                    suspendTransaction {
                        ParkourPlayerTexturesTable.batchInsert(users) { uuid ->
                            this[ParkourPlayerTexturesTable.playerUuid] = uuid
                            this[ParkourPlayerTexturesTable.playerName] =
                                "Debug${users.indexOf(uuid)}"
                            this[ParkourPlayerTexturesTable.texture] =
                                "texture_data_${users.indexOf(uuid)}"
                        }

                        val chunkSize = 1000
                        var inserted = 0

                        while (inserted < runAmount) {
                            val remaining = runAmount - inserted
                            val currentChunk = minOf(chunkSize, remaining)

                            ParkourRunsTable.batchInsert((0 until currentChunk)) {
                                this[ParkourRunsTable.parkourUuid] = parkour.uuid
                                this[ParkourRunsTable.playerUuid] =
                                    users[random.nextInt(users.size)]
                                this[ParkourRunsTable.runJumps] = random.nextInt(5, 50)
                                this[ParkourRunsTable.runTime] = random.nextLong(1_000, 300_000)
                            }

                            inserted += currentChunk
                        }
                    }

                    executor.sendMessage("§aDebugInsert abgeschlossen: $runAmount Runs erstellt.")
                }
            }
        }
    }
}