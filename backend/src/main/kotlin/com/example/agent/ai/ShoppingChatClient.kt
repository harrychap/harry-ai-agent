package com.example.agent.ai

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.ChatClient.AdvisorSpec
import org.springframework.ai.chat.client.ChatClient.PromptUserSpec
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.SessionScope
import java.util.*


@Component
@SessionScope
class ShoppingChatClient(chatModel: ChatModel, chatMemory: ChatMemory, toolCallbackProvider: SyncMcpToolCallbackProvider) {
    private val chatClient: ChatClient = ChatClient.builder(chatModel)
        .defaultSystem("""
            You are a helpful shopping assistant. You help users manage their shopping list.
            Be concise and friendly in your responses.
        """.trimIndent())
        .defaultToolCallbacks(*toolCallbackProvider.toolCallbacks)
        .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
        .build()
    private val conversationId: String = UUID.randomUUID().toString()

    fun chat(prompt: String): String? {
        return chatClient.prompt()
            .user { userMessage: PromptUserSpec? -> userMessage?.text(prompt) }
            .advisors { a: AdvisorSpec? -> a?.param(ChatMemory.CONVERSATION_ID, conversationId) }
            .call()
            .content()
    }
}