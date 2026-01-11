package com.example.agent.service

import com.example.agent.dto.ChatMessageDto
import com.example.agent.dto.ChatResponse
import com.example.agent.dto.ChatHistoryResponse
import com.example.agent.model.ChatMessage
import com.example.agent.model.ChatSession
import com.example.agent.repository.ChatMessageRepository
import com.example.agent.repository.ChatSessionRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ChatService(
    private val sessionRepository: ChatSessionRepository,
    private val messageRepository: ChatMessageRepository,
    private val agentService: AgentService
) {
    private val logger = LoggerFactory.getLogger(ChatService::class.java)

    @Transactional
    fun sendMessage(userMessage: String, sessionId: UUID?): ChatResponse {
        logger.info("Processing message: sessionId=$sessionId, messageLength=${userMessage.length}")

        // Get or create session
        val session = if (sessionId != null) {
            sessionRepository.findById(sessionId)
                .orElseThrow { NoSuchElementException("Session not found: $sessionId") }
        } else {
            logger.info("Creating new chat session")
            sessionRepository.save(ChatSession())
        }

        // Save user message
        val userChatMessage = messageRepository.save(
            ChatMessage(
                session = session,
                role = "user",
                content = userMessage
            )
        )
        logger.debug("Saved user message: id=${userChatMessage.id}")

        // Get agent response
        val agentResponse = agentService.processMessage(userMessage, session.id!!)

        // Save agent message
        val assistantMessage = messageRepository.save(
            ChatMessage(
                session = session,
                role = "assistant",
                content = agentResponse
            )
        )
        logger.debug("Saved assistant message: id=${assistantMessage.id}")

        return ChatResponse(
            sessionId = session.id!!,
            userMessage = userChatMessage.toDto(),
            assistantMessage = assistantMessage.toDto()
        )
    }

    @Transactional(readOnly = true)
    fun getChatHistory(sessionId: UUID): ChatHistoryResponse {
        logger.debug("Fetching chat history for session: $sessionId")

        // Verify session exists
        if (!sessionRepository.existsById(sessionId)) {
            throw NoSuchElementException("Session not found: $sessionId")
        }

        val messages = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)

        return ChatHistoryResponse(
            sessionId = sessionId,
            messages = messages.map { it.toDto() }
        )
    }

    private fun ChatMessage.toDto() = ChatMessageDto(
        id = this.id,
        role = this.role,
        content = this.content,
        createdAt = this.createdAt
    )
}
