package net.ltm.ui

import net.ltm.dataDir
import net.ltm.userQQBot
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

fun downloadPicture(url: String) {
    val client = OkHttpClient()
    val request = Request.Builder().get()
        .url(url)
        .build()
    val response = client.newCall(request).execute()
    val types = response.headers["Content-Type"]?.toMediaType()?.subtype
    val inputStream = response.body!!.byteStream()
    val fos: FileOutputStream
    val file =
        File("$dataDir/cache/qq/pictures/${userQQBot.userBot.id}/${convertUrlToValidFilePath(url)}")// :转换为~ /转换为% ?转换为+
    if (!file.exists()) {
        file.mkdirs()
    }
    try {
        fos = FileOutputStream(File("$file/temp.$types"))
        fos.write(inputStream.readBytes())
        fos.flush()
        fos.close()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return
}//https://blog.csdn.net/weixin_45509601/article/details/115150181

fun convertUrlToValidFilePath(url: String): String {
    return url.replace(":", "~").replace("/", "%").replace("?", "+")
}

