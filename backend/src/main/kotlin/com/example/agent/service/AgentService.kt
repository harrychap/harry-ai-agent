package com.example.agent.service

import com.example.agent.tools.ShoppingTools
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service

@Service
class AgentService(
    private val chatClient: ChatClient,
    private val shoppingTools: ShoppingTools
) {
    private val logger = LoggerFactory.getLogger(AgentService::class.java)

    fun processMessage(userMessage: String): String {
         return try {
             chatClient.prompt()
                 .user(userMessage)
                 .tools(shoppingTools)
                 .call()
                 .content() ?: "Sorry, I couldn't process that request."
         } catch (e: Exception) {
             logger.error("Error calling Claude: ${e.message}", e)
             "Sorry, I encountered an error processing your request."
         }
    }
}
