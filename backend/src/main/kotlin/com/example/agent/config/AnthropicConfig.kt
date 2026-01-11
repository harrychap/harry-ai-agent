package com.example.agent.config

import com.anthropic.client.AnthropicClient
import com.anthropic.client.okhttp.AnthropicOkHttpClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for the Anthropic Claude SDK.
 *
 * SDK SETUP EXPLANATION:
 * ======================
 *
 * This class configures the official Anthropic Java SDK (com.anthropic:anthropic-java).
 *
 * CONFIGURATION PROPERTIES:
 * - anthropic.api-key: Your Anthropic API key (from console.anthropic.com)
 * - anthropic.model: The Claude model to use (default: claude-sonnet-4-20250514)
 * - anthropic.max-tokens: Maximum tokens in response (default: 1024)
 *
 * SETTING YOUR API KEY:
 * 1. Create a .env file in the project root (copy from .env.example)
 * 2. Add: ANTHROPIC_API_KEY=sk-ant-xxxxx
 * 3. Docker Compose will pass this to the backend container
 *
 * AVAILABLE MODELS (as of 2025):
 * - claude-opus-4-20250514: Most capable, best for complex tasks
 * - claude-sonnet-4-20250514: Balanced performance and cost (recommended)
 * - claude-haiku-3-20250514: Fastest, best for simple tasks
 *
 * SDK USAGE EXAMPLES:
 *
 * Basic message:
 * ```kotlin
 * val params = MessageCreateParams.builder()
 *     .maxTokens(maxTokens)
 *     .addUserMessage("Hello!")
 *     .model(Model.known(model))
 *     .build()
 * val response = anthropicClient.messages().create(params)
 * ```
 *
 * With system prompt:
 * ```kotlin
 * val params = MessageCreateParams.builder()
 *     .maxTokens(maxTokens)
 *     .system("You are a helpful shopping assistant...")
 *     .addUserMessage(userMessage)
 *     .model(Model.known(model))
 *     .build()
 * ```
 *
 * With tools (function calling):
 * ```kotlin
 * val tool = Tool.builder()
 *     .name("add_item")
 *     .description("Add an item to the shopping list")
 *     .inputSchema(JsonSchema.builder()
 *         .properties(mapOf("name" to ..., "quantity" to ...))
 *         .required(listOf("name"))
 *         .build())
 *     .build()
 *
 * val params = MessageCreateParams.builder()
 *     .tools(listOf(tool))
 *     // ... rest of params
 *     .build()
 * ```
 *
 * STREAMING (for real-time responses):
 * ```kotlin
 * anthropicClient.messages().createStreaming(params).use { stream ->
 *     stream.forEach { event ->
 *         when (event) {
 *             is ContentBlockDelta -> print(event.delta.text)
 *             // handle other events
 *         }
 *     }
 * }
 * ```
 *
 * See: https://docs.anthropic.com/en/docs/sdks
 */
@Configuration
class AnthropicConfig {

    private val logger = LoggerFactory.getLogger(AnthropicConfig::class.java)

    @Value("\${anthropic.api-key:}")
    private lateinit var apiKey: String

    @Value("\${anthropic.model:claude-sonnet-4-20250514}")
    lateinit var model: String

    @Value("\${anthropic.max-tokens:1024}")
    var maxTokens: Long = 1024

    @Bean
    fun anthropicClient(): AnthropicClient? {
        if (apiKey.isBlank()) {
            logger.warn("ANTHROPIC_API_KEY not configured. AI features will use placeholder responses.")
            return null
        }

        logger.info("Initializing Anthropic client with model: $model")
        return AnthropicOkHttpClient.builder()
            .apiKey(apiKey)
            .build()
    }
}
