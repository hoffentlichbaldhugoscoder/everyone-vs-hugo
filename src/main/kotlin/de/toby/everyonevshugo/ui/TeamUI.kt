package de.toby.everyonevshugo.ui

import de.toby.everyonevshugo.team.implementation.playerTeam
import de.toby.everyonevshugo.team.implementation.spectatorTeam
import de.toby.everyonevshugo.team.implementation.streamerTeam
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.event.listen
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.kSpigotGUI
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.sound.sound
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.player.PlayerInteractEvent

object TeamUI {

    val item = itemStack(Material.WHITE_BED) {
        meta { name = literalText("${ChatColor.AQUA}Teams") }
    }

    fun enable() {
        listen<PlayerInteractEvent> {
            if (it.item?.isSimilar(item) == true) it.player.performCommand("teams")
        }
    }

    fun ui() = kSpigotGUI(GUIType.THREE_BY_NINE) {
        defaultPage = 0
        title = literalText("Teams")
        page(0) {
            placeholder(Slots.All, itemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
                meta { name = literalText() }
            })

            button(Slots.RowTwoSlotThree, itemStack(Material.RED_BED) {
                meta { name = literalText("${ChatColor.AQUA}${streamerTeam.name}") }
            }) {
                it.player.performCommand("streamer")
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
            }

            button(Slots.RowTwoSlotFive, itemStack(Material.GREEN_BED) {
                meta { name = literalText("${ChatColor.AQUA}${playerTeam.name}") }
            }) {
                it.player.performCommand("player")
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
            }

            button(Slots.RowTwoSlotSeven, itemStack(Material.GRAY_BED) {
                meta { name = literalText("${ChatColor.AQUA}${spectatorTeam.name}") }
            }) {
                it.player.performCommand("spectator")
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
            }
        }
    }
}