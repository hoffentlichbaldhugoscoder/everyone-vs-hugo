package de.toby.everyonevshugo.user

import de.toby.everyonevshugo.kit.Kit
import de.toby.everyonevshugo.kit.KitSettings
import de.toby.everyonevshugo.scoreboard.Board
import net.axay.kspigot.runnables.KSpigotRunnable
import org.bukkit.entity.Player
import java.util.*

private val players = mutableMapOf<UUID, User>()

class User {
    var board: Board? = null
    var state: UserState? = null
    var task: KSpigotRunnable? = null
    var kit: Kit<KitSettings>? = null
}

fun Player.user() = players.computeIfAbsent(uniqueId) { User() }