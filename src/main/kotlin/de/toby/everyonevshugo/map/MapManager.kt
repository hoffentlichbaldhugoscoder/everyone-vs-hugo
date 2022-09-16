package de.toby.everyonevshugo.map

import de.toby.everyonevshugo.map.implementation.MapSettings
import net.axay.kspigot.main.KSpigotMainInstance
import org.apache.commons.io.FileUtils
import org.bukkit.Bukkit
import java.io.File

object MapManager {
    lateinit var directory: File
    val maps = mutableListOf<Map<MapSettings>>()

    fun enable() {
        directory = File("plugins${File.separator}${KSpigotMainInstance.name}${File.separator}Maps")
        directory.mkdirs()
        if (directory.listFiles() == null) return

        directory.listFiles()!!.filter(File::isDirectory).forEach {
            val worldFile = File(Bukkit.getServer().worldContainer, it.name)

            if (directory.exists()) {
                FileUtils.deleteDirectory(worldFile)
                FileUtils.copyDirectory(it, worldFile)
            }

            maps += Map(it.name, MapSettings(it.name))
        }
    }
}