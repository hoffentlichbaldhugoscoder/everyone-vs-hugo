package de.toby.everyonevshugo.command

import de.toby.everyonevshugo.game.Game
import de.toby.everyonevshugo.game.implementation.Lobby
import de.toby.everyonevshugo.kit.Kit
import de.toby.everyonevshugo.kit.KitManager
import de.toby.everyonevshugo.kit.KitSettings
import de.toby.everyonevshugo.team.TeamManager
import de.toby.everyonevshugo.ui.KitUI
import de.toby.everyonevshugo.user.UserState
import de.toby.everyonevshugo.user.user
import de.toby.everyonevshugo.util.Base64
import de.toby.everyonevshugo.util.reset
import net.axay.kspigot.commands.*
import net.axay.kspigot.gui.openGUI
import net.axay.kspigot.sound.sound
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Sound

object KitCommand {

    fun enable() {
        command("kits") {
            runs {
                if (Game.current !is Lobby) player.sendMessage("${ChatColor.RED}The game already started")
                if (player.user().state != UserState.PLAYING) player.sendMessage("${ChatColor.RED}You are not participating in the game")
                else {
                    player.openGUI(KitUI.ui(player))
                    player.sound(Sound.BLOCK_CHEST_OPEN)
                }
            }
        }
        command("kit") {
            requiresPermission("everyonevshugo.kits")
            literal("save") {
                argument<String>("name") {
                    argument<String>("team") {
                        suggestListSuspending { listOf("Streamer", "Player") }
                        runs {
                            val name = getArgument<String>("name")
                            val team = TeamManager.teams.find { it.name == getArgument<String>("team") } ?: return@runs
                            val lobby = Game.current as? Lobby ?: return@runs
                            if (!lobby.editing.contains(player.uniqueId)) {
                                player.sendMessage("${ChatColor.RED}You are currently not working on a kit")
                                return@runs
                            }
                            KitManager.kits.find { it.name == name }?.let { KitManager.remove(it) }
                            val kit = Kit(name, KitSettings(name))
                            kit.properties.team = team.name
                            kit.properties.items = Base64.itemStackArrayToBase64(player.inventory.contents)
                            KitManager.kits.add(kit)
                            player.reset()
                            lobby.equipHotBar(player)
                            lobby.editing -= player.uniqueId
                            player.sendMessage("${ChatColor.WHITE}You saved the kit ${ChatColor.YELLOW}$name")
                        }
                    }
                }
            }
            literal("load") {
                argument<String>("name") {
                    suggestListSuspending { KitManager.kits.map(Kit<KitSettings>::name) }
                    runs {
                        val name = getArgument<String>("name")
                        val kit = KitManager.kits.find { it.name == name } ?: return@runs
                        val lobby = Game.current as? Lobby ?: return@runs
                        player.reset()
                        kit.equip(player)
                        player.gameMode = GameMode.CREATIVE
                        lobby.editing += player.uniqueId
                        player.sendMessage("${ChatColor.WHITE}You loaded the kit ${ChatColor.YELLOW}$name")
                    }
                }
            }
            literal("create") {
                runs {
                    val lobby = Game.current as? Lobby ?: return@runs
                    player.reset()
                    player.gameMode = GameMode.CREATIVE
                    lobby.editing += player.uniqueId
                    player.sendMessage("${ChatColor.WHITE}You created a new kit")
                }
            }
            literal("delete") {
                argument<String>("name") {
                    suggestListSuspending { KitManager.kits.map(Kit<KitSettings>::name) }
                    runs {
                        val name = getArgument<String>("name")
                        val kit = KitManager.kits.find { it.name == name } ?: return@runs
                        KitManager.remove(kit)
                        player.sendMessage("${ChatColor.WHITE}You deleted the kit ${ChatColor.YELLOW}$name")
                    }
                }
            }
            literal("exit") {
                runs {
                    val lobby = Game.current as? Lobby ?: return@runs
                    player.reset()
                    lobby.editing -= player.uniqueId
                    lobby.equipHotBar(player)
                    player.sendMessage("${ChatColor.WHITE}You stopped working on kits")
                }
            }
        }
    }
}