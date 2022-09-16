package de.toby.everyonevshugo.map.implementation

import de.toby.everyonevshugo.config.Properties
import de.toby.everyonevshugo.map.MapManager

class MapSettings(name: String) : Properties("Maps", name) {
    val spawnLocationX by value(0.0)
    val spawnLocationY by value(64.0)
    val spawnLocationZ by value(0.0)
}