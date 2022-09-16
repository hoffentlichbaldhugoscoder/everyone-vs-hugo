package de.toby.everyonevshugo.scoreboard.implementation

import de.toby.everyonevshugo.scoreboard.scoreboard
import org.bukkit.ChatColor

val mainScoreboard = scoreboard("${ChatColor.AQUA}${ChatColor.BOLD}  100VSHUGO  ") {
    +""
    +"${ChatColor.YELLOW}${ChatColor.BOLD}Time: ${ChatColor.WHITE}00:00"
    +""
    +"Player: ${ChatColor.GRAY}0"
    +""
    +"Team: ${ChatColor.GRAY}N/A"
    +"Kit: ${ChatColor.GRAY}Random"
    +""
}

val endScoreboard = scoreboard("${ChatColor.AQUA}${ChatColor.BOLD}  100VSHUGO  ") {
    +""
    +"${ChatColor.YELLOW}${ChatColor.BOLD}Restart: ${ChatColor.WHITE}00"
    +""
    +"Winner: ${ChatColor.GREEN}"
    +""
}