# Research: Spring AI RAG with CSV Data Source

**Feature**: 002-spring-ai-csv-rag
**Date**: 2026-01-15

## Decision 1: Vector Store Implementation

**Decision**: PGVector with Spring AI starter

**Rationale**:
- Project already uses PostgreSQL (`org.postgresql:postgresql` runtime dependency)
- Spring AI provides `spring-ai-starter-vector-store-pgvector` with auto-configuration
- HNSW index type provides good balance of speed and accuracy
- PGVector extension handles schema initialization automatically

**Alternatives Considered**:
- SimpleVectorStore (in-memory): No persistence, not suitable for production
- Chroma: Requires separate container/service
- Redis: Would add new infrastructure dependency

**Implementation**:
```gradle
implementation("org.springframework.ai:spring-ai-starter-vector-store-pgvector")
```

**Configuration**:
```yaml
spring:
  ai:
    vectorstore:
      pgvector:
        index-type: HNSW
        distance-type: COSINE_DISTANCE
        dimensions: 1536  # TODO: Match your embedding model dimensions
        initialize-schema: true
```

---

## Decision 2: CSV Document Loading Strategy

**Decision**: Custom CSV loader creating Spring AI `Document` objects

**Rationale**:
- Spring AI does not provide a built-in CSV DocumentReader
- Available readers: JSON, Text, HTML, Markdown, PDF, Tika (multi-format)
- Tika supports CSV but with limited control over parsing
- Custom loader provides flexibility for header mapping and metadata extraction

**Alternatives Considered**:
- TikaDocumentReader: Generic, less control over CSV structure
- TextReader: Would load entire file as single document, not ideal for row-based data
- JsonReader: Would require CSV-to-JSON conversion first

**Implementation Pattern**:
```kotlin
// Create Document for each CSV row or group of rows
val documents = csvRecords.map { record ->
    Document(
        content = formatRecordAsText(record),  // TODO: Implement formatting
        metadata = mapOf(
            "source" to csvFileName,
            "row" to record.rowNumber
            // TODO: Add custom metadata
        )
    )
}
```

---

## Decision 3: Embedding Model Configuration

**Decision**: Configurable embedding model (Ollama local or OpenAI API)

**Rationale**:
- Project already references Ollama in assumptions
- OpenAI provides reliable embeddings with `text-embedding-ada-002` (1536 dimensions)
- Ollama supports local models like `nomic-embed-text` (768 dimensions)
- Spring AI supports both via starters

**Alternatives Considered**:
- Hardcoded single model: Less flexible
- Azure OpenAI: Additional cloud dependency

**Implementation Options**:
```gradle
// Option A: Ollama (local)
implementation("org.springframework.ai:spring-ai-starter-model-ollama")

// Option B: OpenAI (API)
implementation("org.springframework.ai:spring-ai-starter-model-openai")
```

---

## Decision 4: Document Chunking Strategy

**Decision**: Row-based chunking with optional grouping

**Rationale**:
- CSV data is naturally structured by rows
- Each row represents a discrete unit of information
- Keeps chunks small and focused for better retrieval precision
- TokenTextSplitter available for additional splitting if rows are large

**Alternatives Considered**:
- TokenTextSplitter on full file: Loses row context boundaries
- Fixed character chunking: May split mid-field

**Implementation Pattern**:
```kotlin
// Basic: One document per row
val documents = csvRecords.map { createDocumentFromRow(it) }

// Optional: Group related rows
val groupedDocuments = csvRecords
    .groupBy { it.getField("category") }  // TODO: Define grouping key
    .map { createDocumentFromGroup(it) }
```

---

## Decision 5: RAG Query Pattern

**Decision**: Use Spring AI Advisor pattern for prompt augmentation

**Rationale**:
- Spring AI provides `QuestionAnswerAdvisor` for RAG patterns
- Integrates cleanly with `ChatClient`
- Handles context injection automatically

**Implementation Pattern**:
```kotlin
val response = chatClient.prompt()
    .user(userQuery)
    .advisors(QuestionAnswerAdvisor(vectorStore, searchRequest))
    .call()
    .content()
```

---

## Technical Dependencies Summary

| Dependency | Purpose | Version |
|------------|---------|---------|
| `spring-ai-starter-vector-store-pgvector` | PGVector integration | 1.1.x |
| `spring-ai-starter-model-ollama` OR `spring-ai-starter-model-openai` | Embedding model | 1.1.x |
| PostgreSQL with PGVector extension | Vector storage | pgvector/pgvector:latest |

---

## Database Requirements

PostgreSQL must have these extensions enabled:
```sql
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
```

Spring AI will auto-create the vector_store table with `initialize-schema: true`.

---

## Sources

- [Spring AI PGVector Documentation](https://docs.spring.io/spring-ai/reference/api/vectordbs/pgvector.html)
- [Spring AI ETL Pipeline](https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html)
- [Spring AI Vector Databases Overview](https://docs.spring.io/spring-ai/reference/api/vectordbs.html)
- [Baeldung: Spring AI PGVector Semantic Search](https://www.baeldung.com/spring-ai-pgvector-semantic-search)
