package com.example.agent.rag

import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

/**
 * Component responsible for initializing the vector store with documents on startup.
 *
 * This component implements CommandLineRunner to execute after the Spring context
 * is fully initialized. It loads documents from the CSV file and adds them to
 * the vector store for semantic search.
 *
 * TODO: [EXTENSION POINT] - Implement the following for your use case:
 *   - shouldInitialize(): Control when to re-index
 *   - onInitializationComplete(): Post-initialization hooks
 */
@Component
class VectorStoreInitializer(
    private val csvDocumentLoader: CsvDocumentLoader,
    private val vectorStore: VectorStore
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(VectorStoreInitializer::class.java)

    companion object {
        private const val BATCH_SIZE = 100
    }

    override fun run(vararg args: String?) {
        logger.info("Starting vector store initialization...")

        try {
            initialize()
        } catch (e: Exception) {
            logger.error("Failed to initialize vector store: ${e.message}", e)
        }
    }

    fun initialize() {
        logger.info("Loading documents from CSV...")
        val documents = csvDocumentLoader.loadDocuments()

        if (documents.isEmpty()) {
            logger.warn("No documents loaded from CSV. Vector store will be empty.")
            return
        }

        logger.info("Indexing ${documents.size} documents into vector store...")
        addDocumentsInBatches(documents)
        logger.info("Vector store initialization complete. Indexed ${documents.size} documents.")
    }

    private fun addDocumentsInBatches(documents: List<Document>) {
        val totalBatches = (documents.size + BATCH_SIZE - 1) / BATCH_SIZE

        documents.chunked(BATCH_SIZE).forEachIndexed { index, batch ->
            logger.debug("Processing batch ${index + 1}/$totalBatches (${batch.size} documents)")
            try {
                vectorStore.add(batch)
                logger.info("Indexed batch ${index + 1}/$totalBatches")
            } catch (e: Exception) {
                logger.error("Failed to index batch ${index + 1}: ${e.message}", e)
                throw e
            }
        }
    }
}
