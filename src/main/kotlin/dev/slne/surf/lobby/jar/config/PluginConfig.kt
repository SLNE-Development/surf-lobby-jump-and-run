package dev.slne.surf.lobby.jar.config

import dev.slne.surf.lobby.jar.plugin
import dev.slne.surf.lobby.jar.service.JumpAndRun
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration

object PluginConfig {
    fun config(): FileConfiguration {
        return plugin.config
    }

    private fun createConfig() {
        plugin.saveDefaultConfig()
    }

    fun save(jumpAndRun: JumpAndRun) {
        val config = config()
        val materialNames: ObjectList<String> = ObjectArrayList()
        val path = "settings.arena"

        for (material in jumpAndRun.materials) {
            materialNames.add(material.name)
        }

        config["$path.world"] = jumpAndRun.world?.name
        config["$path.materials"] = materialNames
        config["$path.displayName"] = jumpAndRun.displayName

        config["$path.posOne"] = jumpAndRun.boundingBox.min
        config["$path.posTwo"] = jumpAndRun.boundingBox.max
        config["$path.spawn"] = jumpAndRun.spawn
        config["$path.start"] = jumpAndRun.start

        plugin.saveConfig()
    }

    fun loadJumpAndRun(): JumpAndRun {
        createConfig()

        val path = "settings.arena"
        val world = config().getString("$path.world")
        val posOne = config().getVector("$path.posOne")
        val posTwo = config().getVector("$path.posTwo")
        val spawn = config().getVector("$path.spawn")
        val start = config().getVector("$path.start")
        val displayName = config().getString("$path.displayName", "Parkour")
        val materials: ObjectList<Material> = ObjectArrayList()
        val materialNames: ObjectList<String> = ObjectArrayList(
            config().getStringList("$path.materials")
        )

        if (materialNames.isEmpty()) {
            materialNames.add(Material.BLACKSTONE.toString())
        }

        for (name in materialNames) {
            materials.add(Material.valueOf(name))
        }

        val jumpAndRun = JumpAndRun(
            displayName = displayName ?: "Parkour",
            worldName = world,
            posOne = posOne,
            posTwo = posTwo,
            spawn = spawn,
            start = start,
            materials = materials,
        )

        return jumpAndRun
    }
}
