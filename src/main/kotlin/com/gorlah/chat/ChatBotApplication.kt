package com.gorlah.chat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.nativex.hint.TypeHint

@ConfigurationPropertiesScan
@SpringBootApplication
@TypeHint(types = [MessageData::class])
class ChatBotApplication

fun main(args: Array<String>) {
    runApplication<ChatBotApplication>(*args)
}