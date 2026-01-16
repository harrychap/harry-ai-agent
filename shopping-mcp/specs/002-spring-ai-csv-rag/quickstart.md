# Quickstart: Spring AI RAG with CSV Data Source

**Feature**: 002-spring-ai-csv-rag
**Date**: 2026-01-15

## Prerequisites

1. **PostgreSQL with PGVector extension**
   ```bash
   # Using Docker (recommended)
   docker run -d --name pgvector \
     -p 5432:5432 \
     -e POSTGRES_USER=postgres \
     -e POSTGRES_PASSWORD=postgres \
     -e POSTGRES_DB=shopping_list \
     pgvector/pgvector:latest
   ```

2. **Embedding Model** (choose one):
   - **Ollama** (local): `ollama pull nomic-embed-text`
   - **OpenAI**: Set `OPENAI_API_KEY` environment variable

---

## Setup Steps

### 1. Add Dependencies

Add to `build.gradle.kts`:

```kotlin
dependencies {
    // Existing dependencies...

    // Spring AI PGVector
    implementation("org.springframework.ai:spring-ai-starter-vector-store-pgvector")

    // Choose ONE embedding model:
    // Option A: Ollama (local)
    implementation("org.springframework.ai:spring-ai-starter-model-ollama")
    // Option B: OpenAI (cloud)
    // implementation("org.springframework.ai:spring-ai-starter-model-openai")
}
```

### 2. Enable PGVector Extension

Connect to PostgreSQL and run:

```sql
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
```

### 3. Add Configuration

Add to `application.yml`:

```yaml
spring:
  ai:
    vectorstore:
      pgvector:
        index-type: HNSW
        distance-type: COSINE_DISTANCE
        dimensions: 768  # nomic-embed-text; use 1536 for OpenAI
        initialize-schema: true

    # For Ollama (local embedding)
    ollama:
      base-url: http://localhost:11434
      embedding:
        model: nomic-embed-text

    # For OpenAI (uncomment if using)
    # openai:
    #   api-key: ${OPENAI_API_KEY}
    #   embedding:
    #     model: text-embedding-ada-002

# RAG Configuration
rag:
  csv:
    path: classpath:data/knowledge.csv
  vectorstore:
    top-k: 5
    similarity-threshold: 0.7
```

### 4. Add Your CSV File

Create `src/main/resources/data/knowledge.csv`:

```csv
id,name,description,category
1,Example Item,This is a description that will be searchable,category1
2,Another Item,More searchable content goes here,category2
```

### 5. Run the Application

```bash
./gradlew bootRun
```

---

## Verification

### Check Vector Store Table

```sql
SELECT COUNT(*) FROM vector_store;
-- Should show number of rows from your CSV
```

### Check Logs

Look for startup messages:
```
INFO  - Loading CSV from: classpath:data/knowledge.csv
INFO  - Parsed X rows from CSV
INFO  - Created X documents
INFO  - Indexed X documents in vector store
```

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| `relation "vector_store" does not exist` | Ensure `initialize-schema: true` in config |
| `could not find extension "vector"` | Run `CREATE EXTENSION vector;` in PostgreSQL |
| `Embedding model not available` | Check Ollama is running (`ollama serve`) or OpenAI key is set |
| `CSV file not found` | Verify path in `rag.csv.path` matches actual file location |
| `Wrong vector dimensions` | Match `dimensions` config to your embedding model output |

---

## Next Steps

After boilerplate is running:

1. **Customize CSV parsing** in `CsvDocumentLoader`
   - Edit `formatRowAsContent()` for your schema
   - Add metadata from specific columns

2. **Integrate with MCP tools** in `ShoppingTools.kt`
   - Add tool method that uses `RagService`

3. **Tune retrieval** in configuration
   - Adjust `top-k` and `similarity-threshold`
   - Experiment with different chunking strategies
