# Architecture Guide

This document provides a comprehensive overview of the AI Shopping Assistant architecture.

## Project Structure

```
harry-ai-agent/
├── backend/                     # Spring Boot + Kotlin backend
│   ├── src/main/kotlin/com/example/agent/
│   │   ├── Application.kt       # Main entry point
│   │   ├── config/              # Configuration classes
│   │   │   ├── AnthropicConfig.kt    # Claude SDK setup
│   │   │   └── GlobalExceptionHandler.kt
│   │   ├── controller/          # REST endpoints
│   │   │   ├── ChatController.kt     # POST /api/chat
│   │   │   ├── HealthController.kt   # GET /api/health
│   │   │   └── ShoppingItemController.kt  # CRUD /api/items
│   │   ├── dto/                 # Request/Response objects
│   │   │   ├── ChatDtos.kt
│   │   │   ├── HealthDtos.kt
│   │   │   └── ShoppingItemDtos.kt
│   │   ├── model/               # JPA entities
│   │   │   ├── ChatMessage.kt
│   │   │   ├── ChatSession.kt
│   │   │   └── ShoppingItem.kt
│   │   ├── repository/          # Spring Data repositories
│   │   │   ├── ChatMessageRepository.kt
│   │   │   ├── ChatSessionRepository.kt
│   │   │   └── ShoppingItemRepository.kt
│   │   └── service/             # Business logic
│   │       ├── AgentService.kt       # TODO: AI - Main integration point
│   │       ├── ChatService.kt        # Session & message management
│   │       └── ShoppingItemService.kt
│   ├── src/main/resources/
│   │   ├── application.yml      # App configuration
│   │   ├── logback-spring.xml   # Logging config
│   │   └── schema.sql           # Database schema
│   ├── build.gradle.kts         # Gradle build file
│   └── Dockerfile
│
├── frontend/                    # React + TypeScript frontend
│   ├── src/
│   │   ├── components/          # React components
│   │   │   ├── ChatWindow.tsx
│   │   │   ├── MessageBubble.tsx
│   │   │   └── MessageInput.tsx
│   │   ├── hooks/               # Custom React hooks
│   │   │   └── useChat.ts       # Chat state management
│   │   ├── services/            # API clients
│   │   │   └── api.ts           # Backend communication
│   │   ├── types/               # TypeScript definitions
│   │   │   └── api.ts           # API types matching DTOs
│   │   ├── App.tsx              # Main app component
│   │   ├── App.css              # Styles
│   │   └── main.tsx             # Entry point
│   ├── index.html
│   ├── package.json
│   ├── tsconfig.json
│   ├── vite.config.ts
│   └── Dockerfile
│
├── docker-compose.yml           # Orchestration
├── .env.example                 # Environment template
└── specs/                       # Feature specifications
```

## Technology Stack

| Layer | Technology | Version |
|-------|------------|---------|
| Backend Runtime | Java | 25 |
| Backend Framework | Spring Boot | 3.5.3 |
| Backend Language | Kotlin | 2.1.10 |
| AI SDK | Anthropic Java | 2.11.1 |
| Database | PostgreSQL | 16 |
| Frontend Runtime | Node.js | 24 |
| Frontend Framework | React | 18 |
| Build Tool (Frontend) | Vite | 6 |
| Build Tool (Backend) | Gradle | 9.2 |
| Container | Docker Compose | - |

## Data Flow

### Chat Message Flow

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Browser   │────>│  Frontend   │────>│   Backend   │────>│  Database   │
│             │<────│   (React)   │<────│  (Spring)   │<────│ (PostgreSQL)│
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘
                                               │
                                               v
                                        ┌─────────────┐
                                        │  Anthropic  │
                                        │     API     │
                                        └─────────────┘
```

1. User types message in MessageInput component
2. useChat hook sends POST /api/chat
3. ChatController receives request
4. ChatService creates/retrieves session
5. ChatService saves user message
6. AgentService calls Anthropic API (TODO: AI)
7. ChatService saves assistant response
8. Response returns through stack
9. ChatWindow displays new messages

### Shopping Item Flow

```
Frontend                  Backend                    Database
   │                         │                          │
   │  POST /api/items        │                          │
   │────────────────────────>│                          │
   │                         │  INSERT/UPDATE           │
   │                         │─────────────────────────>│
   │                         │                          │
   │  ShoppingItemResponse   │                          │
   │<────────────────────────│                          │
