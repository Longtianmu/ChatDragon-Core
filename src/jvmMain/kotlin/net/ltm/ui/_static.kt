package net.ltm.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.lt.load_the_image.rememberImagePainter
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.ltm.dataDir
import net.ltm.datas.CheckImage
import net.ltm.datas.Messages
import net.ltm.datas.RenderMessages
import net.ltm.userQQBot
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun buildMessageCard(message: RenderMessages) {
    val rawMessage = Json.decodeFromString<Messages>(message.messageContent)
    val timeStamp = message.timeStamp
    val sender = rawMessage.sender
    val senderName = rawMessage.senderName
    val senderAvatar = rawMessage.senderAvatar
    val messageList = rawMessage.content
    val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")
    val formattedTime = simpleDateFormat.format(Date(timeStamp * 1000))
    Card(modifier = Modifier.fillMaxWidth()) {
        Row {
            Card {
                val res = checkCacheImageExists(senderAvatar)
                if (res.exists) {
                    Image(
                        painter = rememberImagePainter(res.imageFile),
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(5.dp)),
                        contentDescription = "Sender Avatar"
                    )
                } else {
                    downloadPicture(senderAvatar)
                    Image(
                        painter = rememberImagePainter(senderAvatar),
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(5.dp)),
                        contentDescription = "Sender Avatar"
                    )
                }
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("$senderName   $formattedTime")
                    for (i in messageList) {
                        when (i.type) {
                            "Text" -> {
                                Text(i.content)
                            }
                            "Image" -> {
                                val res = checkCacheImageExists(i.content)
                                if (res.exists) {
                                    Image(
                                        painter = rememberImagePainter(res.imageFile),
                                        modifier = Modifier.clip(RoundedCornerShape(5.dp)),
                                        contentDescription = "Message Images"
                                    )
                                } else {
                                    downloadPicture(i.content)
                                    Image(
                                        painter = rememberImagePainter(i.content),
                                        modifier = Modifier.clip(RoundedCornerShape(5.dp)),
                                        contentDescription = "Message Images"
                                    )
                                }
                            }
                            else -> Text("WIP")
                        }
                    }
                }
            }
        }
    }
}

fun downloadPicture(url: String) {
    CoroutineScope(Dispatchers.Default).launch {
        val client = HttpClient(CIO)
        val response: HttpResponse = client.get(url)
        val type = response.contentType()?.contentSubtype
        val file =
            File("$dataDir/cache/qq/pictures/${userQQBot.userBot.id}/${convertUrlToValidFilePath(url)}")// :转换为~ /转换为% ?转换为+
        if (!file.exists()) {
            file.mkdirs()
        }
        val tmp = response.readBytes()
        if (tmp.isNotEmpty()) {
            try {
                File("$file/temp.$type").writeBytes(tmp)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            println("Receive Empty Picture")
        }
        return@launch
    }
}

fun checkCacheImageExists(url: String): CheckImage {
    val file =
        File("$dataDir/cache/qq/pictures/${userQQBot.userBot.id}/${convertUrlToValidFilePath(url)}")// :转换为~ /转换为% ?转换为+
    var returns = CheckImage(false, File(""))
    if (file.exists()) {
        if (file.listFiles()?.isNotEmpty() == true) {
            if (file.listFiles()?.single()?.exists() == true) {
                returns = CheckImage(true, file.listFiles()?.single()!!)
            }
        }
    }
    return returns
}

fun convertUrlToValidFilePath(url: String): String {
    return url.replace(":", "~").replace("/", "%").replace("?", "+")
}


