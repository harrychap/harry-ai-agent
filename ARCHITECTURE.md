# Architecture Guide

This document provides a comprehensive overview of the AI Chat Assistant architecture.

## Project Structure

```
harry-ai-agent/
├── backend/                     # Spring Boot + Kotlin backend
│   ├── src/main/kotlin/com/example/agent/
│   │   ├── Application.kt       # Main entry point
│   │   ├── ai/                  # AI integration
│   │   │   └── ShoppingChatClient.kt  # Chat client with MCP tools
│   │   ├── config/              # Configuration classes
│   │   │   ├── GlobalExceptionHandler.kt    # Error handling
│   │   │   └── InMemoryChatMemoryRepository.kt  # Chat history storage
│   │   ├── controller/          # REST endpoints
│   │   │   ├── ChatController.kt     # POST /api/chat
│   │   │   └── HealthController.kt   # GET /api/health
│   │   ├── dto/                 # Request/Response objects
│   │   │   ├── ChatDtos.kt
│   │   │   └── HealthDtos.kt
│   │   └── service/             # Business logic
│   │       ├── AgentService.kt       # AI agent orchestration
│   │       └── ChatService.kt        # Message handling
│   ├── src/main/resources/
│   │   ├── application.yml      # App configuration (Ollama, MCP)
│   │   └── logback-spring.xml   # Logging config
│   ├── build.gradle.kts         # Gradle build file
│   └── Dockerfile
│
├── shopping-mcp/                # MCP Server for shopping tools
│   ├── src/main/kotlin/com/example/mcp/
│   │   ├── Application.kt       # Main entry point
│   │   ├── config/              # Configuration
│   │   │   └── McpServerConfig.kt    # Tool registration
│   │   ├── model/               # Data models
│   │   │   └── ShoppingItem.kt
│   │   ├── repository/          # Database access
│   │   │   └── ShoppingItemRepository.kt
│   │   ├── service/             # Business logic
│   │   │   └── ShoppingItemService.kt
│   │   └── tools/               # MCP tool definitions
│   │       └── ShoppingTools.kt      # @Tool annotated methods
│   ├── src/main/resources/
│   │   ├── application.yml      # DB config
│   │   └── schema.sql           # Database schema
│   ├── build.gradle.kts
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
├── docker-compose.yml           # Orchestration (backend, frontend, ollama, mcp, db)
├── .env.example                 # Environment template
└── specs/                       # Feature specifications
```

## Technology Stack

| Layer | Technology | Version |
|-------|------------|---------|
| Backend Runtime | Java | 21 |
| Backend Framework | Spring Boot | 3.5.3 |
| Backend Language | Kotlin | 2.1.10 |
| LLM | Ollama (Llama 3.2) | local |
| AI SDK | Spring AI Ollama | 1.1.2 |
| Chat Memory | In-Memory (ConcurrentHashMap) | - |
| Frontend Runtime | Node.js | 24 |
| Frontend Framework | React | 18 |
| Build Tool (Frontend) | Vite | 6 |
| Build Tool (Backend) | Gradle | 9.2 |
| Container | Docker Compose | - |

> **Note**: Anthropic Claude is also supported as an alternative. See README.md for switching instructions.

## Data Flow

### Chat Message Flow

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Browser   │────>│  Frontend   │────>│   Backend   │
│             │<────│   (React)   │<────│  (Spring)   │
└─────────────┘     └─────────────┘     └─────────────┘
                                               │
                           ┌───────────────────┼───────────────────┐
                           │                   │                   │
                           v                   v                   v
                    ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
                    │   Ollama    │     │ ChatMemory  │     │ Shopping    │
                    │ (Llama 3.2)│     │ Repository  │     │ MCP Server  │
                    └─────────────┘     └─────────────┘     └─────────────┘
                                                                  │
                                                                  v
                                                           ┌─────────────┐
                                                           │ PostgreSQL  │
                                                           │  Database   │
                                                           └─────────────┘
```

1. User types message in MessageInput component
2. useChat hook sends POST /api/chat
3. ChatController receives request
4. ChatService delegates to AgentService
5. ShoppingChatClient sends prompt to Ollama with MCP tools
6. Ollama decides whether to call MCP tools (e.g., listItems, addItem)
7. MCP tools execute against PostgreSQL via shopping-mcp service
8. Tool results are sent back to Ollama for final response
9. ChatMemoryRepository stores user and assistant messages
10. Response returns through stack
11. ChatWindow displays new messages

## Chat Memory System

### InMemoryChatMemoryRepository

The application uses an in-memory chat memory system to maintain conversation context:

```kotlin
class InMemoryChatMemoryRepository : ChatMemoryRepository {
    private val conversations = ConcurrentHashMap<String, MutableList<Message>>()

