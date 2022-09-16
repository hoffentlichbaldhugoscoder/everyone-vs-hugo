package de.toby.everyonevshugo.map

import de.toby.everyonevshugo.config.Properties
import net.axay.kspigot.main.KSpigotMainInstance
import org.apache.commons.io.FileUtils
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import java.io.File

class Map<T : Properties>(name: String, var properties: T) {
    var world: World

    init {
        val directory = File("plugins${File.separator}${KSpigotMainInstance.name}${properties.path}")
        val worldFile = File(Bukkit.getServer().worldContainer, name)
        val mapFile = File(directory, name)

        if (directory.exists() && mapFile.exists()) {
            FileUtils.deleteDirectory(worldFile)
            FileUtils.copyDirectory(mapFile, worldFile)
        }

        world = Bukkit.getWorld(name) ?: WorldCreator(name).createWorld()!!
    }
}