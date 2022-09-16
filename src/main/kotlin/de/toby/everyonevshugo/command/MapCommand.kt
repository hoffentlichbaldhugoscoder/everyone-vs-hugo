package de.toby.everyonevshugo.command

import de.toby.everyonevshugo.game.Game
import de.toby.everyonevshugo.game.implementation.Lobby
import de.toby.everyonevshugo.ui.MapsUI
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.requiresPermission
import net.axay.kspigot.commands.runs
import net.axay.kspigot.gui.openGUI
import net.axay.kspigot.sound.sound
import org.bukkit.ChatColor
import org.bukkit.Sound

object MapCommand {

    fun enable() {
        command("maps") {
            requiresPermission("everyonevshugo.maps")
            runs {
                if (Game.current !is Lobby) player.sendMessage("${ChatColor.RED}The game already started")
                else {
                    player.openGUI(MapsUI.ui())
                    player.sound(Sound.BLOCK_CHEST_OPEN)
                }
            }
        }
    }
}