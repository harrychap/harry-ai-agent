package com.example.agent.rag

import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.Instant

@Service
class CsvDocumentLoader(
    private val ragProperties: RagProperties,
    private val resourceLoader: ResourceLoader
) {
    private val logger = LoggerFactory.getLogger(CsvDocumentLoader::class.java)

    fun loadDocuments(): List<Document> {
        val csvPath = ragProperties.csv.path
        logger.info("Loading CSV from: $csvPath")

        return try {
            val resource = resourceLoader.getResource(csvPath)

            if (!resource.exists()) {
                logger.warn("CSV file not found at: $csvPath")
                return emptyList()
            }

            val documents = mutableListOf<Document>()
            var headers: List<String>? = null

            resource.inputStream.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use { reader ->
                    reader.lineSequence().forEachIndexed { index, line ->
                        if (index == 0) {
                            headers = parseCsvLine(line)
                            logger.debug("CSV headers: $headers")
                        } else {
                            val values = parseCsvLine(line)
                            if (values.isNotEmpty() && headers != null) {
                                val record = headers.zip(values).toMap()
                                val document = createDocument(record, index, csvPath)
                                documents.add(document)
                            }
                        }
                    }
                }
            }

            logger.info("Loaded ${documents.size} documents from CSV")
            documents
        } catch (e: Exception) {
            logger.error("Error loading CSV from $csvPath: ${e.message}", e)
            emptyList()
        }
    }

    private fun parseCsvLine(line: String): List<String> {
        return line.split(",").map { it.trim() }
    }

    private fun createDocument(
        record: Map<String, String>,
        rowNumber: Int,
        sourcePath: String
    ): Document {
        val content = formatRowAsContent(record)
        val metadata = extractMetadata(record, rowNumber, sourcePath)
        return Document(content, metadata)
    }

    private fun formatRowAsContent(record: Map<String, String>): String {
        return record.entries
            .filter { it.value.isNotBlank() }
            .joinToString(" | ") { "${it.key}: ${it.value}" }
    }

    private fun extractMetadata(
        record: Map<String, String>,
        rowNumber: Int,
        sourcePath: String
    ): Map<String, Any> {
        val metadata = mutableMapOf<String, Any>(
            "source" to sourcePath,
            "rowNumber" to rowNumber,
            "timestamp" to Instant.now().toString()
        )
        return metadata
    }
}
