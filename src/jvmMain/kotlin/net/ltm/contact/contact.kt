package net.ltm.contact

import androidx.compose.runtime.mutableStateListOf
import net.ltm.datas.RenderMessages
import net.ltm.message.messageListener

class Contacts() {
    var id: String = ""
    var name: String = ""
    var avatar: String = ""
    var history = mutableStateListOf<RenderMessages>()
    private val size = 100
    lateinit var type: String

    constructor(type: String, id: String, name: String, avatar: String) : this() {
        this.id = id
        this.name = name
        this.avatar = avatar
        this.type = type
        messageListener(this)
    }

    fun addHistory(message: RenderMessages) {
        synchronized(this.history) {
            if (history.size == size) {
                val tmp = mutableStateListOf<RenderMessages>()
                System.arraycopy(history, 1, tmp, 0, history.size - 1)
                history = tmp
            }
            history.add(message)
            return
        }
    }
}