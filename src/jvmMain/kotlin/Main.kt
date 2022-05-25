import adaptors.mirai.botSets
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

var userQQBot =botSets(123456789,"")
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication, title = "Chat-Dragon"
    ) {
        App()
    }
}
