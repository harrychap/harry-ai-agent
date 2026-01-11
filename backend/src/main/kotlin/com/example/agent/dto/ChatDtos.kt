package com.example.agent.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.*

data class ChatMessageDto(
    val id: UUID?,
    val role: String,
    val content: String,
    val createdAt: Instant?
)

data class SendMessageRequest(
    @field:NotBlank(message = "Message cannot be blank")
    @field:Size(max = 10000, message = "Message cannot exceed 10000 characters")
    val message: String
    )

data class ChatResponse(
    val userMessage: ChatMessageDto,
    val assistantMessage: ChatMessageDto
)
