# AI Chat Assistant

A Spring Boot + Kotlin chat application with AI-powered responses using Anthropic Claude and in-memory conversation history.

## Overview

This project provides a chat interface that communicates with Claude via Spring AI. Conversations are stored in-memory, allowing the AI to maintain context across messages within a session.

## Features

- Chat interface with Claude AI
- In-memory conversation history for context retention
- MCP (Model Context Protocol) server support for tool integration
- React frontend with real-time messaging

## Quick Start

```bash
# 1. Clone and enter the project
cd harry-ai-agent

# 2. Copy environment template
cp .env.example .env

# 3. Add your Anthropic API key
# Edit .env and set ANTHROPIC_API_KEY=sk-ant-xxxxx

# 4. Start all services
docker compose up -d

# 5. Open the app
open http://localhost:5173
```

## Project Structure

See [ARCHITECTURE.md](./ARCHITECTURE.md) for detailed documentation.

| Directory | Description |
|-----------|-------------|
| `backend/` | Spring Boot + Kotlin API server |
| `frontend/` | React + TypeScript chat UI |
| `shopping-mcp/` | MCP server for tool integration |

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.5.3, Kotlin 2.1.10
- **AI SDK**: Spring AI Anthropic 1.1.2
- **Chat Memory**: In-Memory (ConcurrentHashMap)
- **Frontend**: React 18, Vite 6, TypeScript
- **Build**: Gradle 9.2, Node 24
- **Infrastructure**: Docker Compose

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
| `AgentService.kt` | AI integration with Claude |
| `AnthropicConfig.kt` | Spring AI configuration |
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
