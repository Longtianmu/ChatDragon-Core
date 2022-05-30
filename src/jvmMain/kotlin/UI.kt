import adaptors.mirai.initQQ
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import contact.Contacts
import datas.MessagesQQ
import datas.RenderMessages
import datas.calculateRelationIDQQ
import io.appoutlet.karavel.Karavel
import io.appoutlet.karavel.Page
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import message.simpleMessageListenerForChatUI
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.nameCardOrNick
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

val nav = Karavel(MainPage())
val groupListQQ = mutableStateListOf<Group>()
val contactListQQ = mutableStateListOf<Friend>()
val history = mutableStateListOf<RenderMessages>()
val contactsMap = mutableMapOf<String, MutableMap<String, Contacts>>()

//左侧边栏
@Composable
fun leftSidebar() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxHeight().width(66.dp).background(Color(247, 242, 243))
    ) {
        Image(contentDescription = "Chat Lists",
            painter = painterResource("icons/112-bubbles3.svg"),
            modifier = Modifier.padding(vertical = 20.dp).size(42.dp).clickable {
                nav.navigate(MainPage())
            })
        /*Image(
            contentDescription = "Contact Lists",
            painter = painterResource("")
        )*/
        Image(contentDescription = "Settings",
            painter = painterResource("icons/147-equalizer.svg"),
            modifier = Modifier.padding(top = 400.dp).size(42.dp).clickable {
                nav.navigate(SettingsPage())
            })
    }
}

//联系人列表
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun sessionList() {
    val selected = remember { mutableStateOf(Pair("None", "")) }
    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxHeight().width(250.dp).background(color = Color(180, 180, 180)).padding(10.dp)
        ) {
            val state = rememberLazyListState()
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxSize().padding(end = 12.dp),
                state = state
            ) {
                stickyHeader {
                    Box {
                        Text("联系人列表", modifier = Modifier.padding(4.dp).clip(shape = RoundedCornerShape(4.dp)))
                    }
                }
                items(groupListQQ) {
                    Box(modifier = Modifier.align(Alignment.Center).fillMaxSize().clip(RoundedCornerShape(5.dp))
                        .clickable {
                            selected.value = Pair("QQ_Group", it.id.toString())
                        }) {
                        Row {
                            AsyncImage(
                                load = { loadImageBitmap(it.avatarUrl) },
                                painterFor = { remember { BitmapPainter(it) } },
                                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(5.dp)),
                                contentDescription = "Group Avatar"
                            )
                            Box(modifier = Modifier.padding(4.dp).fillMaxSize()) {
                                Text(text = it.name)
                            }
                        }
                    }
                }
                items(contactListQQ) {
                    Box(modifier = Modifier.align(Alignment.Center).fillMaxSize().clip(RoundedCornerShape(5.dp))
                        .clickable {
                            selected.value = Pair("QQ_Friend", it.id.toString())
                        }) {
                        Row {
                            AsyncImage(
                                load = { loadImageBitmap(it.avatarUrl) },
                                painterFor = { remember { BitmapPainter(it) } },
                                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(5.dp)),
                                contentDescription = "Friend Avatar"
                            )
                            Box(modifier = Modifier.padding(4.dp).fillMaxSize()) {
                                Text(text = it.nameCardOrNick)
                            }
                        }
                    }
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(), adapter = rememberScrollbarAdapter(
                    scrollState = state
                )
            )
        }
        if (selected.value.first != "None") {
            chatUI(selected.value.first, selected.value.second)
        }
    }
}

//聊天界面
@Composable
fun chatUI(type: String, id: String) {
    val contact = contactsMap[type]!![id]!!
    history.clear()
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            contactBar(contact)
            chatBar(contact, type)
        }
    }
}

@Composable
fun contactBar(contact: Contacts) {
    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.1f)) {
        Row {
            Box() {
                Row() {
                    AsyncImage(
                        load = { loadImageBitmap(contact.avatar) },
                        painterFor = { remember { BitmapPainter(it) } },
                        modifier = Modifier.size(64.dp).clip(RoundedCornerShape(5.dp)),
                        contentDescription = "Contacts Avatar"
                    )
                    Text(contact.name, modifier = Modifier.padding(10.dp))
                }

            }
            Text(contact.type, modifier = Modifier.padding(10.dp))
            Text(contact.id, modifier = Modifier.padding(10.dp))
        }
    }
}


@Composable
fun chatBar(contact: Contacts, type: String) {
    val id = when (type) {
        "QQ_Friend" -> "QID"
        "QQ_Group" -> "GID"
        else -> ""
    }
    val relation = calculateRelationIDQQ(userQQBot.userBot.id, contact.id+id)
    CoroutineScope(Dispatchers.IO).launch {
        transaction(chatHistoryQQ) {
            MessagesQQ
                .select { (MessagesQQ.relationID eq relation) and (MessagesQQ.contactID eq contact.id+id) }
                .orderBy(MessagesQQ.timeStamp to SortOrder.ASC)
                .limit(50, 1).forEach {
                    history.add(
                        RenderMessages(
                            it[MessagesQQ.msgID],
                            it[MessagesQQ.relationID],
                            it[MessagesQQ.contactID],
                            it[MessagesQQ.timeStamp],
                            it[MessagesQQ.messageContent]
                        )
                    )
                }
            simpleMessageListenerForChatUI(contact)
        }
    }
    println(history.toString())
    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.65f)) {
        val state = rememberLazyListState()
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxSize(), state = state
        ) {
            items(history) {
                buildMessageCard(it)
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(), adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )
    }
}


class MainPage : Page() {
    @Composable
    override fun content() {
        Card(
            modifier = Modifier.fillMaxSize(), backgroundColor = Color(255, 255, 255), elevation = 0.dp
        ) {
            Scaffold {
                Row(modifier = Modifier.fillMaxSize()) {
                    leftSidebar()
                    sessionList()
                }
            }
        }
    }
}

class SettingsPage : Page() {
    @Composable
    override fun content() {
        val qqid = remember { mutableStateOf("") }
        val password = remember { mutableStateOf("") }
        Card(
            modifier = Modifier.fillMaxSize(), backgroundColor = Color(255, 255, 255), elevation = 0.dp
        ) {
            Scaffold {
                Row(modifier = Modifier.fillMaxSize()) {
                    leftSidebar()
                    Box(modifier = Modifier.fillMaxSize()) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Column {
                                TextField(
                                    value = qqid.value,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    onValueChange = { qqid.value = it },
                                    placeholder = { Text("QQ号") },
                                    modifier = Modifier.padding(12.dp)
                                )
                                TextField(
                                    value = password.value,
                                    onValueChange = { password.value = it },
                                    visualTransformation = PasswordVisualTransformation(),
                                    placeholder = { Text("密码") },
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Button(onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    initQQ(qqid.value, password.value)
                                }
                            }) {
                                Text("登录QQ")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun App() {
    MaterialTheme {
        nav.currentPage().content()
    }
}

