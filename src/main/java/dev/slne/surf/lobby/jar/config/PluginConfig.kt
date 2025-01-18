package dev.slne.surf.lobby.jar.config

import dev.slne.surf.lobby.jar.JumpAndRun
import dev.slne.surf.lobby.jar.PluginInstance
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration

object PluginConfig {
    fun config(): FileConfiguration {
        return PluginInstance.Companion.instance().getConfig()
    }

    fun createConfig() {
        PluginInstance.Companion.instance().saveDefaultConfig()
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

        saveLocation(path + "posOne", jumpAndRun.posOne)
        saveLocation(path + "posTwo", jumpAndRun.posTwo)
        saveLocation(path + "spawn", jumpAndRun.spawn)
        saveLocation(path + "start", jumpAndRun.start)

        PluginInstance.Companion.instance().saveConfig()
    }

    fun loadJumpAndRun(): JumpAndRun {
        createConfig()

        val path = "settings.arena"
        val posOne = getLocation(path + "posOne")
        val posTwo = getLocation(path + "posTwo")
        val spawn = getLocation(path + "spawn")
        val start = getLocation(path + "start")
        val displayName = config().getString(path + "displayname", "Parkour")!!
        val materials: ObjectList<Material> = ObjectArrayList()
        val materialNames: ObjectList<String> = ObjectArrayList(
            config().getStringList(path + "materials")
        )

        if (materialNames.isEmpty()) {
            materialNames.add(Material.BLACKSTONE.toString())
        }

        for (name in materialNames) {
            materials.add(Material.valueOf(name!!))
        }

        return JumpAndRun.builder()
            .displayName(displayName)
            .posOne(posOne)
            .posTwo(posTwo)
            .spawn(spawn)
            .start(start)
            .players(ObjectArrayList())
            .materials(ObjectArrayList(materials))
            .latestBlocks(Object2ObjectOpenHashMap())
            .build()
    }

    fun saveLocation(path: String, location: Location) {
        config()["$path.world"] = location.world.name
        config()["$path.x"] = location.blockX
        config()["$path.y"] = location.blockY
        config()["$path.z"] = location.blockZ
    }

    fun getLocation(path: String): Location {
        val defaultLocation: Location = Bukkit.getWorlds().getFirst().getSpawnLocation()

        val worldName =
            config().getString("$path.world", defaultLocation.world.name)!!
        val x = config().getInt("$path.x", defaultLocation.blockX)
        val y = config().getInt("$path.y", defaultLocation.blockY)
        val z = config().getInt("$path.z", defaultLocation.blockZ)

        return Location(Bukkit.getWorld(worldName), x.toDouble(), y.toDouble(), z.toDouble())
    }
}
