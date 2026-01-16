# RAG Service Contract

**Feature**: 002-spring-ai-csv-rag
**Date**: 2026-01-15

## Overview

This contract defines the internal service interfaces for the RAG system. Since this integrates with the existing MCP server pattern (not exposing standalone REST APIs), these are Kotlin service interfaces.

---

## Service Interfaces

### 1. CsvDocumentLoader

Responsible for loading and parsing CSV files into Spring AI Documents.

```kotlin
interface CsvDocumentLoader {
    /**
     * Load documents from the configured CSV resource.
     * @return List of Document objects ready for embedding
     */
    fun loadDocuments(): List<Document>

    /**
     * Reload documents (e.g., after CSV update).
     * Clears existing documents and reloads from source.
     */
    fun reloadDocuments()
}
```

**Behavior**:
- Called on application startup via `@PostConstruct` or `CommandLineRunner`
- Parses CSV headers from first row
- Creates one `Document` per CSV row (or grouped rows)
- Populates metadata with source, row number, timestamp

---

### 2. RagService

Orchestrates retrieval and context augmentation.

```kotlin
interface RagService {
    /**
     * Search for documents relevant to the query.
     * @param query User's search query
     * @param topK Maximum number of results (default from config)
     * @return List of relevant documents with similarity scores
     */
    fun search(query: String, topK: Int? = null): List<Document>

    /**
     * Get augmented context for a query.
     * @param query User's question
     * @return Formatted context string for prompt injection
     */
    fun getContext(query: String): String
}
```

**Behavior**:
- Delegates to `VectorStore.similaritySearch()`
- Applies similarity threshold from configuration
- Formats retrieved documents into context string

---

### 3. VectorStoreInitializer

Handles vector store population on startup.

```kotlin
interface VectorStoreInitializer {
    /**
     * Initialize the vector store with documents from CSV.
     * Should be idempotent - safe to call multiple times.
     */
    fun initialize()

    /**
     * Check if vector store is already populated.
     * @return true if documents exist in store
     */
    fun isInitialized(): Boolean

    /**
     * Clear all documents from the vector store.
     */
    fun clear()
}
```

**Behavior**:
- Checks if store already has data (skip re-indexing if populated)
- Batches document additions for large CSV files
- Logs progress during initialization

---

## Configuration Properties Contract

```yaml
# application.yml structure
rag:
  csv:
    path: classpath:data/knowledge.csv  # Resource path to CSV
    # TODO: Add delimiter, encoding options if needed

  vectorstore:
    top-k: 5                    # Default number of results
    similarity-threshold: 0.7   # Minimum similarity score

  embedding:
    # TODO: Configure embedding model specifics
```

**Kotlin Configuration Class**:

```kotlin
@ConfigurationProperties(prefix = "rag")
data class RagProperties(
    val csv: CsvProperties = CsvProperties(),
    val vectorstore: VectorStoreProperties = VectorStoreProperties()
)

data class CsvProperties(
    val path: String = "classpath:data/knowledge.csv"
    // TODO: Add additional CSV parsing options
)

data class VectorStoreProperties(
    val topK: Int = 5,
    val similarityThreshold: Double = 0.7
)
```

---

## Integration with MCP Tools

The RAG service integrates with the existing MCP tool pattern. Example tool method:

```kotlin
@Tool(description = "Search knowledge base for relevant information")
fun searchKnowledge(
    @ToolParam(description = "The search query") query: String
): String {
    // TODO: Implement using RagService
    val context = ragService.getContext(query)
    return context.ifEmpty { "No relevant information found." }
}
```

---

## Error Handling Contract

| Scenario | Expected Behavior |
|----------|-------------------|
| CSV file not found | Log warning, start without RAG capability |
| CSV parse error | Log error with row number, skip invalid rows |
| Empty CSV | Log warning, vector store remains empty |
| Embedding API failure | Throw exception, prevent partial indexing |
| Vector store connection failure | Throw exception on startup |

---

## Extension Points Summary

| Component | Extension Point | Purpose |
|-----------|-----------------|---------|
| CsvDocumentLoader | `formatRowAsContent()` | Customize how CSV rows become searchable text |
| CsvDocumentLoader | `extractMetadata()` | Add domain-specific metadata from CSV fields |
| RagService | `formatContext()` | Customize how retrieved docs become prompt context |
| VectorStoreInitializer | `shouldReindex()` | Add logic for when to refresh the index |
