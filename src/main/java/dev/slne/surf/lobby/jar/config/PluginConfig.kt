package dev.slne.surf.lobby.jar.config

import dev.slne.surf.lobby.jar.JumpAndRun
import dev.slne.surf.lobby.jar.PluginInstance
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration

object PluginConfig {
    fun config(): FileConfiguration {
        return PluginInstance.instance().config
    }

    private fun createConfig() {
        PluginInstance.instance().saveDefaultConfig()
    }

    @JvmStatic
    fun save(jumpAndRun: JumpAndRun) {
        val config = config()
        val materialNames: ObjectList<String> = ObjectArrayList()
        val path = "settings.arena"

        for (material in jumpAndRun.materials) {
            materialNames.add(material.name)
        }

        config[path + "materials"] = materialNames
        config[path + "displayname"] = jumpAndRun.displayName

        saveLocation(path + "posOne", jumpAndRun.posOne ?: return)
        saveLocation(path + "posTwo", jumpAndRun.posTwo ?: return)
        saveLocation(path + "spawn", jumpAndRun.spawn ?: return)
        saveLocation(path + "start", jumpAndRun.start ?: return)

        PluginInstance.Companion.instance().saveConfig()
    }

    fun loadJumpAndRun(): JumpAndRun {
        createConfig()

        val path = "settings.arena"
        val posOne = getLocation(path + "posOne")
        val posTwo = getLocation(path + "posTwo")
        val spawn = getLocation(path + "spawn")
        val start = getLocation(path + "start")
        val displayName = config().getString(path + "displayname", "Parkour")
        val materials: ObjectList<Material> = ObjectArrayList()
        val materialNames: ObjectList<String> = ObjectArrayList(
            config().getStringList(path + "materials")
        )

        if (materialNames.isEmpty()) {
            materialNames.add(Material.BLACKSTONE.toString())
        }

        for (name in materialNames) {
            materials.add(Material.valueOf(name))
        }

        val jumpAndRun = JumpAndRun()

        jumpAndRun.displayName = displayName ?: "Parkour"
        jumpAndRun.posOne = posOne
        jumpAndRun.posTwo = posTwo
        jumpAndRun.spawn = spawn
        jumpAndRun.start = start
        jumpAndRun.players = ObjectArraySet()
        jumpAndRun.materials = ObjectArrayList(materials)
        jumpAndRun.latestBlocks = Object2ObjectOpenHashMap()

        return jumpAndRun
    }

    private fun saveLocation(path: String, location: Location) {
        config()["$path.world"] = location.world.name
        config()["$path.x"] = location.blockX
        config()["$path.y"] = location.blockY
        config()["$path.z"] = location.blockZ
    }

    private fun getLocation(path: String): Location {
        val defaultLocation: Location = Bukkit.getWorlds().first().spawnLocation

        val worldName =
            config().getString("$path.world", defaultLocation.world.name)
        val x = config().getInt("$path.x", defaultLocation.blockX)
        val y = config().getInt("$path.y", defaultLocation.blockY)
        val z = config().getInt("$path.z", defaultLocation.blockZ)

        return Location(Bukkit.getWorld(worldName ?: Bukkit.getWorlds().first().name), x.toDouble(), y.toDouble(), z.toDouble())
    }
}
