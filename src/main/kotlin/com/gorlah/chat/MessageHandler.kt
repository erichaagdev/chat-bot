package com.gorlah.chat

interface MessageHandler {
    fun shouldHandle(messageData: MessageData): Boolean
    fun handle(messageData: MessageData): String
}