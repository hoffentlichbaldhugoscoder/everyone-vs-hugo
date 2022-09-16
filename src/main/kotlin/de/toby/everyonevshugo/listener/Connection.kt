package de.toby.everyonevshugo.listener

import de.toby.everyonevshugo.config.implementation.Settings
import de.toby.everyonevshugo.game.Game
import de.toby.everyonevshugo.game.implementation.End
import de.toby.everyonevshugo.scoreboard.Board
import de.toby.everyonevshugo.team.TeamManager.setTeam
import de.toby.everyonevshugo.team.TeamManager.updateTeams
import de.toby.everyonevshugo.team.implementation.playerTeam
import de.toby.everyonevshugo.team.implementation.spectatorTeam
import de.toby.everyonevshugo.team.implementation.streamerTeam
import de.toby.everyonevshugo.user.UserState
import de.toby.everyonevshugo.user.user
import de.toby.everyonevshugo.util.PlayerHider
import de.toby.everyonevshugo.util.PlayerHider.hide
import de.toby.everyonevshugo.util.eliminate
import de.toby.everyonevshugo.util.hitCooldown
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.task
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object Connection {

    fun enable() {
        listen<PlayerJoinEvent>(EventPriority.LOWEST) { event ->
            PlayerHider.handleLogin()

            val player = event.player
            player.hitCooldown(Settings.hitCooldown)
            if (player.user().state == UserState.SPECTATING) {
                player.setTeam(spectatorTeam)
                player.hide()
                player.gameMode = GameMode.SPECTATOR
            }

            Board(player)
            onlinePlayers.forEach { it.updateTeams() }
        }
        listen<PlayerQuitEvent> {
            it.player.hitCooldown(true)
        }
    }

    fun handleLogin(player: Player) {
        player.user().task?.cancel()
    }

    fun handleQuit(player: Player) {
        val user = player.user()

        if (user.state != UserState.PLAYING) return
        user.task = task(period = 20, howOften = Settings.offlineTime) {
            if (it.counterDownToZero != 0L) return@task
            if (streamerTeam.player().isEmpty()) Game.current = End(playerTeam)
            if (playerTeam.player().isEmpty()) Game.current = End(streamerTeam)

            player.eliminate()
            it.cancel()
            broadcast("${ChatColor.YELLOW}${player.displayName} was offline for to long and got eliminated trying to rejoin")
        }
    }
}