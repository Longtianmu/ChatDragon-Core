package net.ltm.adaptors.mirai

import kotlinx.coroutines.Dispatchers
import net.ltm.*
import net.ltm.contact.Contacts
import net.ltm.datas.RelationQQ
import net.ltm.datas.calculateRelationIDQQ
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.utils.BotConfiguration
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import java.io.File

class BotSets(qq: Long, password: String) {
    val userBot = BotFactory.newBot(qq, password) {
        cacheDir = File("${dataDir}/cache/mirai")
        protocol = BotConfiguration.MiraiProtocol.MACOS
        redirectBotLogToDirectory(File("${dataDir}/logs/mirai"))
        redirectNetworkLogToDirectory(File("${dataDir}/logs/mirai"))
    }

    fun closeBot() {
        userBot.close()
    }
}

suspend fun initQQ(qqid: String, password: String): String {
    val processQQID = qqid.replace(" ", "").replace("\n", "")
        if (!processQQID.matches(Regex("[1-9]([0-9]{5,11})"))) {
            return "非法的QQ号!"
        }
    userQQBot = BotSets(processQQID.toLong(), password)
    try {
        userQQBot.userBot.login()
    } catch (e: Exception) {
        return e.toString()
    }
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
    }
    return if (!userQQBot.userBot.isOnline) {
        "未知原因登录失败,可能是风控或其他原因导致"
    } else {
        "登录成功"
    }
}