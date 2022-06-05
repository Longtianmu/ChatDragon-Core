@file:JvmName("MainKt")

package net.ltm

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import net.ltm.datas.MessagesQQ
import net.ltm.datas.RelationQQ
import net.ltm.ui.App
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

fun main() = application {
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
    Window(
        onCloseRequest = ::exitApplication, title = "Chat-Dragon"
    ) {
        App()
    }
}

