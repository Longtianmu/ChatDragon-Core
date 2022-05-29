package message

import adaptors.mirai.convertMiraiMessageToJson
import chatHistoryQQ
import contact.Contacts
import datas.MessagesQQ
import datas.calculateMsgIDQQ
import datas.calculateRelationIDQQ
import kotlinx.coroutines.Dispatchers
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import userQQBot

@kotlinx.serialization.Serializable
data class Messages(
    val sender: String,
    val senderName: String,
    val senderAvatar: String,
    val content: MutableList<Content>
)

@kotlinx.serialization.Serializable
data class Content(
    val type: String,
    val content: String
)

fun messageListener(contacts: Contacts) {
    when (contacts.type) {
        "QQ_Friend" -> {
            userQQBot.userBot.eventChannel.subscribeAlways<FriendMessageEvent> {
                if (it.sender.id == contacts.id.toLong()) {
                    suspendedTransactionAsync(Dispatchers.IO, db = chatHistoryQQ) {
                        MessagesQQ.insert { msg ->
                            msg[relationID] =
                                calculateRelationIDQQ(userQQBot.userBot.id, it.sender.id.toString() + "QID")
                            msg[timeStamp] = it.time.toLong()
                            msg[msgID] = calculateMsgIDQQ(msg[relationID], msg[timeStamp])
                            msg[contactID] = contacts.id + "QID"
                            msg[messageContent] = convertMiraiMessageToJson(
                                sender.id.toString(),
                                sender.nameCardOrNick,
                                sender.avatarUrl,
                                it.message
                            )
                        }
                        commit()
                    }
                }
            }
        }
        "QQ_Group" -> {
            userQQBot.userBot.eventChannel.subscribeAlways<GroupMessageEvent> {
                if (it.group.id == contacts.id.toLong()) {
                    suspendedTransactionAsync(Dispatchers.IO, db = chatHistoryQQ) {
                        MessagesQQ.insert { msg ->
                            msg[relationID] =
                                calculateRelationIDQQ(userQQBot.userBot.id, it.sender.id.toString() + "GID")
                            msg[timeStamp] = it.time.toLong()
                            msg[msgID] = calculateMsgIDQQ(msg[relationID], msg[timeStamp])
                            msg[contactID] = contacts.id + "GID"
                            msg[messageContent] = convertMiraiMessageToJson(
                                sender.id.toString(),
                                sender.nameCardOrNick,
                                sender.avatarUrl,
                                it.message
                            )
                        }
                        commit()
                    }
                }
            }
        }
    }
}
