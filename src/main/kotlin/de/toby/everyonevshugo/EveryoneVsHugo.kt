package de.toby.everyonevshugo

import de.toby.everyonevshugo.command.*
import de.toby.everyonevshugo.game.Game
import de.toby.everyonevshugo.game.implementation.Lobby
import de.toby.everyonevshugo.kit.KitManager
import de.toby.everyonevshugo.listener.Connection
import de.toby.everyonevshugo.map.MapManager
import de.toby.everyonevshugo.team.TeamManager
import de.toby.everyonevshugo.team.implementation.playerTeam
import de.toby.everyonevshugo.team.implementation.spectatorTeam
import de.toby.everyonevshugo.team.implementation.streamerTeam
import de.toby.everyonevshugo.ui.KitUI
import de.toby.everyonevshugo.ui.MapsUI
import de.toby.everyonevshugo.ui.SettingsUI
import de.toby.everyonevshugo.ui.TeamUI
import net.axay.kspigot.main.KSpigot

class EveryoneVsHugo : KSpigot() {

    companion object {
        lateinit var INSTANCE: EveryoneVsHugo; private set
    }

    override fun load() {
        INSTANCE = this
    }

    override fun startup() {
        TeamManager.teams += listOf(streamerTeam, playerTeam, spectatorTeam)

        MapManager.enable()
        KitManager.enable()
        Connection.enable()

        FreezeCommand.enable()
        MapCommand.enable()
        StartCommand.enable()
        KitCommand.enable()
        TeamCommand.enable()
        SettingsCommand.enable()

        KitUI.enable()
        MapsUI.enable()
        TeamUI.enable()
        SettingsUI.enable()

        Game.current = Lobby()
    }
}

val Manager by lazy { EveryoneVsHugo.INSTANCE }