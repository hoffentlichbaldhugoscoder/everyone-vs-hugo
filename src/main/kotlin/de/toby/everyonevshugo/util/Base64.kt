package de.toby.everyonevshugo.util

import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

object Base64 {

    fun itemStackArrayFromBase64(data: String): Array<ItemStack?> {
        return try {
            val input = ByteArrayInputStream(Base64Coder.decodeLines(data))
            val dataInput = BukkitObjectInputStream(input)
            val items = arrayOfNulls<ItemStack>(dataInput.readInt())

            items.indices.forEach {
                items[it] = dataInput.readObject() as ItemStack?
            }
            dataInput.close()
            items
        } catch (e: ClassNotFoundException) {
            throw IOException("Unable to decode class type.", e)
        }
    }

    fun itemStackArrayToBase64(items: Array<ItemStack?>): String {
        return try {
            val outputStream = ByteArrayOutputStream()
            val dataOutput = BukkitObjectOutputStream(outputStream)

            dataOutput.writeInt(items.size)
            items.indices.forEach {
                dataOutput.writeObject(items[it])
            }
            dataOutput.close()
            Base64Coder.encodeLines(outputStream.toByteArray())
        } catch (e: Exception) {
            throw IllegalStateException("Unable to save item stacks.", e)
        }
    }
}