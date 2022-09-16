package de.toby.everyonevshugo.util

import de.toby.everyonevshugo.user.UserState
import de.toby.everyonevshugo.user.user
import de.toby.everyonevshugo.util.PlayerHider.hide
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

fun Player.eliminate() {
    user().state = UserState.ELIMINATED
    hide()
    gameMode = GameMode.SPECTATOR
}

fun Player.hitCooldown(enabled: Boolean) {
    if (enabled) hitCooldown(4) else hitCooldown(100)
}

fun Player.hitCooldown(value: Int) {
    getAttribute(Attribute.GENERIC_ATTACK_SPEED)?.baseValue = value.toDouble()
}