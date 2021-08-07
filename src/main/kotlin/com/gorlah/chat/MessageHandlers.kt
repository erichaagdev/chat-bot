package com.gorlah.chat

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MessageHandlers {

    @Bean
    fun handleHelloThere() = object : MessageHandler {
        override fun shouldHandle(messageData: MessageData) = messageData.message == "hello there"
        override fun handle(messageData: MessageData) = "general kenobi"
    }

    @Bean
    fun handlePing() = object : MessageHandler {
        override fun shouldHandle(messageData: MessageData) = messageData.message == "ping"
        override fun handle(messageData: MessageData) = "pong"
    }
}