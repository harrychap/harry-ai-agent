package com.example.agent.service

import com.anthropic.client.AnthropicClient
import com.anthropic.models.messages.MessageCreateParams
import com.anthropic.models.messages.Model
import com.example.agent.config.AnthropicConfig
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

/**
 * AI Agent Service - The heart of your shopping assistant.
 *
 * TODO: AI - SHOPPING LIST INTEGRATION GUIDE
 * ==========================================
 *
 * This service should parse user messages and determine their intent:
 *
 * 1. ADD ITEMS: "Add milk to my list", "I need eggs", "Put bread on the list"
 *    -> Call ShoppingItemService.addItem(name, quantity)
 *
 * 2. VIEW LIST: "What's on my list?", "Show my shopping list", "What do I need to buy?"
 *    -> Call ShoppingItemService.getAllItems() and format response
 *
 * 3. REMOVE ITEMS: "Remove milk", "Take eggs off the list", "Delete bread"
 *    -> Find item by name, then call ShoppingItemService.removeItem(id)
 *
 * 4. UPDATE QUANTITY: "Change milk to 3", "I need 2 dozen eggs"
 *    -> Find item by name, then call ShoppingItemService.updateItem(id, quantity)
 *
 * 5. CLEAR LIST: "Clear my list", "Start fresh", "Remove everything"
 *    -> Call ShoppingItemService.clearAll()
 *
 * IMPLEMENTATION APPROACHES:
 *
 * Option A: Use Claude's tool_use/function_calling feature
 *   - Define tools for add/remove/list/update operations
 *   - Let Claude decide which tool to call based on user input
 *   - Parse tool_use blocks from response and execute
 *
 * Option B: Two-step approach
 *   - First call: Ask Claude to parse intent and extract parameters
 *   - Execute the appropriate service method
 *   - Second call: Generate a friendly response about what was done
 *
 * Option C: JSON mode with structured output
 *   - Ask Claude to return JSON with intent and parameters
 *   - Parse JSON and execute service method
 *   - Format response based on result
 *
 * EXAMPLE TOOL DEFINITION (for Option A):
 * ```kotlin
 * val addItemTool = Tool.builder()
 *     .name("add_shopping_item")
 *     .description("Add an item to the shopping list")
 *     .inputSchema(/* JSON schema for name, quantity */)
 *     .build()
 * ```
 */
@Service
class AgentService(
    private val anthropicClient: AnthropicClient?,
    private val anthropicConfig: AnthropicConfig,
    // TODO: AI - Inject ShoppingItemService here to access shopping list operations
    // private val shoppingItemService: ShoppingItemService
) {
    private val logger = LoggerFactory.getLogger(AgentService::class.java)

    /**
     * Process a user message and return the agent's response.
     *
     * TODO: AI - This is the main integration point for your AI agent logic.
     * Currently returns a placeholder response. To implement real AI functionality:
     *
     * 1. The anthropicClient is already configured (if ANTHROPIC_API_KEY is set)
     * 2. Use MessageCreateParams to build your request
     * 3. Add system prompts to define your agent's behavior
     * 4. Parse the response and extract the assistant's message
     *
     * Example implementation:
     * ```kotlin
     * val params = MessageCreateParams.builder()
     *     .maxTokens(anthropicConfig.maxTokens)
     *     .addUserMessage(userMessage)
     *     .model(Model.known(anthropicConfig.model))
     *     .system("You are a helpful shopping assistant...")
     *     .build()
     *
     * val response = anthropicClient?.messages()?.create(params)
     * return response?.content()?.firstOrNull()?.text() ?: "Error processing request"
     * ```
     */
    fun processMessage(userMessage: String, sessionId: UUID): String {
        logger.info("Processing message for session: $sessionId")

        // TODO: AI - Check if the Anthropic client is configured
        if (anthropicClient == null) {
            logger.warn("Anthropic client not configured - returning placeholder response")
            return getPlaceholderResponse(userMessage)
        }

        // TODO: AI - Implement your agent logic here
        // For now, return a placeholder that explains what needs to be implemented
        return getPlaceholderResponse(userMessage)
    }

    /**
     * Returns a placeholder response explaining the integration point.
     *
     * TODO: AI - Replace this with actual Anthropic API calls
     */
    private fun getPlaceholderResponse(userMessage: String): String {
        return """
            I received your message: "$userMessage"

            This is a placeholder response. To enable AI functionality:

            1. Set your ANTHROPIC_API_KEY in the .env file
            2. Implement the processMessage() function in AgentService.kt
            3. Look for TODO: AI comments for guidance

            The shopping list endpoints are available at /api/items for you to integrate.
        """.trimIndent()
    }
}
