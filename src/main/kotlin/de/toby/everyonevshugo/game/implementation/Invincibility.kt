package de.toby.everyonevshugo.game.implementation

import de.toby.everyonevshugo.config.implementation.Settings
import de.toby.everyonevshugo.game.Game
import de.toby.everyonevshugo.game.Phase
import de.toby.everyonevshugo.listener.Connection
import de.toby.everyonevshugo.map.Map
import de.toby.everyonevshugo.map.implementation.MapSettings
import de.toby.everyonevshugo.scoreboard.implementation.mainScoreboard
import de.toby.everyonevshugo.team.TeamManager.getTeam
import de.toby.everyonevshugo.team.implementation.playerTeam
import de.toby.everyonevshugo.team.implementation.streamerTeam
import de.toby.everyonevshugo.user.UserState
import de.toby.everyonevshugo.user.user
import de.toby.everyonevshugo.util.PlayerHider.hide
import de.toby.everyonevshugo.util.eliminate
import de.toby.everyonevshugo.util.formatToMinutes
import de.toby.everyonevshugo.util.reset
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

class Invincibility(map: Map<MapSettings>): Phase() {

    init {
        map.world.time = 1000
        map.world.setStorm(false)
        map.world.isThundering = false

        event<PlayerJoinEvent> { event ->
            val player = event.player
            if (player.user().state == null) player.user().state = UserState.SPECTATING

            player.user().board?.layout(mainScoreboard)
            Connection.handleLogin(player)

            onlinePlayers.forEach { updateScoreboard(it) }
        }

        onlinePlayers.forEach {
            val location = Location(map.world, map.properties.spawnLocationX, map.properties.spawnLocationY, map.properties.spawnLocationZ)
            it.teleport(location)
            it.reset()
            it.user().kit?.equip(it)
            updateScoreboard(it)

            if(it.user().state == UserState.SPECTATING) {
                it.hide()
                it.gameMode = GameMode.SPECTATOR
            }
        }

        event<PlayerQuitEvent> {
            Connection.handleQuit(it.player)
        }

        event<PlayerInteractEvent> {
            it.isCancelled = it.player.getTeam() == playerTeam
        }

        event<EntityPickupItemEvent> {
            it.isCancelled = (it.entity as? Player)?.getTeam() == playerTeam
        }

        event<EntityDamageEvent> {
            it.isCancelled = it.entity is Player && !Settings.graceDamage && (it.entity as? Player)?.getTeam() == playerTeam
        }

        event<FoodLevelChangeEvent> {
            it.isCancelled = (it.entity as? Player)?.getTeam() == playerTeam
        }

        event<EntityTargetEvent> {
            it.isCancelled = true
        }

        event<PlayerMoveEvent> {
            it.isCancelled = it.player.getTeam() == playerTeam
        }

        event<PlayerDeathEvent> { event ->
            event.entity.eliminate()
            onlinePlayers.forEach { updateScoreboard(it) }
            if (streamerTeam.player().isEmpty()) Game.current = End(playerTeam)
            if (playerTeam.player().isEmpty()) Game.current = End(streamerTeam)
        }
    }

    override fun run() {
        onlinePlayers.forEach { updateScoreboard(it) }

        when (countdown()) {
            2, 3, 4, 5, 10 -> broadcast("${ChatColor.YELLOW}The grace period ends in ${countdown()} seconds")
            1 -> broadcast("${ChatColor.YELLOW}The grace period ends in one second")
            0 -> {
                broadcast("${ChatColor.YELLOW}The grace period has ended")
                Game.current = Ingame()
            }
        }
    }

    private fun countdown() = Settings.graceDuration - time

    private fun updateScoreboard(player: Player) {
        player.user().board?.run{
            update(6, "${ChatColor.YELLOW}${ChatColor.BOLD}Grace: ${ChatColor.WHITE}${countdown().formatToMinutes()}")
            update(4, "Player: ${ChatColor.GRAY}${playerTeam.player().size}")
            update(2, "Team: ${ChatColor.GRAY}${player.getTeam()?.name}")
            update(1, "Kit: ${ChatColor.GRAY}${player.user().kit?.name ?: "Random"}")
        }
    }
}