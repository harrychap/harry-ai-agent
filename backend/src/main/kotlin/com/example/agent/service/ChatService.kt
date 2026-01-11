package com.example.agent.service

import com.example.agent.dto.ChatMessageDto
import com.example.agent.dto.ChatResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class ChatService(
    private val agentService: AgentService
) {
    private val logger = LoggerFactory.getLogger(ChatService::class.java)

    @Transactional
    fun sendMessage(userMessage: String): ChatResponse {
        logger.info("Processing message: messageLength=${userMessage.length}")

        val userChatMessage = ChatMessageDto(
            id = UUID.randomUUID(),
            role = "user",
            content = userMessage,
            createdAt = Instant.now()
        )

        val agentResponse = agentService.processMessage(userMessage)

        val assistantMessage =
            ChatMessageDto(
                id = UUID.randomUUID(),
                role = "assistant",
                content = agentResponse,
                createdAt = Instant.now()

            )


        return ChatResponse(
            userMessage = userChatMessage.toDto(),
            assistantMessage = assistantMessage.toDto()
        )
    }

    private fun ChatMessageDto.toDto() = ChatMessageDto(
        id = this.id,
        role = this.role,
        content = this.content,
        createdAt = this.createdAt
    )
}
