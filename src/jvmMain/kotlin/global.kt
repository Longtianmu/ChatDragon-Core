import adaptors.mirai.BotSets
import androidx.compose.runtime.mutableStateListOf
import contact.Contacts
import datas.RenderMessages
import io.appoutlet.karavel.Karavel
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import org.jetbrains.exposed.sql.Database
import ui.MainPage

val nav = Karavel(MainPage())
val groupListQQ = mutableStateListOf<Group>()
val contactListQQ = mutableStateListOf<Friend>()
val history = mutableStateListOf<RenderMessages>()
val contactsMap = mutableMapOf<String, MutableMap<String, Contacts>>()

lateinit var userQQBot: BotSets
lateinit var relationQQ: Database
lateinit var chatHistoryQQ: Database