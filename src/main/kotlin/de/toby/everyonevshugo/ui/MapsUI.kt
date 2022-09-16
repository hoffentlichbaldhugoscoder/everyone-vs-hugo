package de.toby.everyonevshugo.ui

import de.toby.everyonevshugo.game.Game
import de.toby.everyonevshugo.game.implementation.Lobby
import de.toby.everyonevshugo.map.Map
import de.toby.everyonevshugo.map.MapManager
import de.toby.everyonevshugo.map.implementation.MapSettings
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
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemFlag

object MapsUI {

    val item = itemStack(Material.PAPER) {
        meta { name = literalText("${ChatColor.AQUA}Maps") }
    }

    fun enable() {
        listen<PlayerInteractEvent> {
            if (it.item?.isSimilar(item) == true) it.player.performCommand("maps")
        }
    }

    fun ui() = kSpigotGUI(GUIType.FIVE_BY_NINE) {
        defaultPage = 0
        title = literalText("Maps")
        page(0) {
            placeholder(Slots.All, itemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
                meta { name = literalText() }
            })

            val compound = createRectCompound<Map<MapSettings>>(
                Slots.RowTwoSlotTwo,
                Slots.RowFourSlotEight,
                iconGenerator = { map(it) },
                onClick = { event, map ->
                    event.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                    val lobby = Game.current as? Lobby ?: return@createRectCompound
                    lobby.map = map
                    lobby.idle = !lobby.canStart()
                    event.bukkitEvent.isCancelled = true
                    event.guiInstance.reloadCurrentPage()
                })
            MapManager.maps.forEach { compound.addContent(it) }
        }
    }

    private fun map(map: Map<MapSettings>) = itemStack(Material.PAPER) {
        val world = map.world
        meta {
            name = literalText("${ChatColor.AQUA}${world.name}")
            val lobby = Game.current as? Lobby ?: return@meta
            if (lobby.map == map) {
                addEnchant(Enchantment.DURABILITY, 1, false)
                addItemFlags(ItemFlag.HIDE_ENCHANTS)
            }
        }
    }
}