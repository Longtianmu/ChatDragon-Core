package net.ltm.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.appoutlet.karavel.Page
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.ltm.adaptors.mirai.initQQ
import net.ltm.nav

class SettingsPage : Page() {
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun content() {
        val qqid = remember { mutableStateOf("") }
        val password = remember { mutableStateOf("") }
        val result = remember { mutableStateOf("") }
        Card(
            modifier = Modifier.fillMaxSize(), backgroundColor = Color(255, 255, 255), elevation = 0.dp
        ) {
            Scaffold {
                Row(modifier = Modifier.fillMaxSize()) {
                    LeftSidebar()
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            OutlinedTextField(
                                value = qqid.value,
                                onValueChange = { qqid.value = it },
                                placeholder = { Text("QQ号") },
                                singleLine = true,
                                modifier = Modifier.padding(12.dp)
                            )
                            OutlinedTextField(
                                value = password.value,
                                onValueChange = { password.value = it },
                                visualTransformation = PasswordVisualTransformation(),
                                placeholder = { Text("密码") },
                                singleLine = true,
                                modifier = Modifier.padding(12.dp)
                            )
                            Button(onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    result.value = initQQ(qqid.value, password.value)
                                    qqid.value = ""
                                    password.value = ""
                                }
                            }) {
                                Text("登录QQ")
                            }
                        }
                    }
                }
            }
            if (result.value != "") {
                AlertDialog(
                    onDismissRequest = {
                        result.value = ""
                    },
                    title = {
                        Text(text = "QQ登录提示框")
                    },
                    text = {
                        Text(result.value)
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                result.value = ""
                                nav.navigate(SettingsPage())
                            }) {
                            Text("关闭窗口")
                        }
                    },
                    modifier = Modifier.fillMaxSize(0.45f)
                )
            }
        }
    }
}