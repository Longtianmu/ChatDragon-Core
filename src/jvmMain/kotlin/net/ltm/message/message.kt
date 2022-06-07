package net.ltm.message

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.ltm.adaptors.mirai.convertMiraiMessageToJson
import net.ltm.chatHistoryQQ
import net.ltm.contact.Contacts
import net.ltm.datas.MessagesQQ
import net.ltm.datas.RenderMessages
import net.ltm.datas.calculateMsgIDQQ
import net.ltm.datas.calculateRelationIDQQ
import net.ltm.userQQBot
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

suspend fun insertMessageToDB(tmp: RenderMessages) {
    newSuspendedTransaction {
        addLogger(StdOutSqlLogger)
        MessagesQQ.insert { msg ->
            msg[relationID] = tmp.relationID
            msg[timeStamp] = tmp.timeStamp
            msg[msgID] = tmp.msgID
            msg[contactID] = tmp.contactID
            msg[messageContent] = tmp.messageContent
        }
    }
}

fun messageListener(contact: Contacts) {
    val currentID = userQQBot.userBot.id
    val globalID = contact.id + when (contact.type) {
        "QQ_Friend" -> "QID"
        "QQ_Group" -> "GID"
        else -> ""
    }
    val relation = calculateRelationIDQQ(currentID, globalID)
    if (contact.type.contains("QQ")) {
        transaction(chatHistoryQQ) {
            MessagesQQ
                .select { (MessagesQQ.relationID eq relation) and (MessagesQQ.contactID eq globalID) }
                .orderBy(MessagesQQ.timeStamp to SortOrder.DESC)
                .limit(50, 0).forEach {
                    contact.addHistory(
                        RenderMessages(
                            it[MessagesQQ.msgID],
                            it[MessagesQQ.relationID],
                            it[MessagesQQ.contactID],
                            it[MessagesQQ.timeStamp],
                            it[MessagesQQ.messageContent]
                        )
                    )
                }
        }
    }
    contact.history.sortBy { it.timeStamp }
    when (contact.type) {
        "QQ_Friend" -> {
            userQQBot.userBot.eventChannel.subscribeAlways<FriendMessageEvent> {
                if (it.sender.id == contact.id.toLong()) {
                    val times = it.time.toLong()
                    val msgs = calculateMsgIDQQ(relation, times)
                    var result: String
                    withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                        result = convertMiraiMessageToJson(
                            it.sender.id.toString(),
                            it.sender.nameCardOrNick,
                            it.sender.avatarUrl,
                            it.message
                        )
                    }
                    val tmp = RenderMessages(msgs, relation, globalID, times, result)
                    contact.addHistory(tmp)
                    insertMessageToDB(tmp)
                }
            }
        }
        "QQ_Group" -> {
            userQQBot.userBot.eventChannel.subscribeAlways<GroupMessageEvent> {
                if (it.group.id == contact.id.toLong()) {
                    val times = it.time.toLong()
                    val msgs = calculateMsgIDQQ(relation, times)
                    var result: String
                    withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                        result = convertMiraiMessageToJson(
                            it.sender.id.toString(),
                            it.sender.nameCardOrNick,
                            it.sender.avatarUrl,
                            it.message
                        )
                    }
                    val tmp = RenderMessages(msgs, relation, globalID, times, result)
                    contact.addHistory(tmp)
                    insertMessageToDB(tmp)
                }
            }
        }
    }
}

