package net.ltm.adaptors.mirai

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.ltm.contact.Contacts
import net.ltm.datas.RenderMessages
import net.ltm.datas.calculateMsgIDQQ
import net.ltm.datas.calculateRelationIDQQ
import net.ltm.message.insertMessageToDB
import net.ltm.userQQBot
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.message.sourceMessage
import net.mamoe.mirai.message.sourceTime

suspend fun sendMessageToMirai(contact: Contacts, content: String) {
    val currentID = userQQBot.userBot.id
    val nowUser = userQQBot.userBot
    when (contact.type) {
        "QQ_Friend" -> {
            val res = userQQBot.userBot.getFriendOrFail(contact.id.toLong()).sendMessage(content)
            val contactID = contact.id + "QID"
            val relations = calculateRelationIDQQ(currentID, contactID)
            val times = res.sourceTime.toLong()
            var result: String
            withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                result = convertMiraiMessageToJson(
                    currentID.toString(),
                    nowUser.nameCardOrNick,
                    nowUser.avatarUrl,
                    res.sourceMessage
                )
            }
            val tmp = RenderMessages(
                calculateMsgIDQQ(relations, times),
                relations,
                contactID,
                res.sourceTime.toLong(),
                result
            )
            insertMessageToDB(tmp)
            contact.addHistory(tmp)
        }
        "QQ_Group" -> {
            val res = userQQBot.userBot.getGroupOrFail(contact.id.toLong()).sendMessage(content)
            val contactID = contact.id + "GID"
            val relations = calculateRelationIDQQ(currentID, contactID)
            val times = res.sourceTime.toLong()
            var result: String
            withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                result = convertMiraiMessageToJson(
                    currentID.toString(),
                    nowUser.nameCardOrNick,
                    nowUser.avatarUrl,
                    res.sourceMessage
                )
            }
            val tmp = RenderMessages(
                calculateMsgIDQQ(relations, times),
                relations,
                contactID,
                res.sourceTime.toLong(),
                result
            )
            insertMessageToDB(tmp)
            contact.addHistory(tmp)
        }
        else -> {
            println("Err")
        }
    }
}