    // Find all active conversation IDs
    fun findConversationIds(): List<String>

    // Retrieve messages for a conversation
    fun findByConversationId(conversationId: String?): List<Message>

    // Save/update messages for a conversation
    fun saveAll(conversationId: String?, messages: List<Message?>?)

    // Delete a conversation's history
    fun deleteByConversationId(conversationId: String?)
}
```

**Key characteristics:**
- Thread-safe using `ConcurrentHashMap`
- Messages persist for the lifetime of the application
- No external database required
- Ideal for development and single-instance deployments

**Limitations:**
- Data is lost on application restart
- Not suitable for multi-instance deployments
- Memory usage grows with conversation history

## API Endpoints

### Chat

| Method | Path | Description |
|--------|------|-------------|
| POST | /api/chat | Send message to agent |

### Health

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/health | Service health check |

## Key Components

### AgentService

The main integration point for AI logic:
- Delegates chat processing to ShoppingChatClient
- Handles error logging and recovery

### ShoppingChatClient

Session-scoped chat client with MCP tool integration:
- Configures ChatClient with Ollama model
- Registers MCP tools from shopping-mcp server
- Manages conversation memory via MessageChatMemoryAdvisor
- System prompt defines assistant behavior

### Shopping MCP Server

Exposes shopping list tools via Model Context Protocol:
- `addItem` - Add item to shopping list
- `listItems` - List all shopping items
- `removeItem` - Remove item from list
- `updateItemQuantity` - Update item quantity
- `clearList` - Clear all items

### ChatService

Request/response handling:
- Creates DTOs for frontend communication
- Delegates AI processing to AgentService

## Docker Services

| Service | Port | Description |
|---------|------|-------------|
| backend | 8080 | Spring Boot API |
| frontend | 5173 | Vite dev server / Nginx |
| ollama | 11434 (internal) | Local LLM server |
| shopping-mcp | 8081 | MCP server with shopping tools |
| db | 5432 | PostgreSQL database |

### Starting the Stack

```bash
# Start all services (no API key required for Ollama)
docker compose up -d

# First run: wait for model download (~2GB)
docker compose logs -f ollama

# View all logs
docker compose logs -f

# Stop services
docker compose down
```

## Configuration

### Environment Variables

| Variable | Required | Description |
|----------|----------|-------------|
| ANTHROPIC_API_KEY | No* | Claude API key (only if using Anthropic) |

*Default setup uses Ollama which runs locally and requires no API key.

### Application Properties

Located in `backend/src/main/resources/application.yml`:

- Server port: 8080
- Ollama configuration (base-url, model, temperature)
- MCP client configuration (shopping-mcp connection)
- Logging levels

### MCP Server Configuration

Located in `shopping-mcp/src/main/resources/application.yml`:

- Server port: 8081
- PostgreSQL database connection
- JPA/Hibernate settings

## Development Workflow

1. Make changes to backend code
2. Rebuild: `docker compose build backend`
3. Restart: `docker compose up -d backend`
4. Check logs: `docker compose logs -f backend`

For frontend changes, Vite provides hot module replacement.

## Extending the Application

### Adding Persistent Chat Memory

To persist conversations across restarts, implement `ChatMemoryRepository` with a database:

1. Add database dependency (e.g., Spring Data JPA)
2. Create a `JpaChatMemoryRepository` implementation
3. Replace the `InMemoryChatMemoryRepository` bean

### Adding New MCP Tools

The shopping-mcp server demonstrates MCP tool integration:

1. Add method with `@Tool` annotation in `ShoppingTools.kt`
2. Use `@ToolParam` to document parameters
3. Rebuild: `docker compose build shopping-mcp && docker compose up -d`

Example:
```kotlin
@Tool(description = "Mark an item as purchased")
fun markPurchased(
    @ToolParam(description = "Name of the item") name: String
): String {
    // Implementation
}
```

### Switching to Anthropic Claude

To use Anthropic Claude instead of Ollama:

1. Edit `backend/build.gradle.kts`:
   - Comment out `spring-ai-starter-model-ollama`
   - Uncomment `spring-ai-starter-model-anthropic`
2. Set `ANTHROPIC_API_KEY` in `.env`
3. Rebuild backend

### Adding New API Endpoints

1. Create DTO in `dto/` package
2. Create controller in `controller/` package
3. Create service in `service/` package
4. Update TypeScript types in `frontend/src/types/api.ts`
5. Add API client method in `frontend/src/services/api.ts`
