# Implementation Plan: Spring AI RAG with CSV Data Source

**Feature**: 002-spring-ai-csv-rag
**Branch**: `002-spring-ai-csv-rag`
**Date**: 2026-01-15

## Technical Context

| Aspect | Details |
|--------|---------|
| Language | Kotlin 2.1.10 |
| Framework | Spring Boot 3.5.3, Spring AI 1.1.x |
| Database | PostgreSQL with PGVector extension |
| Existing Integration | Spring AI MCP Server (`spring-ai-starter-mcp-server-webmvc:1.1.2`) |
| Embedding Model | Ollama (local) or OpenAI (configurable) |
| Build Tool | Gradle Kotlin DSL |

---

## Implementation Phases

### Phase 1: Dependencies and Configuration

**Files to modify**:
- `build.gradle.kts`
- `src/main/resources/application.yml`

**Tasks**:
1. Add Spring AI PGVector starter dependency
2. Add embedding model starter (Ollama by default)
3. Configure PGVector properties (dimensions, index type, schema init)
4. Add RAG configuration properties section
5. Create sample CSV file location

---

### Phase 2: Configuration Classes

**Files to create**:
- `src/main/kotlin/com/example/mcp/config/RagConfig.kt`
- `src/main/kotlin/com/example/mcp/config/RagProperties.kt`

**Tasks**:
1. Create `RagProperties` data class for configuration binding
2. Create `RagConfig` class with `@Configuration`
3. Configure `VectorStore` bean (if not auto-configured)
4. Add `@ConfigurationPropertiesScan` to enable properties

---

### Phase 3: CSV Document Loader

**Files to create**:
- `src/main/kotlin/com/example/mcp/rag/CsvDocumentLoader.kt`

**Tasks**:
1. Create `CsvDocumentLoader` class with `@Service`
2. Inject `Resource` for CSV file
3. Implement CSV parsing with headers
4. Create `Document` objects from rows
5. Add TODO comments for:
   - Custom content formatting
   - Custom metadata extraction
   - Row grouping logic

---

### Phase 4: RAG Service

**Files to create**:
- `src/main/kotlin/com/example/mcp/rag/RagService.kt`

**Tasks**:
1. Create `RagService` class with `@Service`
2. Inject `VectorStore` and `RagProperties`
3. Implement `search()` method using `similaritySearch`
4. Implement `getContext()` method for prompt augmentation
5. Add TODO comments for:
   - Custom context formatting
   - Result filtering logic

---

### Phase 5: Initialization

**Files to create**:
- `src/main/kotlin/com/example/mcp/rag/VectorStoreInitializer.kt`

**Tasks**:
1. Create initializer with `@Component`
2. Implement `@PostConstruct` or `CommandLineRunner`
3. Load documents from `CsvDocumentLoader`
4. Add documents to `VectorStore`
5. Add logging for progress
6. Add TODO comment for:
   - Reindex trigger logic
   - Idempotency check

---

### Phase 6: Sample Data

**Files to create**:
- `src/main/resources/data/knowledge.csv`

**Tasks**:
1. Create sample CSV with headers
2. Add 3-5 example rows
3. Include comment in file about schema customization

---

## File Structure (After Implementation)

```
src/main/kotlin/com/example/mcp/
├── Application.kt
├── config/
│   ├── McpServerConfig.kt (existing)
│   ├── RagConfig.kt (new)
│   └── RagProperties.kt (new)
├── rag/
│   ├── CsvDocumentLoader.kt (new)
│   ├── RagService.kt (new)
│   └── VectorStoreInitializer.kt (new)
├── model/ (existing)
├── repository/ (existing)
├── service/ (existing)
└── tools/ (existing)

src/main/resources/
├── application.yml (modified)
├── data/
│   └── knowledge.csv (new)
└── ... (existing)
```

---

## TODO Comments Template

Each boilerplate file will include clearly marked extension points:

```kotlin
// TODO: [EXTENSION POINT] - Description of what to customize
// Example:
//   - Option A: ...
//   - Option B: ...
// Your implementation here
```

---

## Verification Checklist

After implementation, verify:

- [ ] Application starts without errors
- [ ] PGVector table `vector_store` is created
- [ ] CSV file is parsed (check logs)
- [ ] Documents are indexed (check `SELECT COUNT(*) FROM vector_store`)
- [ ] All TODO comments are specific and actionable
- [ ] No hardcoded values (all in configuration)

---

## Dependencies Summary

```kotlin
// build.gradle.kts additions
dependencies {
    // Spring AI PGVector
    implementation("org.springframework.ai:spring-ai-starter-vector-store-pgvector")

    // Embedding Model (choose one)
    implementation("org.springframework.ai:spring-ai-starter-model-ollama")
    // OR: implementation("org.springframework.ai:spring-ai-starter-model-openai")
}
```

---

## Related Artifacts

- [spec.md](./spec.md) - Feature specification
- [research.md](./research.md) - Technical research and decisions
- [data-model.md](./data-model.md) - Entity and data model design
- [contracts/rag-service-contract.md](./contracts/rag-service-contract.md) - Service interfaces
- [quickstart.md](./quickstart.md) - Setup and verification guide
