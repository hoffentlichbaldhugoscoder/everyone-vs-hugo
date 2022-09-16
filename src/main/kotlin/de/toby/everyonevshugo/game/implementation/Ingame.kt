package de.toby.everyonevshugo.game.implementation

import de.toby.everyonevshugo.game.Game
import de.toby.everyonevshugo.game.Phase
import de.toby.everyonevshugo.listener.Connection
import de.toby.everyonevshugo.scoreboard.implementation.mainScoreboard
import de.toby.everyonevshugo.team.TeamManager.getTeam
import de.toby.everyonevshugo.team.implementation.playerTeam
import de.toby.everyonevshugo.team.implementation.streamerTeam
import de.toby.everyonevshugo.user.UserState
import de.toby.everyonevshugo.user.user
import de.toby.everyonevshugo.util.eliminate
import de.toby.everyonevshugo.util.formatToMinutes
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class Ingame : Phase() {

    init {
        event<PlayerJoinEvent> { event ->
            val player = event.player
            if (player.user().state == null) player.user().state = UserState.SPECTATING

            player.user().board?.layout(mainScoreboard)
            Connection.handleLogin(player)

            onlinePlayers.forEach { updateScoreboard(it) }
        }

        event<PlayerQuitEvent> {
            Connection.handleQuit(it.player)
        }

        event<EntityDamageByEntityEvent> {
            val player = it.damager as? Player ?: return@event
            val enemy = it.entity as? Player ?: return@event

            if (player.getTeam() != enemy.getTeam()) return@event
            it.isCancelled = true
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
    }

    private fun updateScoreboard(player: Player) {
        player.user().board?.run {
            update(6, "${ChatColor.YELLOW}${ChatColor.BOLD}Time: ${ChatColor.WHITE}${time.formatToMinutes()}")
            update(4, "Player: ${ChatColor.GRAY}${playerTeam.player().size}")
            update(2, "Team: ${ChatColor.GRAY}${player.getTeam()?.name}")
            update(1, "Kit: ${ChatColor.GRAY}${player.user().kit?.name ?: "Random"}")
        }
    }
}