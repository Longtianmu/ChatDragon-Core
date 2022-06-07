package net.ltm.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.ltm.contact.Contacts
import net.ltm.contactsMap
import net.ltm.message.sendMessage

//聊天界面
@Composable
fun ChatUI(type: String, id: String) {
    val contact = contactsMap[type]!![id]!!
    val content = mutableStateOf("")
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            ContactBar(contact)
            ChatBar(contact)
            TypeBar(contact, content)
        }
    }
}

@Composable
fun ContactBar(contact: Contacts) {
    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.10f)) {
        Row {
            Box {
                Row {
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
fun ChatBar(contact: Contacts) {
    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.70f)) {
        val state = rememberLazyListState()
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxSize(), state = state
        ) {
            items(contact.history) {
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

@Composable
fun TypeBar(contact: Contacts, content: MutableState<String>) {
    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.20f)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                OutlinedTextField(
                    value = content.value,
                    onValueChange = { content.value = it },
                    placeholder = { Text("聊天内容") },
                )
                IconButton(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            sendMessage(contact, content.value)
                            contact.history.sortBy { it.timeStamp }
                            content.value = ""
                        }
                    }
                ) {
                    Image(
                        imageVector = Icons.Default.Send,
                        contentDescription = "发送消息",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}