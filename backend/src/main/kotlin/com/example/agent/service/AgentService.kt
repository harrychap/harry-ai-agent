package com.example.agent.service

import com.example.agent.ai.ShoppingChatClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AgentService(
    private val shoppingChatClient: ShoppingChatClient
) {
    private val logger = LoggerFactory.getLogger(AgentService::class.java)

    fun processMessage(userMessage: String): String {
        logger.info("Processing message: $userMessage")
        return try {
            shoppingChatClient.chat(userMessage) ?: "Sorry, I couldn't understand your request."
        } catch (e: Exception) {
            logger.error("Error calling Claude: ${e.message}", e)
            "Sorry, I encountered an error processing your request."
        }
    }
}
