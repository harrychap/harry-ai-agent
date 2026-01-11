# Implementation Plan: AI Agent Shopping List Boilerplate

**Branch**: `001-ai-agent-boilerplate` | **Date**: 2026-01-11 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-ai-agent-boilerplate/spec.md`

## Summary

Create a learning-focused boilerplate for building an AI agent shopping list application. The system includes a Spring Boot/Kotlin backend with Spring AI Anthropic integration, a React frontend chatbot interface, and PostgreSQL database, all orchestrated via Docker Compose. The boilerplate provides working infrastructure with instructional "TODO: AI" markers where the user will implement their own agent logic.

## Technical Context

**Language/Version**: Kotlin (Java 21), TypeScript/JavaScript (Node 24)
**Primary Dependencies**: Spring Boot 3.x, React 18.x, Spring AI Anthropic
**Storage**: PostgreSQL (local via Docker)
**Testing**: Optional per POC constitution (JUnit 5 if needed, Jest/Vitest if needed)
**Target Platform**: Local development (Docker Compose on macOS/Linux/Windows)
**Project Type**: Web application (frontend + backend)
**Performance Goals**: Sub-second response times for chat; 2-minute stack startup (per SC-001, SC-003)
**Constraints**: Single-user local development; no authentication required
**Scale/Scope**: Single developer learning project; minimal UI (chat interface only)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Status | Notes |
|-----------|--------|-------|
| I. Simplicity First | ✅ PASS | Flat structure, minimal abstractions, boilerplate-only scope |
| II. Iterative Development | ✅ PASS | 4 user stories prioritized for incremental delivery (P1→P4) |
| III. Practical Observability | ✅ PASS | Structured logging required; errors must surface clearly |

**Gate Result**: PASSED - No violations requiring justification.

## Project Structure

### Documentation (this feature)

```text
specs/001-ai-agent-boilerplate/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output (OpenAPI specs)
└── tasks.md             # Phase 2 output (/speckit.tasks command)
```

### Source Code (repository root)

```text
backend/
├── src/main/kotlin/
│   ├── config/           # Spring configuration, Anthropic client setup
│   ├── controller/       # REST endpoints (chat, shopping items, health)
│   ├── model/            # Entity classes (ShoppingItem, ChatMessage)
│   ├── repository/       # Spring Data JPA repositories
│   ├── service/          # Business logic, agent integration point
│   └── Application.kt    # Main entry point
├── src/main/resources/
│   ├── application.yml   # Spring configuration
│   └── db/migration/     # Flyway migrations (if used) or schema.sql
├── build.gradle.kts      # Gradle build with Kotlin DSL
└── Dockerfile            # Backend container

frontend/
├── src/
│   ├── components/       # React components (ChatWindow, MessageInput, etc.)
│   ├── services/         # API client for backend communication
│   ├── hooks/            # Custom React hooks (useChat, etc.)
│   ├── types/            # TypeScript type definitions
│   ├── App.tsx           # Main app component
│   └── main.tsx          # Entry point
├── package.json          # Node dependencies
├── vite.config.ts        # Vite bundler config
└── Dockerfile            # Frontend container

docker-compose.yml        # Orchestrates all services
.env.example              # Environment variable template (ANTHROPIC_API_KEY)
ARCHITECTURE.md           # Project architecture guide (FR-009)
```

**Structure Decision**: Web application structure selected (Option 2) with backend/ and frontend/ directories. This aligns with the explicit requirement for separate Spring Boot backend and React frontend services.

## Complexity Tracking

> No violations to justify - design follows Simplicity First principle.

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| N/A | N/A | N/A |
