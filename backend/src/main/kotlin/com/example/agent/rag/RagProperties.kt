package com.example.agent.rag

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for the RAG (Retrieval-Augmented Generation) system.
 *
 * Bound from application.yml under the "rag" prefix.
 *
 * Example configuration:
 * ```yaml
 * rag:
 *   csv:
 *     path: classpath:data/knowledge.csv
 *   vectorstore:
 *     top-k: 5
 *     similarity-threshold: 0.7
 * ```
 */
@ConfigurationProperties(prefix = "rag")
data class RagProperties(
    val csv: CsvProperties = CsvProperties(),
    val vectorstore: VectorStoreProperties = VectorStoreProperties()
)

/**
 * CSV file configuration.
 *
 * TODO: [EXTENSION POINT] - Add additional CSV parsing options:
 *   - delimiter: String = "," (for custom delimiters like ';' or '\t')
 *   - encoding: String = "UTF-8" (for different character encodings)
 *   - skipLines: Int = 0 (to skip header comments)
 */
data class CsvProperties(
    /** Resource path to the CSV knowledge base file */
    val path: String = "classpath:data/knowledge.csv"
)

/**
 * Vector store search configuration.
 *
 * TODO: [EXTENSION POINT] - Add additional search options:
 *   - filterExpression: String? = null (for metadata filtering)
 *   - includeMetadata: Boolean = true (to control metadata in results)
 */
data class VectorStoreProperties(
    /** Number of results to return from similarity search */
    val topK: Int = 5,
    /** Minimum similarity score (0.0 to 1.0) for results */
    val similarityThreshold: Double = 0.7
)
