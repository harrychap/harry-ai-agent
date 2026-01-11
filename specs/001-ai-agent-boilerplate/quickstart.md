# Quickstart Guide: AI Agent Shopping List

This guide walks you through getting the AI Agent Shopping List boilerplate running on your local machine.

## Prerequisites

Before you begin, ensure you have:

- [ ] **Docker Desktop** installed and running ([Download](https://www.docker.com/products/docker-desktop/))
- [ ] **Anthropic API Key** from [console.anthropic.com](https://console.anthropic.com/)
- [ ] **Git** installed for cloning the repository

## Step 1: Clone and Configure

```bash
# Clone the repository
git clone <repository-url>
cd harry-ai-agent

# Copy environment template
cp .env.example .env

# Edit .env and add your Anthropic API key
# ANTHROPIC_API_KEY=sk-ant-...
```

## Step 2: Start the Stack

```bash
# Start all services (backend, frontend, database)
docker compose up -d

# Watch the logs (optional)
docker compose logs -f
```

**Expected startup time**: Under 2 minutes

## Step 3: Verify Services

Once started, verify each service is healthy:

| Service | URL | Expected |
|---------|-----|----------|
| Frontend (Chat UI) | http://localhost:5173 | Chat interface loads |
| Backend API | http://localhost:8080/api/health | `{"status":"healthy"}` |
| PostgreSQL | localhost:5432 | (internal, no browser access) |

### Quick Health Check

```bash
# Check backend health
curl http://localhost:8080/api/health

# Expected response:
# {"status":"healthy","database":"connected","timestamp":"..."}
```

## Step 4: Try the Chat Interface

1. Open http://localhost:5173 in your browser
2. Type a message like "Add milk to my shopping list"
3. You should see a placeholder response (until you implement the agent logic)

## Step 5: Explore the Codebase

The boilerplate includes `TODO: AI` markers at all integration points. Find them with:

```bash
# Find all AI integration points
grep -r "TODO: AI" backend/src frontend/src
```

### Key Files to Explore

| File | Purpose |
|------|---------|
| `backend/src/.../service/AgentService.kt` | Main AI agent integration point |
| `backend/src/.../controller/ChatController.kt` | Chat endpoint handling |
| `backend/src/.../config/AnthropicConfig.kt` | Anthropic SDK configuration |
| `frontend/src/components/ChatWindow.tsx` | Chat UI component |
| `ARCHITECTURE.md` | Full architecture documentation |

## Common Commands

```bash
# Start all services
docker compose up -d

# Stop all services
docker compose down

# View logs
docker compose logs -f backend    # Backend only
docker compose logs -f frontend   # Frontend only

# Rebuild after code changes
docker compose up -d --build

# Reset database (clear all data)
docker compose down -v
docker compose up -d
```

## Troubleshooting

### Services won't start

```bash
# Check Docker is running
docker info

# Check for port conflicts
lsof -i :8080  # Backend port
lsof -i :5173  # Frontend port
lsof -i :5432  # PostgreSQL port
```

### Database connection errors

```bash
# Check PostgreSQL is healthy
docker compose ps

# View database logs
docker compose logs db
```

### API key not working

1. Verify your `.env` file contains: `ANTHROPIC_API_KEY=sk-ant-...`
2. Restart the backend: `docker compose restart backend`
3. Check backend logs: `docker compose logs backend`

## Next Steps

1. Read `ARCHITECTURE.md` to understand the project structure
2. Search for `TODO: AI` comments to find implementation points
3. Start with `AgentService.kt` to implement your first agent logic
4. Test your changes by chatting in the UI

## API Reference

Full API documentation is available at:
- OpenAPI Spec: `specs/001-ai-agent-boilerplate/contracts/openapi.yaml`
- Swagger UI (when running): http://localhost:8080/swagger-ui.html

### Quick API Examples

```bash
# Send a chat message
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Add eggs to my list"}'

# List shopping items
curl http://localhost:8080/api/items

# Add an item directly
curl -X POST http://localhost:8080/api/items \
  -H "Content-Type: application/json" \
  -d '{"name": "Bread", "quantity": 2}'
```
