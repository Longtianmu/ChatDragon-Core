import adaptors.mirai.botSets
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.appoutlet.karavel.Karavel
import io.appoutlet.karavel.Page
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

val nav = Karavel(MainPage())

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
                    Text("aaaaaa")
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
                                    onValueChange = { qqid.value = it.toString() },
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
                                CoroutineScope(Dispatchers.IO).launch { userQQBot.userBot.login() }
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



