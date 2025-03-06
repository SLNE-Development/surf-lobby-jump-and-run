package dev.slne.surf.parkour.util

import com.destroystokyo.paper.profile.PlayerProfile
import com.destroystokyo.paper.profile.ProfileProperty
import com.github.benmanes.caffeine.cache.Caffeine
import dev.hsbrysk.caffeine.CoroutineLoadingCache
import dev.hsbrysk.caffeine.buildCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
import java.util.concurrent.TimeUnit

object HeadUtil {
    private val textureCache: CoroutineLoadingCache<UUID, String> = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).buildCoroutine(this::getSkinTexture)
    private const val DEFAULT_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE5OWIwNWI5YTFkYjRkMjliNWU2NzNkNzdhZTU0YTc3ZWFiNjY4MTg1ODYwMzVjOGEyMDA1YWViODEwNjAyYSJ9fX0="

    suspend fun getPlayerHead(playerName: String): ItemStack = withContext(Dispatchers.IO) {
        val itemStack = ItemStack(Material.PLAYER_HEAD)
        val skullMeta = Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD) as SkullMeta
        val offlinePlayer = Bukkit.getOfflinePlayer(playerName)
        val uuid = offlinePlayer.uniqueId

        val texture = textureCache.get(uuid)

        val profile: PlayerProfile = Bukkit.createProfile(uuid, playerName)
        profile.setProperty(ProfileProperty("textures", texture))
        skullMeta.playerProfile = profile

        itemStack.itemMeta = skullMeta
        return@withContext itemStack
    }

    private suspend fun getSkinTexture(uuid: UUID): String {
        return withContext(Dispatchers.IO) {
            val url = "https://sessionserver.mojang.com/session/minecraft/profile/$uuid?unsigned=false"

            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONParser().parse(response) as JSONObject
                val properties = json["properties"] as org.json.simple.JSONArray

                for (i in properties.indices) {
                    val property = properties[i] as JSONObject
                    if (property["name"] == "textures") {
                        val texture = property["value"] as String
                        textureCache.put(uuid, texture)
                        return@withContext texture
                    }
                }
                DEFAULT_TEXTURE
            } catch (e: Exception) {
                e.printStackTrace()
                DEFAULT_TEXTURE
            }
        }
    }
}