package de.toby.everyonevshugo.kit

import de.toby.everyonevshugo.Manager
import de.toby.everyonevshugo.game.Game
import de.toby.everyonevshugo.game.implementation.Lobby
import de.toby.everyonevshugo.user.user
import net.axay.kspigot.extensions.onlinePlayers
import java.io.File

object KitManager {
    private val directory = File("plugins${File.separator}${Manager.name}${File.separator}Kits")
    val kits = mutableListOf<Kit<KitSettings>>()

    fun enable() {
        directory.mkdirs()
        directory.listFiles()?.forEach { file ->
            kits.add(Kit(file.name.removeSuffix(".yml"), KitSettings(file.name)))
        }
    }

    fun remove(kit: Kit<KitSettings>) {
        kits -= kit
        File(directory, "${kit.name}.yml").deleteOnExit()
        onlinePlayers.filter { it.user().kit == kit }.forEach {
            it.user().kit = null
            (Game.current as? Lobby ?: return).updateScoreboard(it)
        }
    }
}