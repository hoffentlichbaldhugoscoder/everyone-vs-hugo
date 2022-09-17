package de.toby.everyonevshugo.ui

import de.toby.everyonevshugo.config.implementation.Settings
import de.toby.everyonevshugo.game.Game
import de.toby.everyonevshugo.game.implementation.Lobby
import de.toby.everyonevshugo.util.hitCooldown
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.GUIType
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.gui.kSpigotGUI
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.items.setLore
import net.axay.kspigot.sound.sound
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemFlag

object SettingsUI {

    val item = itemStack(Material.COMPARATOR) {
        meta { name = literalText("${ChatColor.AQUA}Settings") }
    }

    fun enable() {
        listen<PlayerInteractEvent> {
            if (it.item?.isSimilar(item) == true) {
                it.player.performCommand("settings")
                it.isCancelled = true
            }
        }
    }

    fun ui() = kSpigotGUI(GUIType.THREE_BY_NINE) {
        defaultPage = 0
        page(0) {
            title = literalText("Settings")
            placeholder(Slots.All, itemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
                meta { name = literalText() }
            })

            button(Slots.RowTwoSlotTwo, autoStart()) {
                Settings.autoStart = !Settings.autoStart
                val lobby = Game.current as? Lobby ?: return@button
                lobby.idle = !lobby.canStart()
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                it.bukkitEvent.currentItem = autoStart()
            }

            button(Slots.RowTwoSlotThree, requiredPlayer()) {
                if (it.bukkitEvent.isLeftClick) Settings.requiredPlayer += 1
                else if (it.bukkitEvent.isRightClick && Settings.requiredPlayer > 0) Settings.requiredPlayer -= 1
                val lobby = Game.current as? Lobby ?: return@button
                lobby.idle = !lobby.canStart()
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                it.bukkitEvent.currentItem = requiredPlayer()
            }

            button(Slots.RowTwoSlotFour, offlineTime()) {
                if (it.bukkitEvent.isLeftClick) Settings.offlineTime += 10
                else if (it.bukkitEvent.isRightClick && Settings.offlineTime > 0) Settings.offlineTime -= 10
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                it.bukkitEvent.currentItem = offlineTime()
            }

            nextPage(
                Slots.RowTwoSlotSix,
                itemStack(Material.DIAMOND_SWORD) {
                    meta {
                        name = literalText("${ChatColor.AQUA}PVP Settings")
                        setLore {
                            +"${ChatColor.GRAY}Customize the PVP experience"
                        }
                        addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    }
                },
                null,
            ) {
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
            }

            button(Slots.RowTwoSlotSeven, graceDamage()) {
                Settings.graceDamage = !Settings.graceDamage
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                it.bukkitEvent.currentItem = graceDamage()
            }

            button(Slots.RowTwoSlotEight, graceDuration()) {
                if (it.bukkitEvent.isLeftClick) Settings.graceDuration += 10
                else if (it.bukkitEvent.isRightClick && Settings.graceDuration > 0) Settings.graceDuration -= 10
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                it.bukkitEvent.currentItem = graceDuration()
            }
        }
        page(1) {
            title = literalText("Combat Settings")
            placeholder(Slots.All, itemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE) {
                meta { name = literalText() }
            })

            button(Slots.RowTwoSlotThree, hitCooldown()) { event ->
                Settings.hitCooldown = !Settings.hitCooldown
                onlinePlayers.forEach { it.hitCooldown(Settings.hitCooldown) }
                event.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                event.bukkitEvent.currentItem = hitCooldown()
            }

            button(Slots.RowTwoSlotFive, damageMultiplier()) {
                if (it.bukkitEvent.isLeftClick) Settings.damageMultiplier += 1
                else if (it.bukkitEvent.isRightClick && Settings.damageMultiplier > 0) Settings.damageMultiplier -= 1
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                it.bukkitEvent.currentItem = damageMultiplier()
            }

            button(Slots.RowTwoSlotSeven, critMultiplier()) {
                if (it.bukkitEvent.isLeftClick) Settings.critMultiplier += 1
                else if (it.bukkitEvent.isRightClick && Settings.critMultiplier > 0) Settings.critMultiplier -= 1
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
                it.bukkitEvent.currentItem = critMultiplier()
            }

