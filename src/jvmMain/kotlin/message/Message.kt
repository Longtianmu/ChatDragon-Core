package message

import adaptors.mirai.convertMiraiMessageToJson
import chatHistoryQQ
import contact.Contacts
import datas.MessagesQQ
import datas.calculateMsgIDQQ
import datas.calculateRelationIDQQ
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import userQQBot

fun messageListener(contacts: Contacts) {
    when (contacts.type) {
        "QQ_Friend" -> {
            userQQBot.userBot.eventChannel.subscribeAlways<FriendMessageEvent> {
                if (it.sender.id == contacts.id.toLong()) {
                    val relations = calculateRelationIDQQ(userQQBot.userBot.id, it.sender.id.toString() + "QID")
                    val times = it.time.toLong()
                    newSuspendedTransaction(Dispatchers.IO, chatHistoryQQ) {
                        addLogger(StdOutSqlLogger)
                        var result: String
                        withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                            result = convertMiraiMessageToJson(
                                it.sender.id.toString(),
                                it.sender.nameCardOrNick,
                                it.sender.avatarUrl,
                                it.message
                            )
                        }
                        MessagesQQ.insert { msg ->
                            msg[relationID] = relations
                            msg[timeStamp] = times
                            msg[msgID] = calculateMsgIDQQ(relations, times)
                            msg[contactID] = contacts.id + "QID"
                            msg[messageContent] = result
                        }
                        commit()
                    }
                }
            }
        }
        "QQ_Group" -> {
            userQQBot.userBot.eventChannel.subscribeAlways<GroupMessageEvent> {
                if (it.group.id == contacts.id.toLong()) {
                    val relations = calculateRelationIDQQ(userQQBot.userBot.id, it.group.id.toString() + "GID")
                    val times = it.time.toLong()
                    newSuspendedTransaction(Dispatchers.IO, chatHistoryQQ) {
                        addLogger(StdOutSqlLogger)
                        var result: String
                        withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                            result = convertMiraiMessageToJson(
                                it.sender.id.toString(),
                                it.sender.nameCardOrNick,
                                it.sender.avatarUrl,
                                it.message
                            )
                        }
                        MessagesQQ.insert { msg ->
                            msg[relationID] = relations
                            msg[timeStamp] = times
                            msg[msgID] = calculateMsgIDQQ(relations, times)
                            msg[contactID] = contacts.id + "GID"
                            msg[messageContent] = result
                        }
                        commit()
                    }
                }
            }
        }
    }
}


