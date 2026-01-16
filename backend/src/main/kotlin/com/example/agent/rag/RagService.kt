package com.example.agent.rag

import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service

/**
 * Service responsible for RAG (Retrieval-Augmented Generation) operations.
 *
 * This service provides methods for:
 * - Semantic search against the vector store
 * - Context retrieval for prompt augmentation
 *
 * TODO: [EXTENSION POINT] - Implement the following for your use case:
 *   - formatContext(): Customize how documents become prompt context
 *   - filterResults(): Add post-retrieval filtering logic
 */
@Service
class RagService(
    private val vectorStore: VectorStore,
    private val ragProperties: RagProperties
) {
    private val logger = LoggerFactory.getLogger(RagService::class.java)

    /**
     * Search for documents relevant to the query.
     *
     * @param query User's search query
     * @param topK Maximum number of results (uses config default if null)
     * @return List of relevant documents
     */
    fun search(query: String, topK: Int? = null): List<Document> {
        logger.debug("Searching for: '$query'")

        val searchRequest = SearchRequest.builder()
            .query(query)
            .topK(topK ?: ragProperties.vectorstore.topK)
            .similarityThreshold(ragProperties.vectorstore.similarityThreshold)
            .build()

        val results = vectorStore.similaritySearch(searchRequest)
        logger.debug("Found ${results.size} results for query: '$query'")

        return results
    }

    /**
     * Get augmented context for a query.
     *
     * This method retrieves relevant documents and formats them
     * as a context string suitable for injection into AI prompts.
     *
     * @param query User's question
     * @return Formatted context string for prompt injection
     */
    fun getContext(query: String): String {
        val documents = search(query)

        if (documents.isEmpty()) {
            logger.debug("No relevant context found for: '$query'")
            return ""
        }

        return formatContext(documents)
    }

    /**
     * Format retrieved documents as context for prompt augmentation.
     *
     * TODO: [EXTENSION POINT] - Customize context formatting for your use case
     */
    private fun formatContext(documents: List<Document>): String {
        return buildString {
            appendLine("Context from knowledge base:")
            appendLine()
            documents.forEachIndexed { index, doc ->
                appendLine("[${index + 1}] ${doc.text}")
                val source = doc.metadata["source"]
                if (source != null) {
                    appendLine("    Source: $source")
                }
                appendLine()
            }
        }.trim()
    }

    /**
     * Check if the RAG service is ready to handle queries.
     */
    fun isReady(): Boolean {
        return try {
            val testRequest = SearchRequest.builder()
                .query("test")
                .topK(1)
                .build()
            vectorStore.similaritySearch(testRequest)
            true
        } catch (e: Exception) {
            logger.warn("RAG service not ready: ${e.message}")
            false
        }
    }
}
