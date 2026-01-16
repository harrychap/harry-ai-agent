# Tasks: Spring AI RAG with CSV Data Source

**Feature**: 002-spring-ai-csv-rag
**Branch**: `002-spring-ai-csv-rag`
**Generated**: 2026-01-15
**Updated**: 2026-01-16 - Moved to backend service

---

## Final Architecture

RAG logic is now in the **backend** service (the AI orchestrator), which is the correct architectural location.

```
harry-ai-agent/
├── backend/             # AI orchestrator with RAG ✅
│   └── src/main/kotlin/com/example/agent/
│       ├── rag/         # RAG components
│       │   ├── RagProperties.kt
│       │   ├── RagConfig.kt
│       │   ├── CsvDocumentLoader.kt
│       │   ├── VectorStoreInitializer.kt
│       │   └── RagService.kt
│       ├── service/     # Chat services
│       └── controller/  # REST endpoints
│
├── shopping-mcp/        # MCP server (tools only, unchanged)
│
└── frontend/            # React UI
```

---

## Completed Tasks

### Phase 1: Setup - ✅ Complete

- [x] T001 Add Spring AI PGVector starter dependency
- [x] T002 Configure PGVector properties in application.yml
- [x] T003 Add RAG configuration properties

### Phase 2: RAG Components - ✅ Complete

- [x] T004 Create `RagProperties` configuration class
- [x] T005 Create `RagConfig` configuration class
- [x] T006 Create `CsvDocumentLoader` service
- [x] T007 Create `VectorStoreInitializer` component
- [x] T008 Create `RagService` for search and context retrieval

### Phase 3: Sample Data - ✅ Complete

- [x] T009 Create sample knowledge.csv file

### Phase 4: Architecture Refactoring - ✅ Complete

- [x] T010 Move RAG from shopping-mcp to separate module (rag-orchestrator)
- [x] T011 Move RAG from rag-orchestrator to backend service
- [x] T012 Remove rag-orchestrator module
- [x] T013 Verify backend compiles

---

## File Locations

### Backend RAG Files

```
backend/src/main/kotlin/com/example/agent/rag/
├── RagProperties.kt          # Configuration properties
├── RagConfig.kt              # Spring configuration
├── CsvDocumentLoader.kt      # CSV parsing → Documents
├── VectorStoreInitializer.kt # Startup indexing
└── RagService.kt             # Search & context retrieval
```

### Backend Resources

```
backend/src/main/resources/
├── application.yml           # RAG + PGVector config
└── data/
    └── knowledge.csv         # Sample knowledge base
```

---

## Configuration Summary

### Dependencies (build.gradle.kts)

```kotlin
implementation("org.springframework.ai:spring-ai-starter-vector-store-pgvector")
runtimeOnly("org.postgresql:postgresql")
```

### Application Properties

```yaml
spring.ai.vectorstore.pgvector:
  dimensions: 768
  initialize-schema: true

rag:
  csv.path: classpath:data/knowledge.csv
  vectorstore.top-k: 5
  vectorstore.similarity-threshold: 0.7
```

---

## Next Steps

1. **Use RagService** in your chat service to augment prompts:
   ```kotlin
   val context = ragService.getContext(userQuery)
   val augmentedPrompt = "$context\n\nUser question: $userQuery"
   ```

2. **Customize** the TODO extension points for your CSV schema

3. **Add PostgreSQL with PGVector** to your docker-compose.yml if not already present
