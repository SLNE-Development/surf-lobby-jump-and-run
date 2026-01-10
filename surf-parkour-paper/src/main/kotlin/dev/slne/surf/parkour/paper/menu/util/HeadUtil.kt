package dev.slne.surf.parkour.paper.menu.util

import com.destroystokyo.paper.profile.ProfileProperty
import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.asLoadingCache
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import dev.slne.surf.parkour.paper.database.ParkourPlayerTexturesTable
import dev.slne.surf.surfapi.bukkit.api.builder.buildItem
import dev.slne.surf.surfapi.bukkit.api.builder.meta
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
import kotlin.time.Duration.Companion.hours

object HeadUtil {
    private val textureCache = Caffeine.newBuilder()
        .expireAfterWrite(3.hours)
        .asLoadingCache(this::getSkinTexture)

    private const val DEFAULT_TEXTURE =
        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE5OWIwNWI5YTFkYjRkMjliNWU2NzNkNzdhZTU0YTc3ZWFiNjY4MTg1ODYwMzVjOGEyMDA1YWViODEwNjAyYSJ9fX0="

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun getPlayerHead(uuid: UUID): ItemStack = withContext(Dispatchers.IO) {
        buildItem(Material.PLAYER_HEAD) {
            meta<SkullMeta> {
                val texture = textureCache.get(uuid)
                val profile = Bukkit.createProfile(uuid)
                profile.setProperty(ProfileProperty("textures", texture))
                playerProfile = profile
            }
        }
    }

    private suspend fun getSkinTexture(uuid: UUID): String =
        loadTextureFromDatabase(uuid) ?: loadFromSessionServerAndSaveToDatabase(uuid)
        ?: DEFAULT_TEXTURE

    private suspend fun loadFromSessionServerAndSaveToDatabase(uuid: UUID) = runCatching {
        val response =
            client.get("https://sessionserver.mojang.com/session/minecraft/profile/$uuid?unsigned=false")
                .body<TextureResponse>()
                .properties.find { it.name == "textures" }?.value

        if (response != null) {
            suspendTransaction {
                ParkourPlayerTexturesTable.upsert {
                    it[playerUuid] = uuid
                    it[texture] = response
                }
            }
        }

        response
    }.getOrNull()

    private suspend fun loadTextureFromDatabase(uuid: UUID) = suspendTransaction {
        ParkourPlayerTexturesTable.selectAll().where(ParkourPlayerTexturesTable.playerUuid eq uuid)
            .firstOrNull()?.let {
                it[ParkourPlayerTexturesTable.texture]
            }
    }

    @Serializable
    data class TextureResponse(val name: String, val properties: List<Property>) {
        @Serializable
        data class Property(val name: String, val value: String)
    }
}