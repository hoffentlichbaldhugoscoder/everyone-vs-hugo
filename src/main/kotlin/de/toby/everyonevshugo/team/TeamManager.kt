package de.toby.everyonevshugo.team

import de.toby.everyonevshugo.game.Game
import de.toby.everyonevshugo.game.implementation.Lobby
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.entity.Player

object TeamManager {

    val teams = mutableListOf<Team>()

    fun Player.setTeam(team: Team) {
        leaveTeam()
        team.member.add(uniqueId)

        onlinePlayers.forEach { it.updateTeams() }

        val lobby = Game.current as? Lobby ?: return
        lobby.idle = !lobby.canStart()
        onlinePlayers.forEach { lobby.updateScoreboard(it) }
    }

    fun Player.leaveTeam() {
        val team = getTeam() ?: return
        team.member.remove(this.uniqueId)

        onlinePlayers.forEach { it.updateTeams() }
    }

    fun Player.getTeam() = teams.find { it.member.contains(uniqueId) }

    fun Player.updateTeams() {
        onlinePlayers.forEach {
            val team = it.getTeam()
            if (team != null) {
                val enemy = scoreboard.getTeam("01${teams.indexOf(team)}") ?: scoreboard.registerNewTeam("01${teams.indexOf(team)}")
                enemy.color = team.color
                enemy.addEntry(it.displayName)
            }
            if (getTeam() == null) scoreboard.getTeam("000")?.removeEntry(it.displayName)
            else if (getTeam() == team) {
                val ally = scoreboard.getTeam("000") ?: scoreboard.registerNewTeam("000")
                ally.color = team!!.memberColor
                ally.addEntry(it.displayName)
            }
        }
    }
}