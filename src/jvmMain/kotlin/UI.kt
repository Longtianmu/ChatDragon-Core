import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun App() {
    var a =0
    Card(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color(255,255,255),
        elevation = 0.dp
    ){
        Scaffold {
            Row(modifier = Modifier.fillMaxSize()){
                Column(//左边栏
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight().width(66.dp).background(Color(247, 242, 243))
                ){
                    Image(
                        contentDescription = "Chat Lists",
                        painter = painterResource("icons/112-bubbles3.svg"),
                        modifier = Modifier.padding(vertical = 20.dp).size(42.dp).clickable {

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
                            a=1
                        }
                    )
                }

            }
        }
    }
}

