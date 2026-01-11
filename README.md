# AI Shopping Assistant

A learning project for building an AI-powered shopping assistant using Spring Boot, Kotlin, and React.

## Overview

This project provides boilerplate code for a chatbot application that manages a shopping list. The AI agent integration is stubbed out with instructional comments (`TODO: AI`) to guide you through implementing your own agent logic using the Anthropic Claude SDK.

## Quick Start

```bash
# 1. Clone and enter the project
cd harry-ai-agent

# 2. Copy environment template
cp .env.example .env

# 3. (Optional) Add your Anthropic API key
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
| `specs/` | Feature specifications |

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.5.3, Kotlin 2.1.10
- **AI SDK**: Anthropic Java SDK 2.11.1
- **Frontend**: React 18, Vite 6, TypeScript
- **Database**: PostgreSQL 16
- **Build**: Gradle 9.2, Node 24
- **Infrastructure**: Docker Compose

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/health | Health check |
| POST | /api/chat | Send message to AI |
| GET | /api/chat/{id}/history | Get chat history |
| GET | /api/items | List shopping items |
| POST | /api/items | Add item |
| PUT | /api/items/{id} | Update item |
| DELETE | /api/items/{id} | Remove item |

## Implementing the AI Agent

Search for `TODO: AI` comments in the codebase to find integration points:

```bash
grep -r "TODO: AI" backend/
```

Key files:
- `AgentService.kt` - Main AI logic (implement here!)
- `AnthropicConfig.kt` - SDK configuration
- `ChatController.kt` - Request handling

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
