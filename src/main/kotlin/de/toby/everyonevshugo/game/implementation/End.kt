package de.toby.everyonevshugo.game.implementation

import de.toby.everyonevshugo.game.Phase
import de.toby.everyonevshugo.scoreboard.implementation.endScoreboard
import de.toby.everyonevshugo.team.Team
import de.toby.everyonevshugo.user.UserState
import de.toby.everyonevshugo.user.user
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.extensions.bukkit.title
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.extensions.server
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import java.time.Duration

class End(private val winner: Team) : Phase() {

    init {
        onlinePlayers.forEach {
            it.user().board?.layout(endScoreboard)
            updateScoreboard(it)
        }

        event<PlayerJoinEvent> { event ->
            val player = event.player
            if (player.user().state == null) player.user().state = UserState.SPECTATING

            player.user().board?.layout(endScoreboard)
            updateScoreboard(player)
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
    }

    override fun run() {
        if (countdown() == 0) server.spigot().restart()

        onlinePlayers.forEach {
            it.title(
                literalText("${ChatColor.WHITE}Winner:"),
                literalText("${ChatColor.GREEN}${winner.name}"),
                Duration.ZERO,
                Duration.ofMillis(1050),
                Duration.ZERO
            )
            updateScoreboard(it)
        }
    }

    private fun countdown() = 30 - time

    private fun updateScoreboard(player: Player) {
        player.user().board?.run {
            update(3, "${ChatColor.YELLOW}${ChatColor.BOLD}Restart: ${ChatColor.WHITE}${countdown()}")
            update(1, "Winner: ${ChatColor.GREEN}${winner.name}")
        }
    }
}