package com.example.agent.rag

import org.slf4j.LoggerFactory
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import jakarta.annotation.PostConstruct


@Configuration
@EnableConfigurationProperties(RagProperties::class)
class RagConfig(
    private val ragProperties: RagProperties,
    private val vectorStore: VectorStore
) {
    private val logger = LoggerFactory.getLogger(RagConfig::class.java)

    @PostConstruct
    fun logConfiguration() {
        logger.info("RAG Configuration initialized:")
        logger.info("  CSV Path: ${ragProperties.csv.path}")
        logger.info("  Top-K: ${ragProperties.vectorstore.topK}")
        logger.info("  Similarity Threshold: ${ragProperties.vectorstore.similarityThreshold}")
        logger.info("  VectorStore: ${vectorStore.javaClass.simpleName}")
    }
}
