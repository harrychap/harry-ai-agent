# Tasks: AI Agent Shopping List Boilerplate

**Input**: Design documents from `/specs/001-ai-agent-boilerplate/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/

**Tests**: Testing is optional per POC constitution. No test tasks included unless explicitly requested.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3, US4)
- Include exact file paths in descriptions

## Path Conventions

- **Backend**: `backend/src/main/kotlin/com/example/agent/`
- **Frontend**: `frontend/src/`
- **Infrastructure**: Repository root

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 Create backend directory structure per plan.md in backend/
- [x] T002 Create frontend directory structure per plan.md in frontend/
- [x] T003 [P] Initialize Gradle project with build.gradle.kts (Spring Boot 3.5.3, Kotlin 2.1.10, Java 21)
- [x] T004 [P] Initialize Vite + React + TypeScript project with package.json (Node 24, React 18, Vite 6)
- [x] T005 [P] Create .env.example with ANTHROPIC_API_KEY placeholder at repository root
- [x] T006 [P] Create .gitignore with appropriate exclusions (node_modules, build, .env) at repository root

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T007 Create Application.kt main entry point in backend/src/main/kotlin/com/example/agent/Application.kt
- [x] T008 Create application.yml with PostgreSQL, logging, and server config in backend/src/main/resources/application.yml
- [x] T009 [P] Create database schema.sql with all tables (shopping_items, chat_sessions, chat_messages) in backend/src/main/resources/schema.sql
- [x] T010 [P] Create ShoppingItem entity in backend/src/main/kotlin/com/example/agent/model/ShoppingItem.kt
- [x] T011 [P] Create ChatSession entity in backend/src/main/kotlin/com/example/agent/model/ChatSession.kt
- [x] T012 [P] Create ChatMessage entity in backend/src/main/kotlin/com/example/agent/model/ChatMessage.kt
- [x] T013 [P] Create ShoppingItemRepository in backend/src/main/kotlin/com/example/agent/repository/ShoppingItemRepository.kt
- [x] T014 [P] Create ChatSessionRepository in backend/src/main/kotlin/com/example/agent/repository/ChatSessionRepository.kt
- [x] T015 [P] Create ChatMessageRepository in backend/src/main/kotlin/com/example/agent/repository/ChatMessageRepository.kt
- [x] T016 Create AnthropicConfig with SDK client setup in backend/src/main/kotlin/com/example/agent/config/AnthropicConfig.kt
- [x] T017 Create GlobalExceptionHandler for error responses in backend/src/main/kotlin/com/example/agent/config/GlobalExceptionHandler.kt
- [x] T018 [P] Create DTO classes (request/response) in backend/src/main/kotlin/com/example/agent/dto/
- [x] T019 [P] Create TypeScript API types matching backend DTOs in frontend/src/types/api.ts
- [x] T020 [P] Create API client service in frontend/src/services/api.ts

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Start Development Environment (Priority: P1) 🎯 MVP

**Goal**: Developer can start entire stack with single command and verify all services are healthy

**Independent Test**: Run `docker compose up -d`, open http://localhost:5173, verify chat UI loads, check http://localhost:8080/api/health returns healthy status

### Implementation for User Story 1

- [x] T021 [US1] Create Dockerfile for backend with Java 21 + Gradle build in backend/Dockerfile
- [x] T022 [US1] Create Dockerfile for frontend with Node 24 + Vite build in frontend/Dockerfile
- [x] T023 [US1] Create docker-compose.yml orchestrating backend, frontend, and PostgreSQL at repository root
- [x] T024 [US1] Create HealthController with /api/health endpoint in backend/src/main/kotlin/com/example/agent/controller/HealthController.kt
- [x] T025 [US1] Create vite.config.ts with API proxy to backend in frontend/vite.config.ts
- [x] T026 [US1] Create basic App.tsx shell with chat container in frontend/src/App.tsx
- [x] T027 [US1] Create main.tsx entry point in frontend/src/main.tsx
- [x] T028 [US1] Create index.html with app mount point in frontend/index.html
- [x] T029 [US1] Add structured logging configuration in backend/src/main/resources/logback-spring.xml

**Checkpoint**: `docker compose up -d` starts all services; health endpoint returns healthy; frontend loads

---

## Phase 4: User Story 2 - Send Chat Messages (Priority: P2)

**Goal**: Chat interface sends messages to backend and displays responses with session history

**Independent Test**: Type message in chat UI, see message appear, receive placeholder response, verify history preserved during session

### Implementation for User Story 2

- [x] T030 [US2] Create ChatService with session management in backend/src/main/kotlin/com/example/agent/service/ChatService.kt
- [x] T031 [US2] Create AgentService with stubbed Anthropic calls and TODO markers in backend/src/main/kotlin/com/example/agent/service/AgentService.kt
- [x] T032 [US2] Create ChatController with POST /api/chat and GET /api/chat/{sessionId}/history in backend/src/main/kotlin/com/example/agent/controller/ChatController.kt
- [x] T033 [US2] Create ChatWindow component displaying message history in frontend/src/components/ChatWindow.tsx
- [x] T034 [US2] Create MessageInput component with submit handling in frontend/src/components/MessageInput.tsx
- [x] T035 [US2] Create MessageBubble component for user/assistant messages in frontend/src/components/MessageBubble.tsx
- [x] T036 [US2] Create useChat hook for chat state management in frontend/src/hooks/useChat.ts
- [x] T037 [US2] Integrate chat components into App.tsx in frontend/src/App.tsx
- [x] T038 [US2] Add basic CSS styling for chat interface in frontend/src/App.css
- [x] T039 [US2] Add input validation (prevent empty messages) in frontend/src/components/MessageInput.tsx

**Checkpoint**: Full chat flow works - send message, see response, history preserved in session

---

## Phase 5: User Story 3 - Manage Shopping Items via Chat (Priority: P3)

**Goal**: Shopping list CRUD endpoints work with database persistence and instructional comments

**Independent Test**: Call /api/items endpoints via curl, verify items persist in database, see TODO comments in code

### Implementation for User Story 3

- [x] T040 [US3] Create ShoppingItemService with add/list/remove logic in backend/src/main/kotlin/com/example/agent/service/ShoppingItemService.kt
- [x] T041 [US3] Create ShoppingItemController with CRUD endpoints per OpenAPI spec in backend/src/main/kotlin/com/example/agent/controller/ShoppingItemController.kt
- [x] T042 [US3] Implement duplicate handling (increment quantity) in ShoppingItemService in backend/src/main/kotlin/com/example/agent/service/ShoppingItemService.kt
- [x] T043 [US3] Add TODO: AI comments in AgentService for shopping list intent parsing in backend/src/main/kotlin/com/example/agent/service/AgentService.kt
- [x] T044 [US3] Add request validation with error responses in ShoppingItemController in backend/src/main/kotlin/com/example/agent/controller/ShoppingItemController.kt

**Checkpoint**: All /api/items endpoints work; items persist across restarts; TODO comments visible

---

## Phase 6: User Story 4 - Follow Learning Path (Priority: P4)

**Goal**: Comprehensive instructional comments and architecture documentation throughout codebase

**Independent Test**: Search for "TODO: AI" markers, read ARCHITECTURE.md, find comments explaining agent integration points

### Implementation for User Story 4

- [x] T045 [US4] Add detailed TODO: AI comments in AgentService explaining Anthropic integration in backend/src/main/kotlin/com/example/agent/service/AgentService.kt
- [x] T046 [US4] Add instructional comments in ChatController explaining message flow in backend/src/main/kotlin/com/example/agent/controller/ChatController.kt
- [x] T047 [US4] Add instructional comments in AnthropicConfig explaining SDK setup in backend/src/main/kotlin/com/example/agent/config/AnthropicConfig.kt
- [x] T048 [US4] Create ARCHITECTURE.md with full project structure guide at repository root
- [x] T049 [US4] Add instructional comments in useChat hook explaining frontend state flow in frontend/src/hooks/useChat.ts
- [x] T050 [US4] Add instructional comments in api.ts explaining backend communication in frontend/src/services/api.ts

**Checkpoint**: All agent integration points have TODO: AI markers; ARCHITECTURE.md is complete; code is self-documenting

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Final improvements and validation

- [x] T051 [P] Update README.md with project overview and link to quickstart at repository root
- [x] T052 [P] Verify all services start within 2 minutes (SC-001) via docker compose timing
- [x] T053 [P] Verify health endpoint returns correct response (SC-006) via curl test
- [x] T054 Run quickstart.md validation - follow all steps as new developer
- [x] T055 Verify all "TODO: AI" markers are searchable and count matches expectations (SC-005)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: No dependencies - can start immediately
- **Phase 2 (Foundational)**: Depends on Setup completion - BLOCKS all user stories
- **Phase 3 (US1)**: Depends on Foundational - delivers MVP
- **Phase 4 (US2)**: Depends on Foundational - can run parallel to US1 if needed
- **Phase 5 (US3)**: Depends on Foundational - can run parallel to US1/US2 if needed
- **Phase 6 (US4)**: Depends on US1-US3 code existing (adds comments to existing files)
- **Phase 7 (Polish)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Independent after Phase 2 - Docker/health focus
- **User Story 2 (P2)**: Independent after Phase 2 - Chat functionality focus
- **User Story 3 (P3)**: Independent after Phase 2 - Shopping list focus
- **User Story 4 (P4)**: Depends on US1-US3 files existing - documentation focus

### Within Each User Story

- Backend before frontend (API must exist for frontend to call)
- Models/Services before Controllers
- Core implementation before styling/polish

### Parallel Opportunities

Within Phase 1:
```
T003 (Gradle) || T004 (Vite) || T005 (.env) || T006 (.gitignore)
```

Within Phase 2:
```
T009 (schema) || T010 (ShoppingItem) || T011 (ChatSession) || T012 (ChatMessage)
T013 (ItemRepo) || T014 (SessionRepo) || T015 (MessageRepo)
T018 (DTOs) || T019 (TS types) || T020 (API client)
```

Within Phase 4 (US2):
```
T033 (ChatWindow) || T034 (MessageInput) || T035 (MessageBubble)
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: `docker compose up -d` works, health check passes
5. Ready for demo - stack runs!

### Incremental Delivery

1. **Setup + Foundational** → Project skeleton ready
2. **+ User Story 1** → Stack starts with single command (MVP!)
3. **+ User Story 2** → Chat interface works with placeholder responses
4. **+ User Story 3** → Shopping list CRUD works
5. **+ User Story 4** → Documentation and learning materials complete
6. **+ Polish** → Production-quality boilerplate

### Recommended Execution Order (Single Developer)

Execute tasks in numerical order (T001 → T055). The task IDs are sequenced for optimal dependency resolution.

---

## Notes

- [P] tasks = different files, no dependencies within same phase
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- All backend paths assume package `com.example.agent` - adjust as needed
