package net.ltm.message

import net.ltm.adaptors.mirai.sendMessageToMirai
import net.ltm.contact.Contacts

suspend fun sendMessage(contact: Contacts, content: String) {
    when (contact.type) {
        "QQ_Friend" -> {
            sendMessageToMirai(contact, content)
        }
        "QQ_Group" -> {
            sendMessageToMirai(contact, content)
        }
        else -> println("Error")
    }
}