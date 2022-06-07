package net.ltm.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lt.load_the_image.rememberImagePainter
import io.appoutlet.karavel.Page
import net.ltm.contactListQQ
import net.ltm.contactsMap
import net.ltm.groupListQQ
import net.ltm.nav
import net.mamoe.mirai.contact.nameCardOrNick
import java.lang.Thread.sleep

//左侧边栏
@Composable
fun LeftSidebar() {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(60.dp)
            .background(Color(247, 242, 243))
            .padding(10.dp)
    ) {
        IconButton(
            onClick = {
                nav.navigate(MainPage())
            }
        ) {
            Image(
                imageVector = Icons.Default.Email,
                contentDescription = "Chats",
                modifier = Modifier.size(32.dp)
            )
        }
        /*Image(
            contentDescription = "Contact Lists",
            painter = painterResource("")
        )*/
        Column(
            modifier = Modifier.weight(10F),
            verticalArrangement = Arrangement.Bottom
        ) {
            IconButton(
                onClick = {
                    nav.navigate(SettingsPage())
                }
            ) {
                Image(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Settings",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

//联系人列表
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SessionList() {
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
                            selected.value = Pair("Clear", "")
                            sleep(10)
                            selected.value = Pair("QQ_Group", it.id.toString())
                        }) {
                        Row {
                            Image(
                                painter = rememberImagePainter(it.avatarUrl),
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
                            selected.value = Pair("Clear", "")
                            sleep(10)
                            selected.value = Pair("QQ_Friend", it.id.toString())
                        }) {
                        Row {
                            Image(
                                painter = rememberImagePainter(it.avatarUrl),
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
        if (selected.value.first == "Clear") {
            Card(modifier = Modifier.fillMaxSize()) {
                Text("", modifier = Modifier.fillMaxSize())
            }
        } else if (selected.value.first != "None") {
            contactsMap[selected.value.first]?.get(selected.value.second)?.let { ChatUI(it) }
        }
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
                    LeftSidebar()
                    SessionList()
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

