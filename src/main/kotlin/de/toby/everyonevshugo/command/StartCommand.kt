package de.toby.everyonevshugo.command

import de.toby.everyonevshugo.game.Game
import de.toby.everyonevshugo.game.implementation.Lobby
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.requiresPermission
import net.axay.kspigot.commands.runs
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.ChatColor

object StartCommand {

    fun enable() {
        command("start") {
            requiresPermission("everyonevshugo.forceStart")
            runs {
                val lobby = Game.current

                if (lobby !is Lobby) player.sendMessage("${ChatColor.RED}The game already started")
                else if (!lobby.idle) player.sendMessage("${ChatColor.RED}The countdown already started")
                else {
                    lobby.idle = false
                    onlinePlayers.forEach { lobby.updateScoreboard(it) }
                }
            }
        }
    }
}