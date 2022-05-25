import adaptors.mirai.botSets
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.res.loadXmlImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import contact.Contacts
import io.appoutlet.karavel.Karavel
import io.appoutlet.karavel.Page
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.nameCardOrNick
import org.xml.sax.InputSource
import java.io.File
import java.io.IOException
import java.net.URL
import kotlin.io.use

@Composable
fun <T> AsyncImage(
    load: suspend () -> T,
    painterFor: @Composable (T) -> Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val image: T? by produceState<T?>(null) {
        value = withContext(Dispatchers.IO) {
            try {
                load()
            } catch (e: IOException) {
                // instead of printing to console, you can also write this to log,
                // or show some error placeholder
                e.printStackTrace()
                null
            }
        }
    }

    if (image != null) {
        Image(
            painter = painterFor(image!!),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}

/* Loading from file with java.io API */

fun loadImageBitmap(file: File): ImageBitmap =
    file.inputStream().buffered().use(::loadImageBitmap)

fun loadSvgPainter(file: File, density: Density): Painter =
    file.inputStream().buffered().use { loadSvgPainter(it, density) }

fun loadXmlImageVector(file: File, density: Density): ImageVector =
    file.inputStream().buffered().use { loadXmlImageVector(InputSource(it), density) }

/* Loading from network with java.net API */

fun loadImageBitmap(url: String): ImageBitmap =
    URL(url).openStream().buffered().use(::loadImageBitmap)

fun loadSvgPainter(url: String, density: Density): Painter =
    URL(url).openStream().buffered().use { loadSvgPainter(it, density) }

fun loadXmlImageVector(url: String, density: Density): ImageVector =
    URL(url).openStream().buffered().use { loadXmlImageVector(InputSource(it), density) }

val nav = Karavel(MainPage())
val QQgroupsList =  mutableStateListOf<Group>()
val QQcontactList = mutableStateListOf<Friend>()

@Composable
fun leftSidebar() {
    Column(//左边栏
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxHeight().width(66.dp).background(Color(247, 242, 243))
    ) {
        Image(
            contentDescription = "Chat Lists",
            painter = painterResource("icons/112-bubbles3.svg"),
            modifier = Modifier.padding(vertical = 20.dp).size(42.dp).clickable {
                nav.navigate(MainPage())
            }
        )
        /*Image(
            contentDescription = "Contact Lists",
            painter = painterResource("")
        )*/
        Image(
            contentDescription = "Settings",
            painter = painterResource("icons/147-equalizer.svg"),
            modifier = Modifier.padding(vertical = 200.dp).size(42.dp).clickable {
                nav.navigate(SettingsPage())
            }
        )
    }
}//左侧边栏

@Composable
fun lazyScrollable() {//联系人列表
    val selected = remember { mutableStateOf(Contacts("")) }
    Row(modifier = Modifier.fillMaxSize()){
        Box(
            modifier = Modifier.fillMaxHeight().width(233.dp)
                .background(color = Color(180, 180, 180))
                .padding(10.dp)
        ) {
            val state = rememberLazyListState()
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxSize().padding(end = 12.dp),
                state = state
            ) {
                items(QQgroupsList){
                    Box(modifier = Modifier.align(Alignment.Center).fillMaxSize().clip(RoundedCornerShape(4.dp)).clickable {
                        selected.value = Contacts("QQ_Group")
                        selected.value.id = it.id
                        selected.value.name = it.name
                    }){
                        Row{
                            AsyncImage(
                                load = { loadImageBitmap("https://p.qlogo.cn/gh/${it.id}/${it.id}/0") },
                                painterFor = { remember { BitmapPainter(it) } },
                                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(4.dp)),
                                contentDescription = "Group Avatar"
                            )
                            Box(modifier = Modifier.padding(4.dp).fillMaxSize()){
                                Text(text = it.name)
                            }
                        }
                    }
                }
                items(QQcontactList){
                    Box(modifier = Modifier.align(Alignment.Center).fillMaxSize().clip(RoundedCornerShape(4.dp)).clickable {
                        selected.value = Contacts("QQ_Friend")
                        selected.value.id = it.id
                        selected.value.name = it.nameCardOrNick
                    }){
                        Row{
                            AsyncImage(
                                load = { loadImageBitmap("https://q1.qlogo.cn/g?b=qq&s=0&nk=${it.id}") },
                                painterFor = { remember { BitmapPainter(it) } },
                                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(4.dp)),
                                contentDescription = "Group Avatar"
                            )
                            Box(modifier = Modifier.padding(4.dp).fillMaxSize()){
                                Text(text = it.nameCardOrNick)
                            }
                        }
                    }
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = state
                )
            )
        }
        if(selected.value.type!=""){
            chatUI(selected.value)
        }
    }
}

@Composable
fun chatUI(contact:Contacts){
    Box(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier.fillMaxSize()){
            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.2f)){
                Text(contact.name)
            }
            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.4f)){
                LazyColumn(modifier = Modifier.fillMaxSize()){

                }
            }
        }
    }
}

class MainPage : Page() {
    @Composable
    override fun content() {
        Card(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = Color(255, 255, 255),
            elevation = 0.dp
        ) {
            Scaffold {
                Row(modifier = Modifier.fillMaxSize()) {
                    leftSidebar()
                    lazyScrollable()
                }
            }
        }
    }
}

class SettingsPage : Page() {
    @Composable
    override fun content() {
        var qqid = remember { mutableStateOf("") }
        var password = remember { mutableStateOf("") }
        Card(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = Color(255, 255, 255),
            elevation = 0.dp
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
                                    modifier = Modifier.padding(12.dp)
                                )
                                TextField(
                                    value = password.value,
                                    onValueChange = { password.value = it },
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Button(onClick = {
                                userQQBot= botSets(qqid.value.toLong(),password.value)
                                CoroutineScope(Dispatchers.IO).launch {
                                    userQQBot.userBot.login()
                                    QQcontactList.clear()
                                    QQgroupsList.clear()
                                    userQQBot.userBot.friends.forEach { QQcontactList.add(it) }
                                    userQQBot.userBot.groups.forEach { QQgroupsList.add(it) }
                                }
                            }){
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



