package de.toby.everyonevshugo.ui

import de.toby.everyonevshugo.game.Game
import de.toby.everyonevshugo.game.implementation.Lobby
import de.toby.everyonevshugo.kit.Kit
import de.toby.everyonevshugo.kit.KitManager
import de.toby.everyonevshugo.kit.KitSettings
import de.toby.everyonevshugo.team.TeamManager.getTeam
import de.toby.everyonevshugo.user.user
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.event.listen
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.kSpigotGUI
import net.axay.kspigot.items.addLore
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.sound.sound
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemFlag

object KitUI {

    val item = itemStack(Material.CHEST) {
        meta { name = literalText("${ChatColor.AQUA}Kits") }
    }

    fun enable() {
        listen<PlayerInteractEvent> {
            if (it.item?.isSimilar(item) == true) it.player.performCommand("kits")
        }
    }

    fun ui(player: Player) = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        defaultPage = 0
        title = literalText("Kits")
        page(0) {
            placeholder(Slots.All, itemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
                meta { name = literalText() }
            })

            val compound = createRectCompound<Kit<KitSettings>>(
                Slots.RowTwoSlotTwo,
                Slots.RowFourSlotEight,
                iconGenerator = { kit(player, it) },
                onClick = { event, kit ->
                    event.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                    event.player.user().kit = kit

                    val lobby = Game.current as? Lobby ?: return@createRectCompound
                    lobby.updateScoreboard(player)
                    event.bukkitEvent.isCancelled = true
                    event.guiInstance.reloadCurrentPage()
                })
            KitManager.kits.filter { it.properties.team?.equals(player.getTeam()?.name) ?: false }.forEach { compound.addContent(it) }

            button(Slots.RowOneSlotFive, itemStack(Material.BARRIER) {
                meta {
                    name = literalText("${ChatColor.AQUA}Random Kit")
                }
            }) {
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                it.player.user().kit = null

                val lobby = Game.current as? Lobby ?: return@button
                lobby.updateScoreboard(player)
                it.guiInstance.reloadCurrentPage()
            }
        }
    }

    private fun kit(player: Player, kit: Kit<KitSettings>) = itemStack(Material.IRON_SWORD) {
        meta {
            name = literalText("${ChatColor.AQUA}${kit.name}")
            addLore {
                +""
                if (player.user().kit == kit) {
                    addEnchant(Enchantment.DURABILITY, 1, false)
                    addItemFlags(ItemFlag.HIDE_ENCHANTS)
                    +"${ChatColor.GRAY}Selected"
                } else +"${ChatColor.GRAY}Click to select"
            }
            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }
    }
}