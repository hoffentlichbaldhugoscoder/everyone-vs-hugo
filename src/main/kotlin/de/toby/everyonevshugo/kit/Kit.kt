package de.toby.everyonevshugo.kit

import de.toby.everyonevshugo.util.Base64
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

open class Kit<T: KitSettings>(val name: String, val properties: T) {

    fun equip(player: Player) {
        player.inventory.contents = items()?.clone()
        player.updateInventory()
    }

    private fun items(): Array<ItemStack?>? {
        return Base64.itemStackArrayFromBase64(properties.items ?: return null)
    }
}