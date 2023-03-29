package com.example.ones_02.navigation.model

data class ChatItem(
    val senderId: String,
    val message: String,
) {
    constructor(): this("", "")
}