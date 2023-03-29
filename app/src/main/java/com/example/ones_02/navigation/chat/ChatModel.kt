package com.example.ones_02.navigation.chat

import java.util.HashMap

/**
 * Created by myeongsic on 2017. 9. 25..
 */
class ChatModel {
    var users: Map<String, Boolean> = HashMap() //채팅방의 유저들
    var comments: Map<String, Comment> = HashMap() //채팅방의 대화내용

    class Comment {
        var uid: String? = null
        var message: String? = null
        var timestamp: Any? = null
    }
}