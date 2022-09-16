package de.toby.everyonevshugo.game

object Game {
    var current: Phase? = null
        set(value) {
            field?.stop()
            field = value
        }
}