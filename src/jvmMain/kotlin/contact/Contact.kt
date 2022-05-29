package contact

import message.messageListener

class Contacts() {
    var id: String = ""
    var name: String = ""
    var avatar: String = ""
    lateinit var type: String

    constructor(type: String, id: String, name: String, avatar: String) : this() {
        this.id = id
        this.name = name
        this.avatar = avatar
        this.type = type
        messageListener(this)
    }
}