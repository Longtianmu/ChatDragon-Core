package adaptors.mirai

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.utils.BotConfiguration
import java.io.File

class botSets(qq:Long,password:String){
    val userBot = BotFactory.newBot(qq,password){
        cacheDir = File("cache/mirai")
        protocol = BotConfiguration.MiraiProtocol.MACOS
        redirectBotLogToDirectory(File("logs/mirai"))
        redirectNetworkLogToDirectory(File("logs/mirai"))
    }
    fun closeBot(){
        userBot.close()
    }
}
