package com.example.agent.controller

import com.example.agent.dto.ChatResponse
import com.example.agent.dto.SendMessageRequest
import com.example.agent.service.ChatService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller for chat operations.
 *
 * MESSAGE FLOW EXPLANATION:
 * =========================
 *
 * 1. Frontend sends POST /api/chat with user message
 *    -> ChatController.sendMessage() receives the request
 *
 * 2. ChatController delegates to ChatService.sendMessage()
 *    -> ChatService creates/retrieves session
 *    -> ChatService saves user message to database
 *    -> ChatService calls AgentService.processMessage()
 *
 * 3. AgentService processes the message
 *    -> TODO: AI - This is where you implement Claude integration
 *    -> Currently returns placeholder response
 *
 * 4. Response flows back up the chain
 *    -> ChatService saves assistant message to database
 *    -> ChatService returns ChatResponse with both messages
 *    -> ChatController returns response to frontend
 *
 * 5. Frontend displays the response
 *    -> useChat hook updates message state
 *    -> ChatWindow re-renders with new messages
 *
 * SESSION MANAGEMENT:
 * - First message: sessionId is null, new session created
 * - Subsequent messages: sessionId included, session retrieved
 * - All messages in a session share the same sessionId
 *
 * EXTENDING THE CHAT:
 * - To add streaming: Use Server-Sent Events (SSE) with Anthropic streaming API
 * - To add context: Load previous messages in AgentService and include in prompt
 * - To add memory: Store key facts in session metadata
 */
@RestController
@RequestMapping("/chat")
class ChatController(
    private val chatService: ChatService
) {
    private val logger = LoggerFactory.getLogger(ChatController::class.java)

    /**
     * Send a message to the AI agent.
     *
     * POST /api/chat
     *
     * Request body:
     * {
     *   "message": "Add milk to my shopping list",
     *   "sessionId": "optional-uuid"
     * }
     *
     * If sessionId is not provided, a new session is created.
     */
    @PostMapping
    fun sendMessage(
        @Valid @RequestBody request: SendMessageRequest
    ): ResponseEntity<ChatResponse> {
        logger.info("Received chat message")

        val response = chatService.sendMessage(
            userMessage = request.message,
        )

        return ResponseEntity.ok(response)
    }
}
