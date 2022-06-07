package net.ltm.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.ltm.datas.Messages
import net.ltm.datas.RenderMessages
import java.text.SimpleDateFormat

@Composable
fun buildMessageCard(message: RenderMessages) {
    val rawMessage = Json.decodeFromString<Messages>(message.messageContent)
    val timeStamp = message.timeStamp
    val sender = rawMessage.sender
    val senderName = rawMessage.senderName
    val senderAvatar = rawMessage.senderAvatar
    val messageList = rawMessage.content
    var template = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    Card(modifier = Modifier.fillMaxWidth()) {
        Row {
            Card {
                AsyncImage(
                    load = { loadImageBitmap(senderAvatar) },
                    painterFor = { remember { BitmapPainter(it) } },
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(5.dp)),
                    contentDescription = "Sender Avatar"
                )
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("$senderName   ")
                    for (i in messageList) {
                        when (i.type) {
                            "Text" -> {
                                Text(i.content)
                            }
                            "Image" -> {
                                AsyncImage(
                                    load = { loadImageBitmap(i.content) },
                                    painterFor = { remember { BitmapPainter(it) } },
                                    modifier = Modifier.clip(RoundedCornerShape(5.dp)),
                                    contentDescription = "Message Images"
                                )
                            }
                            else -> Text("WIP")
                        }
                    }
                }
            }
        }
    }
}

