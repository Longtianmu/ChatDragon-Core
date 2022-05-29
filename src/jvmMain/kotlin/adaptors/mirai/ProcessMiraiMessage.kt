package adaptors.mirai

import downloadPicture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import message.Content
import message.Messages
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl


fun convertMiraiMessageToJson(
    sender: String, senderName: String, senderAvatar: String, message: MessageChain
): String {
    val content: MutableList<Content> = mutableListOf()
    CoroutineScope(Dispatchers.IO).launch {
        for (i in message) {
            when (i) {
                is PlainText -> {
                    content.add(Content("Text", i.content))
                }
                is Image -> {
                    val urls = i.queryUrl()
                    content.add(Content("Image", urls))
                    downloadPicture(urls)
                }
                is FlashImage -> {
                    val urls = i.image.imageId
                    content.add(Content("Image", urls))
                    downloadPicture(urls)
                }
                is At -> {
                    content.add(Content("Text", i.content))
                }
                is AtAll -> {
                    content.add(Content("Text", i.content))
                }
                else -> {
                    content.add(Content("Temp", i.contentToString()))
                }
            }
        }
    }
    return Json.encodeToString(Messages(sender, senderName, senderAvatar, content))
}