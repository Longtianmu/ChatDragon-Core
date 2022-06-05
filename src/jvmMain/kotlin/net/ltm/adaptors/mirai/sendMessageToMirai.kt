package net.ltm.adaptors.mirai

import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.ltm.contact.Contacts
import net.ltm.datas.RenderMessages
import net.ltm.datas.calculateRelationIDQQ
import net.ltm.userQQBot
import net.mamoe.mirai.message.sourceMessage
import net.mamoe.mirai.message.sourceTime

suspend fun sendMessageToMirai(contact: Contacts, content:String, history:SnapshotStateList<RenderMessages>){
    val currentID = userQQBot.userBot.id
    val nowUser =userQQBot.userBot
    when(contact.type){
        "QQ_Friend" -> {
            val res = userQQBot.userBot.getFriendOrFail(contact.id.toLong()).sendMessage(content)
            val relations = calculateRelationIDQQ(currentID, contact.id.toString() + "QID")
            val times = res.sourceTime.toLong()
            var result: String
            withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                result = convertMiraiMessageToJson(
                    currentID.toString(),
                    nowUser.nick,
                    nowUser.avatarUrl,
                    res.sourceMessage
                )
            }
            history.add(RenderMessages("", "", "", res.sourceTime.toLong(), result))
        }
        "QQ_Group" -> {
            val res = userQQBot.userBot.getGroupOrFail(contact.id.toLong()).sendMessage(content)
            val relations = calculateRelationIDQQ(currentID, contact.id.toString() + "QID")
            val times = res.sourceTime.toLong()
            var result: String
            withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                result = convertMiraiMessageToJson(
                    currentID.toString(),
                    nowUser.nick,
                    nowUser.avatarUrl,
                    res.sourceMessage
                )
            }
            history.add(RenderMessages("", "", "", res.sourceTime.toLong(), result))
        }
        else -> {
            println("Err")
        }
    }
}