package de.toby.everyonevshugo.kit

import de.toby.everyonevshugo.config.Properties

open class KitSettings(name: String): Properties("Kits", name) {
    var team by value<String?>(null)
    var items by value<String?>(null)
}