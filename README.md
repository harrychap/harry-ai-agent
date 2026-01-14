# AI Chat Assistant

A Spring Boot + Kotlin chat application with AI-powered responses using Ollama (local LLM) and in-memory conversation history.

## Overview

This project provides a chat interface that communicates with a local LLM via Spring AI and Ollama. Conversations are stored in-memory, allowing the AI to maintain context across messages within a session.

## Features

- Chat interface with local LLM (Ollama/Llama 3.2)
- In-memory conversation history for context retention
- MCP (Model Context Protocol) server support for tool integration
- React frontend with real-time messaging
- No API key required - runs fully locally

## Quick Start

```bash
# 1. Clone and enter the project
cd harry-ai-agent

# 2. Start all services (includes Ollama with llama3.2)
docker compose up -d

# 3. Wait for model download (~2GB, first run only)
docker compose logs -f ollama

# 4. Open the app
open http://localhost:5173
```

> **Note**: First startup takes longer as Ollama downloads the llama3.2 model (~2GB).

## Project Structure

See [ARCHITECTURE.md](./ARCHITECTURE.md) for detailed documentation.

| Directory | Description |
|-----------|-------------|
| `backend/` | Spring Boot + Kotlin API server |
| `frontend/` | React + TypeScript chat UI |
| `shopping-mcp/` | MCP server for tool integration |

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.5.3, Kotlin 2.1.10
- **LLM**: Ollama with Llama 3.2 (local) - Spring AI Ollama 1.1.2
- **Chat Memory**: In-Memory (ConcurrentHashMap)
- **Frontend**: React 18, Vite 6, TypeScript
- **Build**: Gradle 9.2, Node 24
- **Infrastructure**: Docker Compose

## Using Anthropic Claude (Alternative)

To use Anthropic Claude instead of Ollama:

1. **Update backend dependencies** (`backend/build.gradle.kts`):
   ```kotlin
   // Comment out Ollama
   // implementation("org.springframework.ai:spring-ai-starter-model-ollama:1.1.2")

   // Uncomment Anthropic
   implementation("org.springframework.ai:spring-ai-starter-model-anthropic:1.1.2")
   ```

2. **Set your API key**:
   ```bash
   cp .env.example .env
   # Edit .env and set ANTHROPIC_API_KEY=sk-ant-xxxxx
   ```

3. **Rebuild and restart**:
   ```bash
   docker compose build backend && docker compose up -d backend
   ```

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/health | Health check |
| POST | /api/chat | Send message to AI |

## Chat Memory

The application uses `InMemoryChatMemoryRepository` to store conversation history:

- Messages persist for the lifetime of the application
- Thread-safe using ConcurrentHashMap
- Supports multiple concurrent conversations
- No external database required

**Note**: Conversation history is lost on application restart. For persistent storage, implement a database-backed `ChatMemoryRepository`.

## Key Files

| File | Description |
|------|-------------|
| `AgentService.kt` | AI agent orchestration |
| `ShoppingChatClient.kt` | Chat client with MCP tools |
| `InMemoryChatMemoryRepository.kt` | Conversation storage |
| `ChatService.kt` | Request handling |

## Development

```bash
# Rebuild backend after changes
docker compose build backend && docker compose up -d backend

# View logs
docker compose logs -f backend

# Stop all services
docker compose down
```

## License

MIT
