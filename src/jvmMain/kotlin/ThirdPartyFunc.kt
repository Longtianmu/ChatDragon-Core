import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.Density
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xml.sax.InputSource
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

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
    file.inputStream().buffered().use { androidx.compose.ui.res.loadSvgPainter(it, density) }

fun loadXmlImageVector(file: File, density: Density): ImageVector =
    file.inputStream().buffered().use { androidx.compose.ui.res.loadXmlImageVector(InputSource(it), density) }

/* Loading from network with java.net API */

fun loadImageBitmap(url: String): ImageBitmap =
    URL(url).openStream().buffered().use(::loadImageBitmap)

fun loadSvgPainter(url: String, density: Density): Painter =
    URL(url).openStream().buffered().use { androidx.compose.ui.res.loadSvgPainter(it, density) }

fun loadXmlImageVector(url: String, density: Density): ImageVector =
    URL(url).openStream().buffered().use { androidx.compose.ui.res.loadXmlImageVector(InputSource(it), density) }

fun downloadPicture(url:String){
    val client = OkHttpClient()
    val request = Request.Builder().get()
        .url(url)
        .build()
    val response = client.newCall(request).execute()
    val inputStream = response.body!!.byteStream()
    var fos : FileOutputStream
    val file = File("./cache/mirai/picture/${url}")
    try {
        fos = FileOutputStream(file)
        fos.write(inputStream.readBytes())
        fos.flush();
        fos.close()
    }
    catch (e:java.lang.Exception) {e.printStackTrace()}
}//https://blog.csdn.net/weixin_45509601/article/details/115150181