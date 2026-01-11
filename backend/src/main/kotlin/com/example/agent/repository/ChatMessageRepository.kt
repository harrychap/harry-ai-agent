package com.example.agent.repository

import com.example.agent.model.ChatMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ChatMessageRepository : JpaRepository<ChatMessage, UUID> {

    fun findBySessionIdOrderByCreatedAtAsc(sessionId: UUID): List<ChatMessage>
}
