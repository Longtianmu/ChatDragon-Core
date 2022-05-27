package message

class Messages(type:String){
    constructor(type:String,content:String):this(type){
        this.content =content
    }
    var content:String =""
}
