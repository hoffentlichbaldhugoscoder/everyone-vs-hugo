package de.toby.everyonevshugo.command

import de.toby.everyonevshugo.game.Game
import de.toby.everyonevshugo.game.implementation.Lobby
import de.toby.everyonevshugo.team.TeamManager.getTeam
import de.toby.everyonevshugo.team.TeamManager.setTeam
import de.toby.everyonevshugo.team.implementation.playerTeam
import de.toby.everyonevshugo.team.implementation.spectatorTeam
import de.toby.everyonevshugo.team.implementation.streamerTeam
import de.toby.everyonevshugo.ui.TeamUI
import de.toby.everyonevshugo.user.UserState
import de.toby.everyonevshugo.user.user
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.requiresPermission
import net.axay.kspigot.commands.runs
import net.axay.kspigot.gui.openGUI
import net.axay.kspigot.sound.sound
import org.bukkit.ChatColor
import org.bukkit.Sound

object TeamCommand {

    fun enable() {
        command("teams") {
            runs {
                if (Game.current !is Lobby) player.sendMessage("${ChatColor.RED}The game already started")
                else {
                    player.openGUI(TeamUI.ui())
                    player.sound(Sound.BLOCK_CHEST_OPEN)
                }
            }
        }
        command("streamer") {
            requiresPermission("everyonevshugo.streamer")
            runs {
                if (Game.current !is Lobby) player.sendMessage("${ChatColor.RED}The game already started")
                else {
                    if (player.getTeam() == streamerTeam) player.sendMessage("${ChatColor.RED}You are already in this team")
                    else {
                        player.user().kit = null
                        player.user().state = UserState.PLAYING
                        player.setTeam(streamerTeam)
                        player.sendMessage("${ChatColor.WHITE}You joined the ${ChatColor.YELLOW}${streamerTeam.name} ${ChatColor.WHITE}team")
                    }
                }
            }
        }
        command("player") {
            runs {
                if (Game.current !is Lobby) player.sendMessage("${ChatColor.RED}The game already started")
                else {
                    if (player.getTeam() == playerTeam) player.sendMessage("${ChatColor.RED}You are already in this team")
                    else {
                        player.user().kit = null
                        player.user().state = UserState.PLAYING
                        player.setTeam(playerTeam)
                        player.sendMessage("${ChatColor.WHITE}You joined the ${ChatColor.YELLOW}${playerTeam.name} ${ChatColor.WHITE}team")
                    }
                }
            }
        }
        command("spectator") {
            runs {
                if (Game.current !is Lobby) player.sendMessage("${ChatColor.RED}The game already started")
                else {
                    if (player.getTeam() == spectatorTeam) player.sendMessage("${ChatColor.RED}You are already spectating")
                    else {
                        player.user().kit = null
                        player.user().state = UserState.SPECTATING
                        player.setTeam(spectatorTeam)
                        player.sendMessage("${ChatColor.WHITE}You are now spectating")
                    }
                }
            }
        }
    }
}