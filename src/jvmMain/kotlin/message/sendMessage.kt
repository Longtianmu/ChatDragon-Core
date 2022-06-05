package message

import adaptors.mirai.sendMessageToMirai
import androidx.compose.runtime.snapshots.SnapshotStateList
import contact.Contacts
import datas.RenderMessages

suspend fun sendMessage(contact: Contacts, content:String,history:SnapshotStateList<RenderMessages>){
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