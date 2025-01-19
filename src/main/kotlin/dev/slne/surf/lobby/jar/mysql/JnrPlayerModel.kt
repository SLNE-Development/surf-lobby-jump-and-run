package dev.slne.surf.lobby.jar.mysql

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.proxy.HibernateProxy
import org.hibernate.type.SqlTypes
import org.springframework.boot.context.properties.bind.DefaultValue
import java.util.*

@Entity
@Table(name = "jnr_players")
data class JnrPlayerModel(
    @Id
    @Column(name = "uuid", length = 36, nullable = false)
    @JdbcTypeCode(SqlTypes.CHAR)
    val uuid: UUID,

    @Column(name = "points", nullable = false)
    var points: Int = 0,

    @Column(name = "tries", nullable = false)
    var tries: Int = 0,

    @Column(name = "sound", nullable = false)
    @DefaultValue("true")
    var sound: Boolean = true,

    @Column(name = "high_score", nullable = false)
    var highScore: Int = 0
) {
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as JnrPlayerModel

        return uuid != null && uuid == other.uuid
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    override fun toString(): String {
        return "JnrPlayerModel(uuid=$uuid, points=$points, tries=$tries, sound=$sound, highScore=$highScore)"
    }

}