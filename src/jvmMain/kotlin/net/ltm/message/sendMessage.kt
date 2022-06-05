package net.ltm.message

import androidx.compose.runtime.snapshots.SnapshotStateList
import net.ltm.adaptors.mirai.sendMessageToMirai
import net.ltm.contact.Contacts
import net.ltm.datas.RenderMessages

suspend fun sendMessage(contact: Contacts, content:String, history:SnapshotStateList<RenderMessages>){
    when(contact.type){
        "QQ_Friend" -> {
            sendMessageToMirai(contact,content,history)
        }
        "QQ_Group" -> {
            sendMessageToMirai(contact,content,history)
        }
        else -> println("Error")
    }
}