package dev.slne.surf.lobby.jar.player

import dev.slne.surf.lobby.jar.mysql.JnrPlayerModel
import org.springframework.data.domain.Limit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface JnrPlayerModelRepository : JpaRepository<JnrPlayerModel, UUID> {


    @Query("select j from JnrPlayerModel j order by j.highScore DESC")
    fun queryHighScoreLeaderboard(limit: Limit): List<JnrPlayerModel>

    @Query("select j from JnrPlayerModel j order by j.points DESC")
    fun queryPointsLeaderboard(limit: Limit): List<JnrPlayerModel>

}