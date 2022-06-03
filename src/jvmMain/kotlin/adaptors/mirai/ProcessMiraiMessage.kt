package adaptors.mirai

import datas.Content
import datas.Messages
import ui.downloadPicture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl


suspend fun convertMiraiMessageToJson(
    sender: String, senderName: String, senderAvatar: String, message: MessageChain
): String {
    val content: MutableList<Content> = mutableListOf()
    withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
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
                    content.add(Content("Temp", i.content))
                }
            }
        }

    }
    return Json.encodeToString(Messages(sender, senderName, senderAvatar, content))
}