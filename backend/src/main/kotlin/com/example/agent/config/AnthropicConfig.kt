package com.example.agent.config

import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AnthropicConfig {

    private val logger = LoggerFactory.getLogger(AnthropicConfig::class.java)

    @Bean
    fun chatClient(chatModel: ChatModel): ChatClient {
        logger.info("Initializing Spring AI ChatClient with Anthropic")
        return ChatClient.builder(chatModel)
            .defaultSystem("""
                You are a helpful shopping assistant. You help users manage their shopping list.
                Be concise and friendly in your responses.
            """.trimIndent())
            .build()
    }
}
