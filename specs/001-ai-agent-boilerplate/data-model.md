# Data Model: AI Agent Shopping List Boilerplate

**Date**: 2026-01-11
**Feature**: 001-ai-agent-boilerplate

## Entity Relationship Diagram

```
┌─────────────────────┐
│   ChatSession       │
├─────────────────────┤
│ id: UUID (PK)       │
│ createdAt: Instant  │
│ updatedAt: Instant  │
└─────────────────────┘
         │
         │ 1:N
         ▼
┌─────────────────────┐
│   ChatMessage       │
├─────────────────────┤
│ id: UUID (PK)       │
│ sessionId: UUID (FK)│
│ role: String        │
│ content: String     │
│ createdAt: Instant  │
└─────────────────────┘


┌─────────────────────┐
│   ShoppingItem      │
├─────────────────────┤
│ id: UUID (PK)       │
│ name: String (UQ)   │
│ quantity: Int       │
│ createdAt: Instant  │
│ updatedAt: Instant  │
└─────────────────────┘
```

## Entities

### ShoppingItem

Represents an item on the shopping list.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | UUID | PK, auto-generated | Unique identifier |
| name | String | NOT NULL, UNIQUE, max 255 chars | Item name (case-insensitive uniqueness) |
| quantity | Integer | NOT NULL, default 1, min 1 | Number of items needed |
| createdAt | Instant | NOT NULL, auto-set | When item was first added |
| updatedAt | Instant | NOT NULL, auto-updated | When item was last modified |

**Business Rules**:
- Adding an item with an existing name increments quantity (FR clarification)
- Name uniqueness is case-insensitive ("Milk" == "milk")
- Quantity cannot be zero or negative; removing reduces to 0 then deletes

**State Transitions**:
```
[Not Exists] --add--> [Exists, qty=1]
[Exists, qty=N] --add same--> [Exists, qty=N+1]
[Exists, qty=N] --remove--> [Exists, qty=N-1] or [Deleted if qty=0]
```

### ChatMessage

Represents a single message in a conversation.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | UUID | PK, auto-generated | Unique identifier |
| sessionId | UUID | FK → ChatSession, NOT NULL | Parent session |
| role | String | NOT NULL, enum: 'user', 'assistant' | Who sent the message |
| content | String | NOT NULL, TEXT (unlimited) | Message content |
| createdAt | Instant | NOT NULL, auto-set | When message was sent |

**Business Rules**:
- Messages are immutable once created
- Order determined by createdAt timestamp
- Content can include multi-line text

### ChatSession

Represents a conversation session containing multiple messages.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | UUID | PK, auto-generated | Unique identifier |
| createdAt | Instant | NOT NULL, auto-set | When session started |
| updatedAt | Instant | NOT NULL, auto-updated | When last message was added |

**Business Rules**:
- Sessions are created implicitly on first message (if no session exists)
- For POC: single session per browser tab (no persistence across refreshes)
- Session history preserved in-memory during session lifetime

**Relationships**:
- One session has many messages (1:N)
- Messages ordered by createdAt ascending

---

## Kotlin Entity Definitions

```kotlin
// ShoppingItem.kt
@Entity
@Table(name = "shopping_items")
class ShoppingItem(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(nullable = false, unique = true, length = 255)
    var name: String,

    @Column(nullable = false)
    var quantity: Int = 1,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}

// ChatSession.kt
@Entity
@Table(name = "chat_sessions")
class ChatSession(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now(),

    @OneToMany(mappedBy = "session", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var messages: MutableList<ChatMessage> = mutableListOf()
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}

// ChatMessage.kt
@Entity
@Table(name = "chat_messages")
class ChatMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    var session: ChatSession,

    @Column(nullable = false, length = 20)
    var role: String,  // "user" or "assistant"

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant = Instant.now()
)
```

---

## Database Schema (PostgreSQL)

```sql
-- Shopping Items
CREATE TABLE shopping_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1 CHECK (quantity >= 1),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_shopping_items_name UNIQUE (LOWER(name))
);

CREATE INDEX idx_shopping_items_name ON shopping_items (LOWER(name));

-- Chat Sessions
CREATE TABLE chat_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Chat Messages
CREATE TABLE chat_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL REFERENCES chat_sessions(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL CHECK (role IN ('user', 'assistant')),
    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_chat_messages_session ON chat_messages (session_id, created_at);
```

---

## DTOs (Data Transfer Objects)

```kotlin
// Request/Response DTOs for API layer

data class ShoppingItemDto(
    val id: UUID?,
    val name: String,
    val quantity: Int,
    val createdAt: Instant?,
    val updatedAt: Instant?
)

data class AddItemRequest(
    val name: String,
    val quantity: Int = 1
)

data class ChatMessageDto(
    val id: UUID?,
    val role: String,
    val content: String,
    val createdAt: Instant?
)

data class SendMessageRequest(
    val message: String,
    val sessionId: UUID? = null  // Optional: create new session if null
)

data class ChatResponse(
    val sessionId: UUID,
    val message: ChatMessageDto,
    val history: List<ChatMessageDto>
)
```

---

## Validation Rules

| Entity | Field | Validation |
|--------|-------|------------|
| ShoppingItem | name | Not blank, max 255 chars, trimmed |
| ShoppingItem | quantity | Min 1 |
| ChatMessage | role | Must be "user" or "assistant" |
| ChatMessage | content | Not blank |
| SendMessageRequest | message | Not blank, max 10000 chars |
