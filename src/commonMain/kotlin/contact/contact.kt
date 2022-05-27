package contact

import androidx.compose.runtime.mutableStateListOf
import message.Messages
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import userQQBot

class Contacts(val type: String) {
    var id = ""

    constructor(type: String, id: String) : this(type) {
        this.id = id
        when (type) {
            "QQ_Friend" -> {
                userQQBot.userBot.eventChannel.subscribeAlways<FriendMessageEvent> {
                    if (it.sender.id == id.toLong()) {
                        messageLists.add(Messages("QQ_Friend", it.message.contentToString()))
                    }
                }
            }
            "QQ_Group" -> {
                userQQBot.userBot.eventChannel.subscribeAlways<GroupMessageEvent> {
                    if (it.group.id == id.toLong()) {
                        messageLists.add(Messages("QQ_Group", it.message.contentToString()))
                    }
                }
            }
        }
    }

    constructor(type: String, id: String, name: String) : this(type) {
        this.name = name
    }

    var name: String = ""
    var messageLists = mutableStateListOf<Messages>()
}