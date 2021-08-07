package com.gorlah.chat

data class MessageData(
    val channel: String,
    val message: String,
    val user: String,
)