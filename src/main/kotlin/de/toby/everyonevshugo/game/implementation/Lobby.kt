package de.toby.everyonevshugo.game.implementation

import de.toby.everyonevshugo.config.implementation.Settings
import de.toby.everyonevshugo.game.Game
import de.toby.everyonevshugo.game.Phase
import de.toby.everyonevshugo.kit.KitManager
import de.toby.everyonevshugo.map.Map
import de.toby.everyonevshugo.map.implementation.MapSettings
import de.toby.everyonevshugo.scoreboard.implementation.mainScoreboard
import de.toby.everyonevshugo.team.TeamManager.getTeam
import de.toby.everyonevshugo.team.TeamManager.leaveTeam
import de.toby.everyonevshugo.team.TeamManager.setTeam
import de.toby.everyonevshugo.team.implementation.playerTeam
import de.toby.everyonevshugo.team.implementation.streamerTeam
import de.toby.everyonevshugo.ui.KitUI
import de.toby.everyonevshugo.ui.MapsUI
import de.toby.everyonevshugo.ui.SettingsUI
import de.toby.everyonevshugo.ui.TeamUI
import de.toby.everyonevshugo.user.UserState
import de.toby.everyonevshugo.user.user
import de.toby.everyonevshugo.util.PlayerHider.show
import de.toby.everyonevshugo.util.formatToMinutes
import de.toby.everyonevshugo.util.reset
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.bukkit.actionBar
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.pluginManager
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

class Lobby : Phase() {

    var map: Map<MapSettings>? = null
    var editing = mutableListOf<UUID>()

    init {
        val world =
            Bukkit.getWorld("lobby_map") ?: WorldCreator("lobby_map").generateStructures(false).type(WorldType.FLAT)
                .createWorld()!!
        world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0)
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)

        event<PlayerJoinEvent> { event ->
            val player = event.player

            player.reset()
            player.show()
            player.setTeam(playerTeam)
            player.user().board?.layout(mainScoreboard)
            player.user().state = UserState.PLAYING
            player.teleport(Location(world, 0.0, world.getHighestBlockAt(0, 0).y.toDouble() + 1, 0.0))

            idle = !canStart()
            onlinePlayers.forEach { updateScoreboard(it) }

            equipHotBar(player)
        }

        event<PlayerQuitEvent> { event ->
            val player = event.player
            player.leaveTeam()
            player.user().state = null

            idle = !canStart()
            onlinePlayers.forEach { updateScoreboard(it) }
        }

        event<PlayerDropItemEvent> {
            it.isCancelled = true
        }

        event<PlayerInteractEvent> {
            it.isCancelled = true
        }

        event<EntityPickupItemEvent> {
            it.isCancelled = true
        }

        event<EntitySpawnEvent> {
            it.isCancelled = true
        }

        event<EntityDamageEvent> {
            it.isCancelled = true
        }

        event<FoodLevelChangeEvent> {
            it.isCancelled = true
        }

        event<EntityTargetEvent> {
            it.isCancelled = true
        }

        event<InventoryClickEvent> {
            it.isCancelled = !editing.contains((it.whoClicked as? Player)?.uniqueId)
        }

        onlinePlayers.forEach {
            pluginManager.callEvent(PlayerJoinEvent(it, null))
        }

        idle = !canStart()
    }

    override fun run() {
        onlinePlayers.forEach { updateScoreboard(it) }
        if (!idle) {
            when (countdown()) {
                2, 3, 4, 5, 10, 30 -> broadcast("${ChatColor.YELLOW}The game starts in ${countdown()} seconds")
                1 -> broadcast("${ChatColor.YELLOW}The game starts in one second")
                0 -> {
                    broadcast("${ChatColor.YELLOW}The game has started")

                    onlinePlayers.forEach { player ->
                        player.user().kit ?: run {
                            val kits = KitManager.kits.filter { it.properties.team == player.getTeam()?.name }
                            if (kits.isEmpty()) return@forEach
                            player.user().kit = kits.random()
                        }
                    }
                    Game.current = Invincibility(map!!)
                }
            }
        } else {
            val text = if (streamerTeam.player().isEmpty()) "Not enough streamers are online"
            else if (playerTeam.player().size < Settings.requiredPlayer) "Not enough players are online"
            else if (map == null) "There is no map selected at the moment"
            else if (!Settings.autoStart) "The start is only done via /start" else null

            if (text == null) return
            onlinePlayers.forEach { it.actionBar("${ChatColor.RED}$text") }
        }
    }

    private fun countdown() = 30 - time

    fun canStart(): Boolean {
        return if (streamerTeam.player().isEmpty()) false
        else if (playerTeam.player().size < Settings.requiredPlayer) false
        else if (map == null) false
        else Settings.autoStart
    }

    fun updateScoreboard(player: Player) {
        player.user().board?.run {
            if (!idle) update(
                6,
                "${ChatColor.YELLOW}${ChatColor.BOLD}Start: ${ChatColor.WHITE}${countdown().formatToMinutes()}"
            )
            else update(6, "${ChatColor.YELLOW}${ChatColor.BOLD}Start: ${ChatColor.WHITE}Paused")
            update(4, "Player: ${ChatColor.GRAY}${playerTeam.player().size}")
            update(2, "Team: ${ChatColor.GRAY}${player.getTeam()?.name}")
            update(1, "Kit: ${ChatColor.GRAY}${player.user().kit?.name ?: "Random"}")
        }
    }

    fun equipHotBar(player: Player) {
        player.inventory.setItem(1, KitUI.item)
        player.inventory.setItem(4, TeamUI.item)
        player.inventory.setItem(7, MapsUI.item)
        player.inventory.setItem(8, SettingsUI.item)
    }
}