            previousPage(Slots.RowOneSlotFive, itemStack(Material.ARROW) {
                meta {
                    name = literalText("${ChatColor.AQUA}Back")
                }
            }, null) {
                it.player.sound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE)
            }
        }
    }

    private fun autoStart() = itemStack(Material.CLOCK) {
        meta {
            name = literalText("${ChatColor.AQUA}Automatic Countdown")
            setLore {
                +"${ChatColor.GRAY}If enabled the countdown will start"
                +"${ChatColor.GRAY}as soon as enough players are online"
                +""
                +"${ChatColor.GRAY}Current value: ${ChatColor.YELLOW}${Settings.autoStart}"
                +""
                +"${ChatColor.GRAY}Normal Click: ${ChatColor.GOLD}${!Settings.autoStart}"
            }
        }
    }

    private fun requiredPlayer() = itemStack(Material.WHITE_BED) {
        meta {
            name = literalText("${ChatColor.AQUA}Required Player")
            setLore {
                +"${ChatColor.GRAY}The amount of players required"
                +"${ChatColor.GRAY}for the game to start"
                +""
                +"${ChatColor.GRAY}Current value: ${ChatColor.YELLOW}${Settings.requiredPlayer}"
                +""
                +"${ChatColor.GRAY}Click: ${ChatColor.GOLD}+1"
                +"${ChatColor.GRAY}Right Click: ${ChatColor.GOLD}-1"
            }
        }
    }

    private fun offlineTime() = itemStack(Material.COMPASS) {
        meta {
            name = literalText("${ChatColor.AQUA}Offline Time")
            setLore {
                +"${ChatColor.GRAY}The time you are able"
                +"${ChatColor.GRAY}to reconnect in"
                +""
                +"${ChatColor.GRAY}Current time: ${ChatColor.YELLOW}${Settings.offlineTime} seconds"
                +""
                +"${ChatColor.GRAY}Left Click: ${ChatColor.GOLD}+10"
                +"${ChatColor.GRAY}Right Click: ${ChatColor.GOLD}-10"
            }
        }
    }

    private fun graceDamage() = itemStack(Material.REDSTONE) {
        meta {
            name = literalText("${ChatColor.AQUA}Grace Damage")
            setLore {
                +"${ChatColor.GRAY}If disabled you cannot take"
                +"${ChatColor.GRAY}any kind of damage"
                +""
                +"${ChatColor.GRAY}Current value: ${ChatColor.YELLOW}${Settings.graceDamage}"
                +""
                +"${ChatColor.GRAY}Normal Click: ${ChatColor.GOLD}${!Settings.graceDamage}"
            }
        }
    }

    private fun graceDuration() = itemStack(Material.GOLDEN_APPLE) {
        meta {
            name = literalText("${ChatColor.AQUA}Grace Duration")
            setLore {
                +"${ChatColor.GRAY}The time the hunters are"
                +"${ChatColor.GRAY}incapable of moving"
                +""
                +"${ChatColor.GRAY}Current time: ${ChatColor.YELLOW}${Settings.graceDuration} seconds"
                +""
                +"${ChatColor.GRAY}Left Click: ${ChatColor.GOLD}+10"
                +"${ChatColor.GRAY}Right Click: ${ChatColor.GOLD}-10"
            }
        }
    }

    private fun hitCooldown() = itemStack(Material.IRON_SWORD) {
        meta {
            name = literalText("${ChatColor.AQUA}Hitcooldown")
            setLore {
                +"${ChatColor.GRAY}How fast you can swing your sword"
                +""
                +"${ChatColor.GRAY}Current value: ${ChatColor.YELLOW}${Settings.hitCooldown}"
                +""
                +"${ChatColor.GRAY}Normal Click: ${ChatColor.GOLD}${!Settings.hitCooldown}"
            }
            addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        }
    }

    private fun damageMultiplier() = itemStack(Material.GOLDEN_APPLE) {
        meta {
            name = literalText("${ChatColor.AQUA}Damage Multiplier")
            setLore {
                +""
                +"${ChatColor.GRAY}Current value: ${ChatColor.YELLOW}${(Settings.damageMultiplier.toDouble() / 100)}"
                +""
                +"${ChatColor.GRAY}Left Click: ${ChatColor.GOLD}+0.01"
                +"${ChatColor.GRAY}Right Click: ${ChatColor.GOLD}-0.01"
            }
        }
    }

    private fun critMultiplier() = itemStack(Material.ENCHANTED_GOLDEN_APPLE) {
        meta {
            name = literalText("${ChatColor.AQUA}Crit Damage Multiplier")
            setLore {
                +""
                +"${ChatColor.GRAY}Current value: ${ChatColor.YELLOW}${Settings.critMultiplier.toDouble() / 100}"
                +""
                +"${ChatColor.GRAY}Left Click: ${ChatColor.GOLD}+0.01"
                +"${ChatColor.GRAY}Right Click: ${ChatColor.GOLD}-0.01"
            }
        }
    }
}