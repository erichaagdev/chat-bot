package com.gorlah.chat

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.reactive.socket.client.WebSocketClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI

@Configuration
@Profile("!test")
class MessageListenerConfiguration(
    private val messageHandlers: List<MessageHandler>,
    private val properties: BotProperties,
) {

    private val log = LoggerFactory.getLogger(ChatBotApplication::class.java)
    private val messageRegex = Regex(""":(.+)!.*#(.+)\s:((.|\n)+)""")

    @Bean
    fun messageListener(webSocketClient: WebSocketClient) = ApplicationListener<ApplicationReadyEvent> {
        webSocketClient.execute(URI("wss://irc-ws.chat.twitch.tv:443")) { session ->
            session.send("PASS oauth:${properties.token}")
                .switchIfEmpty(session.send("NICK ${properties.name}"))
                .switchIfEmpty(session.send("JOIN #${properties.channel}"))
                .thenMany(session.receive())
                .map { it.payloadAsText.trim() }
                .doOnNext { log.info(it) }
                .flatMap { handleMessage(it) }
                .doOnNext { log.info(it) }
                .doOnNext { session.send(it).subscribe() }
                .then()
        }.subscribe { log.info("Disconnected") }
    }

    private fun WebSocketSession.send(message: String): Mono<Void> {
        return this.send(Mono.just(this.textMessage(message)))
    }

    private fun handleMessage(message: String): Mono<String> {
        if (message.startsWith("PING")) {
            return Mono.just("PONG")
        }

        val groupValues = messageRegex.matchEntire(message)?.groupValues

        if (groupValues != null && groupValues.isNotEmpty()) {
            val messageData = asMessageData(groupValues)
            return Flux.fromIterable(messageHandlers)
                .filter { it.shouldHandle(messageData) }
                .next()
                .map { it.handle(messageData) }
                .map { "PRIVMSG #${messageData.channel} :$it" }
        }

        return Mono.empty()
    }

    private fun asMessageData(groupValues: List<String>): MessageData {
        return MessageData(
            channel = groupValues[2],
            user = groupValues[1],
            message = groupValues[3],
        )
    }
}