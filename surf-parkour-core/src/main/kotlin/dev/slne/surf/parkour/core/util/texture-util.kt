package dev.slne.surf.parkour.core.util


import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.asLoadingCache
import com.sksamuel.aedile.core.expireAfterWrite
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.time.Duration.Companion.hours

private val client = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }
}

// Ensure the HttpClient is closed when the JVM shuts down to prevent resource leaks
init {
    Runtime.getRuntime().addShutdownHook(Thread {
        client.close()
    })
}
private const val DEFAULT_TEXTURE =
    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE5OWIwNWI5YTFkYjRkMjliNWU2NzNkNzdhZTU0YTc3ZWFiNjY4MTg1ODYwMzVjOGEyMDA1YWViODEwNjAyYSJ9fX0="

private val textureCache = Caffeine.newBuilder()
    .expireAfterWrite(1.hours)
    .asLoadingCache(::requestTexture)

suspend fun loadProfileTexture(uuid: UUID) = textureCache.get(uuid)

private suspend fun requestTexture(uuid: UUID) = runCatching {
    client.get("https://sessionserver.mojang.com/session/minecraft/profile/$uuid?unsigned=false")
        .body<TextureResponse>()
        .properties.find { it.name == "textures" }?.value ?: DEFAULT_TEXTURE
}.getOrDefault(DEFAULT_TEXTURE)

@Serializable
private data class TextureResponse(val name: String, val properties: List<Property>) {
    @Serializable
    data class Property(val name: String, val value: String)
}