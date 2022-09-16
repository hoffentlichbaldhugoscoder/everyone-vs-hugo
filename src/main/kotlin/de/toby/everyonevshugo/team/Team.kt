package de.toby.everyonevshugo.team

import de.toby.everyonevshugo.user.UserState
import de.toby.everyonevshugo.user.user
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.util.*

class Team {
    var name: String
    var memberColor: ChatColor
    var color: ChatColor

    constructor(name: String, memberColor: ChatColor, color: ChatColor) {
        this.name = name
        this.memberColor = memberColor
        this.color = color
    }

    constructor(name: String, color: ChatColor) {
        this.name = name
        this.memberColor = color
        this.color = color
    }

    val member = mutableListOf<UUID>()
    fun player() = member.mapNotNull { Bukkit.getPlayer(it) }.filter { it.user().state == UserState.PLAYING }
}