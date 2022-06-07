package net.ltm

import net.ltm.datas.MessagesQQ
import net.ltm.datas.RelationQQ
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.sql.Connection
import java.util.*
import kotlin.system.exitProcess

fun initApp() {
    val os = System.getProperty("os.name").lowercase(Locale.getDefault())
    dataDir = if (os.startsWith("windows")) {
        "${System.getenv("AppData")}\\Chat-Dragon"
    } else if (os.startsWith("linux")) {
        "${System.getenv("username")}/.local/share/Chat-Dragon"
    } else {
        exitProcess(-1)
    }
    dbPath = "$dataDir/data"
    if (!File(dbPath).exists()) {
        File(dbPath).mkdirs()
    }
    try {
        relationQQ = Database.connect("jdbc:sqlite:$dbPath/relationQQ.db", "org.sqlite.JDBC")
        chatHistoryQQ = Database.connect("jdbc:sqlite:$dbPath/chatHistoryQQ.db", "org.sqlite.JDBC")
        transaction(relationQQ) {
            SchemaUtils.createMissingTablesAndColumns(RelationQQ)
        }
        transaction(chatHistoryQQ) {
            SchemaUtils.createMissingTablesAndColumns(MessagesQQ)
        }
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    } catch (e: Exception) {
        println(e)
    }
}