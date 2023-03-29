package com.example.ones_02.navigation.model

import kotlin.collections.HashMap

class ChatDTO (val users: HashMap<String, Boolean> = HashMap(),
                 val comments : HashMap<String, Comment> = HashMap()){
    class Comment(val uid: String? = null, val message: String? = null, val time: String? = null)
}

//data class ChatDTO(
//    var buyerId: String,
//    var sellerId: String?,
//    val key: Long,
//    val itemTitle: String
//) {
//    constructor(): this("", "", 0, "")
//}