@file:JvmName("net.ltm.MainKt")

package net.ltm

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import net.ltm.ui.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication, title = "Chat-Dragon"
    ) {
        App()
        initApp()
    }
}

