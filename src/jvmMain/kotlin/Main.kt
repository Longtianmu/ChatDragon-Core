import adaptors.mirai.BotSets
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import datas.MessagesQQ
import datas.RelationQQ
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

lateinit var userQQBot:BotSets
lateinit var relationQQ:Database
lateinit var chatHistoryQQ:Database

fun main(){
    relationQQ=Database.connect("jdbc:sqlite:./data/relationQQ.db", "org.sqlite.JDBC")
    chatHistoryQQ=Database.connect("jdbc:sqlite:./data/chatHistoryQQ.db", "org.sqlite.JDBC")
    transaction(relationQQ) {
        SchemaUtils.createMissingTablesAndColumns(RelationQQ)
    }
    transaction(chatHistoryQQ) {
        SchemaUtils.createMissingTablesAndColumns(MessagesQQ)
    }
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    application {
        Window(
            onCloseRequest = ::exitApplication, title = "Chat-Dragon"
        ) {
            App()
        }
    }
}
