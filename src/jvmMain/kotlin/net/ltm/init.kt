package net.ltm

import net.ltm.datas.MessagesQQ
import net.ltm.datas.RelationQQ
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

fun initApp(){
    if (!dbPath.exists()) {
        dbPath.mkdirs()
    }
    relationQQ = Database.connect("jdbc:sqlite:./data/relationQQ.db", "org.sqlite.JDBC")
    chatHistoryQQ = Database.connect("jdbc:sqlite:./data/chatHistoryQQ.db", "org.sqlite.JDBC")
    transaction(relationQQ) {
        SchemaUtils.createMissingTablesAndColumns(RelationQQ)
    }
    transaction(chatHistoryQQ) {
        SchemaUtils.createMissingTablesAndColumns(MessagesQQ)
    }
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
}