```

## Database Schema

### Tables

**shopping_items**
- `id` (UUID, PK) - Unique identifier
- `name` (VARCHAR 255, UNIQUE) - Item name
- `quantity` (INT) - How many needed
- `created_at` (TIMESTAMP) - When added
- `updated_at` (TIMESTAMP) - Last modified

**chat_sessions**
- `id` (UUID, PK) - Session identifier
- `created_at` (TIMESTAMP) - Session start
- `updated_at` (TIMESTAMP) - Last activity

**chat_messages**
- `id` (UUID, PK) - Message identifier
- `session_id` (UUID, FK) - Parent session
- `role` (VARCHAR 20) - 'user' or 'assistant'
- `content` (TEXT) - Message text
- `created_at` (TIMESTAMP) - When sent

## API Endpoints

### Chat

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/chat | Send message to agent |
| GET | /api/chat/{sessionId}/history | Get session messages |

### Shopping Items

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/items | List all items |
| POST | /api/items | Add item (merges duplicates) |
| GET | /api/items/{id} | Get single item |
| PUT | /api/items/{id} | Update quantity |
| DELETE | /api/items/{id} | Remove item |
| DELETE | /api/items | Clear all items |

### Health

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/health | Service health check |

## Key Integration Points

### Where to Implement AI Logic

Look for `TODO: AI` comments in these files:

1. **AgentService.kt** (Primary)
   - `processMessage()` - Main entry point for AI logic
   - Inject `ShoppingItemService` to access shopping list
   - Use Anthropic SDK to call Claude

2. **AnthropicConfig.kt**
   - SDK configuration and examples
   - Model selection and token limits

### Recommended Implementation Steps

1. Uncomment `ShoppingItemService` injection in `AgentService`
2. Define a system prompt for the shopping assistant
3. Implement tool definitions for shopping operations
4. Parse user messages and execute appropriate actions
5. Format responses in a friendly way

## Docker Services

| Service | Port | Description |
|---------|------|-------------|
| db | 5432 | PostgreSQL database |
| backend | 8080 | Spring Boot API |
| frontend | 5173 | Vite dev server / Nginx |

### Starting the Stack

```bash
# Copy environment template
cp .env.example .env

# Add your API key (optional for development)
# Edit .env and set ANTHROPIC_API_KEY=sk-ant-xxxxx

# Start all services
docker compose up -d

# View logs
docker compose logs -f

# Stop services
docker compose down
```

## Configuration

### Environment Variables

| Variable | Required | Description |
|----------|----------|-------------|
| ANTHROPIC_API_KEY | No | Claude API key (placeholder mode if missing) |
| POSTGRES_USER | Yes | Database username (default: agent) |
| POSTGRES_PASSWORD | Yes | Database password (default: agent) |
| POSTGRES_DB | Yes | Database name (default: agent_db) |

### Application Properties

Located in `backend/src/main/resources/application.yml`:

- Server port: 8080
- Database connection pooling
- Anthropic model configuration
- Logging levels

## Development Workflow

1. Make changes to backend code
2. Rebuild: `docker compose build backend`
3. Restart: `docker compose up -d backend`
4. Check logs: `docker compose logs -f backend`

For frontend changes, Vite provides hot module replacement.

## Extending the Application

### Adding New Tools for the Agent

1. Define tool schema in `AgentService.kt`
2. Create corresponding service method
3. Handle tool_use responses from Claude
4. Execute the tool and return results

### Adding New API Endpoints

1. Create DTO in `dto/` package
2. Create controller in `controller/` package
3. Create service in `service/` package
4. Update TypeScript types in `frontend/src/types/api.ts`
5. Add API client method in `frontend/src/services/api.ts`

### Adding New React Components

1. Create component in `frontend/src/components/`
2. Add styles to `App.css` or create component-specific CSS
3. Import and use in parent component
