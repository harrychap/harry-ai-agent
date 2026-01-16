package com.example.agent.rag

import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service

@Service
class RagService(
    private val vectorStore: VectorStore,
    private val ragProperties: RagProperties
) {
    private val logger = LoggerFactory.getLogger(RagService::class.java)

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

    fun getContext(query: String): String {
        val documents = search(query)

        if (documents.isEmpty()) {
            logger.debug("No relevant context found for: '$query'")
            return ""
        }

        return formatContext(documents)
    }

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
}
