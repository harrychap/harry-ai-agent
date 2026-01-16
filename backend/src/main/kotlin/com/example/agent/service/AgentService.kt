package com.example.agent.service

import com.example.agent.ai.ShoppingChatClient
import com.example.agent.rag.RagService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AgentService(
    private val shoppingChatClient: ShoppingChatClient,
    private val ragService: RagService
) {
    private val logger = LoggerFactory.getLogger(AgentService::class.java)

    fun processMessage(userMessage: String): String {
        logger.info("Processing message: $userMessage")
        val context = ragService.getContext(userMessage)
        logger.info("RAG context retrieved: ${context.take(500)}...")

        val augmentedPrompt = if (context.isNotBlank()) {
            "$context\n\nUser question: $userMessage"
        } else {
            userMessage
        }
        logger.info("Augmented prompt: $augmentedPrompt")

        return try {
            shoppingChatClient.chat(augmentedPrompt) ?: "Sorry, I couldn't understand your request."
        } catch (e: Exception) {
            logger.error("Error calling Claude: ${e.message}", e)
            "Sorry, I encountered an error processing your request."
        }
    }
}
