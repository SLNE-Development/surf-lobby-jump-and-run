package dev.slne.surf.parkour.util

import com.destroystokyo.paper.profile.PlayerProfile
import com.destroystokyo.paper.profile.ProfileProperty
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

object HeadUtil {
    suspend fun getPlayerHead(playerName: String): ItemStack = withContext(Dispatchers.IO) {
        val itemStack = ItemStack(Material.PLAYER_HEAD)
        val skullMeta = Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD) as SkullMeta
        val offlinePlayer = Bukkit.getOfflinePlayer(playerName)
        val uuid = offlinePlayer.uniqueId

        val texture = getSkinTexture(uuid)

        if (texture != null) {
            val profile: PlayerProfile = Bukkit.createProfile(uuid, playerName)
            profile.setProperty(ProfileProperty("textures", texture))
            skullMeta.playerProfile = profile
        }

        itemStack.itemMeta = skullMeta
        return@withContext itemStack
    }

    private suspend fun getSkinTexture(uuid: UUID): String? = withContext(Dispatchers.IO) {
        val url = "https://sessionserver.mojang.com/session/minecraft/profile/$uuid?unsigned=false"

        return@withContext try {
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
                    return@withContext property["value"] as String
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}