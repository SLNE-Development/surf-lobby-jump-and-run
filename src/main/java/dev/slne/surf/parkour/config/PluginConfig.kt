package dev.slne.surf.parkour.config

import dev.slne.surf.parkour.plugin
import dev.slne.surf.parkour.service.JumpAndRun
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration

object PluginConfig {
    fun getConfig(): FileConfiguration {
        return plugin.config
    }

    private fun createConfig() {
        plugin.saveDefaultConfig()
    }

    fun save(jumpAndRun: JumpAndRun) {
        val config = this.getConfig()
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
        val world = this.getConfig().getString("$path.world")
        val posOne = this.getConfig().getVector("$path.posOne")
        val posTwo = this.getConfig().getVector("$path.posTwo")
        val spawn = this.getConfig().getVector("$path.spawn")
        val start = this.getConfig().getVector("$path.start")
        val displayName = this.getConfig().getString("$path.displayName", "Parkour")
        val materials: ObjectList<Material> = ObjectArrayList()
        val materialNames: ObjectList<String> = ObjectArrayList(
            this.getConfig().getStringList("$path.materials"))

        if (materialNames.isEmpty()) {
            materialNames.add(Material.RED_STAINED_GLASS.toString())
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
