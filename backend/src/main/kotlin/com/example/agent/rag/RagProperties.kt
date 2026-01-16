package com.example.agent.rag

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "rag")
data class RagProperties(
    val csv: CsvProperties = CsvProperties(),
    val vectorstore: VectorStoreProperties = VectorStoreProperties()
)

data class CsvProperties(
    val path: String = "classpath:data/knowledge.csv"
)

data class VectorStoreProperties(
    val topK: Int = 5,
    val similarityThreshold: Double = 0.7
)
