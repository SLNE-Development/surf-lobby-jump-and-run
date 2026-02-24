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

private val largeTexture = """
ewogICJ0aW1lc3RhbXAiIDogMTc3MTk1MDc0NDc3OSwKICAicHJvZmlsZUlkIiA6ICIxYzc3OWNiMTM4NjA0ZTIzOWNhYzdmMTYwYjJhY2M2MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVCam9SZWRDcmFmdCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yY2NmNWZmMjA5YzA5ZjdkMDc0MzMyN2NmMWUyNWZjN2E1NjViYTBkMjlhY2YxZGU2M2YzOWM1NDU5YzI2YWUzIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0sCiAgICAiQ0FQRSIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzY1OGM1MDI1Yzc3Y2ZhYzc1NzRhYWIzYWY5NGE0NmE4ODg2ZTNiNzcyMmE4OTUyNTVmYmYyMmFiODY1MjQzNCIKICAgIH0KICB9Cn0=
""".trimIndent()

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
                        executor.sendMessage("§aInserting $userAmount users...")

                        val users = ArrayList<UUID>(userAmount).apply {
                            repeat(userAmount) { add(UUID.randomUUID()) }
                        }

                        ParkourPlayerTexturesTable.batchInsert(users.withIndex()) { (index, uuid) ->
                            this[ParkourPlayerTexturesTable.playerUuid] = uuid
                            this[ParkourPlayerTexturesTable.playerName] = "Debug$index"
                            this[ParkourPlayerTexturesTable.texture] = largeTexture
                        }

                        executor.sendMessage("§aInserting $runAmount runs...")

                        val chunkSize = 10000
                        var inserted = 0
                        val startTime = System.currentTimeMillis()

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

                            val elapsed = System.currentTimeMillis() - startTime
                            val avgPerRun = elapsed.toDouble() / inserted
                            val estimatedTotal = avgPerRun * runAmount
                            val remainingTime = estimatedTotal - elapsed

                            val remainingSeconds = (remainingTime / 1000).toLong()
                            val minutes = remainingSeconds / 60
                            val seconds = remainingSeconds % 60

                            executor.sendMessage(
                                "§aInserted $currentChunk runs... ($inserted/$runAmount) " +
                                        "§7| ETA: ${minutes}m ${seconds}s"
                            )
                        }
                    }
                }
            }
        }
    }
}