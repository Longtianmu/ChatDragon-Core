package datas

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import java.util.*

fun calculateRelationIDQQ(userID: Long, contactID: String): String {
    return Base64.getEncoder().encodeToString((userID.toString() + contactID).toByteArray())
}

fun calculateMsgIDQQ(relationID: String, timeStamp: Long): String {
    return Base64.getEncoder().encodeToString((timeStamp.toString() + relationID).toByteArray())
}

@Serializable
data class Messages(
        val sender: String,
        val senderName: String,
        val senderAvatar: String,
        val content: MutableList<Content>
)

@Serializable
data class Content(
        val type: String,
        val content: String
)

data class RenderMessages(
        val msgID: String,
        val relationID: String,
        val contactID: String,
        val timeStamp: Long,
        val messageContent: String
)

object RelationQQ : Table("relationQQ") {
    val relationID = text("RelationID").uniqueIndex()
    val userID = long("UserID")
    val contactID = text("ContactID").index()//每组User,Contact的二元组会分配到唯一的一个RelationID表示所属关系
    override val primaryKey = PrimaryKey(relationID, name = "RelationIDs")
}

object MessagesQQ : Table("messagesQQ") {
    val msgID = text("MsgID").uniqueIndex()
    val relationID = text("RelationID").index()
    val contactID = text("ContactID")
    val timeStamp = long("MessageTime").index()
    val messageContent = text("MessageContent")//msgID表示一条消息 由contact timestamp relation构建而来
    override val primaryKey = PrimaryKey(msgID, name = "MsgIDs")
}

