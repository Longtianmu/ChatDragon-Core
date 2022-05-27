import adaptors.mirai.botSets
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.squareup.sqldelight.db.*
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver

var userQQBot =botSets(123456789,"")
fun main() = application {
    val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    //Database.Schema.create(driver)
    Window(
        onCloseRequest = ::exitApplication, title = "Chat-Dragon"
    ) {
        App()
    }
}
