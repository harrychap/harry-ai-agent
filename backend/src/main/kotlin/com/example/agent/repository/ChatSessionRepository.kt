package com.example.agent.repository

import com.example.agent.model.ChatSession
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ChatSessionRepository : JpaRepository<ChatSession, UUID> {

    fun findByOrderByCreatedAtDesc(): List<ChatSession>
}
