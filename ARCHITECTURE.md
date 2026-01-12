# Architecture Guide

This document provides a comprehensive overview of the AI Chat Assistant architecture.

## Project Structure

```
harry-ai-agent/
├── backend/                     # Spring Boot + Kotlin backend
│   ├── src/main/kotlin/com/example/agent/
│   │   ├── Application.kt       # Main entry point
│   │   ├── config/              # Configuration classes
│   │   │   ├── AnthropicConfig.kt           # Spring AI ChatClient setup
│   │   │   ├── GlobalExceptionHandler.kt    # Error handling
│   │   │   └── InMemoryChatMemoryRepository.kt  # Chat history storage
│   │   ├── controller/          # REST endpoints
│   │   │   ├── ChatController.kt     # POST /api/chat
│   │   │   └── HealthController.kt   # GET /api/health
│   │   ├── dto/                 # Request/Response objects
│   │   │   ├── ChatDtos.kt
│   │   │   └── HealthDtos.kt
│   │   └── service/             # Business logic
│   │       ├── AgentService.kt       # AI integration with Claude
│   │       └── ChatService.kt        # Message handling
│   ├── src/main/resources/
│   │   ├── application.yml      # App configuration
│   │   └── logback-spring.xml   # Logging config
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
| Backend Runtime | Java | 21 |
| Backend Framework | Spring Boot | 3.5.3 |
| Backend Language | Kotlin | 2.1.10 |
| AI SDK | Spring AI Anthropic | 1.1.2 |
| Chat Memory | In-Memory (ConcurrentHashMap) | - |
| Frontend Runtime | Node.js | 24 |
| Frontend Framework | React | 18 |
| Build Tool (Frontend) | Vite | 6 |
| Build Tool (Backend) | Gradle | 9.2 |
| Container | Docker Compose | - |

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
                    │  Anthropic  │     │ ChatMemory  │     │   MCP       │
                    │     API     │     │ Repository  │     │  Servers    │
                    └─────────────┘     └─────────────┘     └─────────────┘
```

1. User types message in MessageInput component
2. useChat hook sends POST /api/chat
3. ChatController receives request
4. ChatService delegates to AgentService
5. AgentService retrieves conversation history from ChatMemoryRepository
6. AgentService calls Anthropic API with context
7. ChatMemoryRepository stores user and assistant messages
8. Response returns through stack
9. ChatWindow displays new messages

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
- Configures ChatClient with memory advisor
- Sends messages to Anthropic Claude API
- Manages conversation context via ChatMemoryRepository

### AnthropicConfig

Spring AI configuration:
- ChatClient bean setup
- Model selection (claude-sonnet-4-20250514)
- ChatMemory and ChatMemoryRepository beans
- System prompt configuration

### ChatService

Request/response handling:
- Creates DTOs for frontend communication
- Delegates AI processing to AgentService

## Docker Services

| Service | Port | Description |
|---------|------|-------------|
| backend | 8080 | Spring Boot API |
| frontend | 5173 | Vite dev server / Nginx |

### Starting the Stack

```bash
# Copy environment template
cp .env.example .env

# Add your API key
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
| ANTHROPIC_API_KEY | Yes | Claude API key |

### Application Properties

Located in `backend/src/main/resources/application.yml`:

- Server port: 8080
- Anthropic model configuration
- Logging levels

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

### Adding MCP Server Integration

The architecture supports Model Context Protocol (MCP) servers for tool integration:

1. Configure MCP servers in application.yml
2. Register tools with the ChatClient
3. Handle tool execution in AgentService

### Adding New API Endpoints

1. Create DTO in `dto/` package
2. Create controller in `controller/` package
3. Create service in `service/` package
4. Update TypeScript types in `frontend/src/types/api.ts`
5. Add API client method in `frontend/src/services/api.ts`
