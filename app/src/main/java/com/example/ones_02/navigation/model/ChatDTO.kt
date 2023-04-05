package com.example.ones_02.navigation.model

import kotlin.collections.HashMap

data class ChatDTO(
    var users: HashMap<String, Boolean> = HashMap(),
    var comments: ArrayList<Comment> = ArrayList()
) {
    data class Comment(
        var uid: String? = null,
        var message: String? = null,
        var time: String? = null
    )
}
