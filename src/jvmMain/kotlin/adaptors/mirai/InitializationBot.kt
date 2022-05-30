package adaptors.mirai

import contact.Contacts
import contactListQQ
import contactsMap
import datas.RelationQQ
import datas.calculateRelationIDQQ
import groupListQQ
import kotlinx.coroutines.Dispatchers
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.utils.BotConfiguration
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import relationQQ
import userQQBot
import java.io.File

class BotSets(qq: Long, password: String) {
    val userBot = BotFactory.newBot(qq, password) {
        cacheDir = File("cache/mirai")
        protocol = BotConfiguration.MiraiProtocol.MACOS
        redirectBotLogToDirectory(File("logs/mirai"))
        redirectNetworkLogToDirectory(File("logs/mirai"))
    }

    fun closeBot() {
        userBot.close()
    }
}

suspend fun initQQ(qqid: String, password: String) {
    userQQBot = BotSets(qqid.toLong(), password)
    userQQBot.userBot.login()
    contactListQQ.clear()
    groupListQQ.clear()
    contactsMap["QQ_Friend"] = mutableMapOf()
    contactsMap["QQ_Group"] = mutableMapOf()
    userQQBot.userBot.friends.forEach {
        contactListQQ.add(it)
        contactsMap["QQ_Friend"]!![it.id.toString()] =
            Contacts("QQ_Friend", it.id.toString(), it.nameCardOrNick, it.avatarUrl)
    }
    userQQBot.userBot.groups.forEach {
        groupListQQ.add(it)
        contactsMap["QQ_Group"]!![it.id.toString()] =
            Contacts("QQ_Group", it.id.toString(), it.name, it.avatarUrl)
    }
    suspendedTransactionAsync(Dispatchers.IO, db = relationQQ) {
        addLogger(StdOutSqlLogger)
        contactListQQ.forEach { friends ->
            RelationQQ.insert {
                it[relationID] = calculateRelationIDQQ(userQQBot.userBot.id, friends.id.toString() + "QID")
                it[userID] = userQQBot.userBot.id
                it[contactID] = friends.id.toString() + "QID"
            }
            commit()
        }
        groupListQQ.forEach { groups ->
            RelationQQ.insert {
                it[relationID] = calculateRelationIDQQ(userQQBot.userBot.id, groups.id.toString() + "GID")
                it[userID] = userQQBot.userBot.id
                it[contactID] = groups.id.toString() + "GID"
            }
            commit()
        }
        commit()
    }
    return
}