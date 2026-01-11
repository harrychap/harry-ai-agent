# Research: AI Agent Shopping List Boilerplate

**Date**: 2026-01-11
**Feature**: 001-ai-agent-boilerplate

## Technology Decisions

### 1. Spring AI with Anthropic

**Decision**: Use `org.springframework.ai:spring-ai-starter-model-anthropic:1.1.2`

**Rationale**:
- Official Spring integration for AI models including Anthropic Claude
- Provides higher-level abstractions (ChatClient, ChatModel) for easier integration
- Auto-configuration with Spring Boot for minimal setup
- Built-in support for function calling, streaming, and structured output
- Consistent API across different AI providers (easy to switch models)
- Native Spring ecosystem integration (dependency injection, properties, etc.)

**Alternatives Considered**:
- Direct Anthropic SDK → Rejected: More boilerplate, less Spring-idiomatic
- HTTP client directly → Rejected: No type safety, manual error handling

**Gradle Dependency**:
```kotlin
implementation("org.springframework.ai:spring-ai-starter-model-anthropic:1.1.2")
```

**Configuration (application.yml)**:
```yaml
spring.ai:
  anthropic:
    api-key: ${ANTHROPIC_API_KEY}
    chat:
      options:
        model: claude-sonnet-4-20250514
        max-tokens: 1024
```

**Usage Pattern**:
```kotlin
@Service
class AgentService(private val chatClient: ChatClient) {
    fun chat(message: String): String {
        return chatClient.prompt()
            .user(message)
            .call()
            .content() ?: "Error"
    }
}
```

---

### 2. Spring Boot + Kotlin + Java Version

**Decision**: Spring Boot 3.5.3 with Kotlin 2.1.10 on Java 21

**Rationale**:
- Spring Boot 3.5 is latest stable 3.x with support through June 2026 (free)
- Kotlin 2.1.x confirmed working with Spring Boot 3.4+ and Java 21
- Java 21 is an LTS release with long-term support
- Stable combination avoids bleeding-edge compatibility issues

**Alternatives Considered**:
- Spring Boot 4.0 + Kotlin 2.2 → Rejected: Too new for learning project, less community resources
- Java 21 → Rejected: User explicitly specified Java 21

**Version Matrix**:
| Component | Version |
|-----------|---------|
| Spring Boot | 3.5.3 |
| Kotlin | 2.1.10 |
| Java | 25 (LTS) |
| Spring Framework | 6.2.x (bundled) |

---

### 3. Gradle Configuration

**Decision**: Gradle 9.2.1 with Kotlin DSL

**Rationale**:
- Gradle 9.2.1 released November 2025 - stable and current
- Full Java 21 support introduced in Gradle 9.1.0
- 42% performance improvement on large Java builds
- Type-safe Kotlin DSL preferred over Groovy

**Alternatives Considered**:
- Maven → Rejected: User explicitly specified Gradle
- Gradle with Groovy DSL → Rejected: Kotlin DSL is more maintainable with IDE support

**Key Plugins Required**:
```kotlin
plugins {
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.spring") version "2.1.10"
    kotlin("plugin.jpa") version "2.1.10"
}
```

---

### 4. Frontend Stack

**Decision**: Node 24 LTS + Vite 6.x + React 18 + TypeScript

**Rationale**:
- Node.js 24 entered Active LTS October 2025 (support through April 2028)
- Vite is the new standard (Create React App deprecated)
- Near-instant dev server startup with native ES modules
- TypeScript provides type safety matching backend DTOs

**Alternatives Considered**:
- Create React App → Rejected: Deprecated, no longer maintained
- Next.js → Rejected: Overkill for simple chat UI, adds SSR complexity
- Plain JavaScript → Rejected: TypeScript improves learning experience with type hints

**Version Matrix**:
| Component | Version |
|-----------|---------|
| Node.js | 24 (LTS) |
| React | 18.3.x |
| Vite | 6.x |
| TypeScript | 5.5.x |

---

### 5. PostgreSQL + Spring Data JPA (Kotlin)

**Decision**: Regular Kotlin classes (not data classes) with kotlin-jpa and all-open plugins

**Rationale**:
- Data classes cause issues with JPA lazy loading and `equals`/`hashCode`
- `var` properties required for JPA entity state management
- kotlin-jpa plugin generates required no-arg constructors
- all-open plugin enables Hibernate proxy generation

**Alternatives Considered**:
- Data classes → Rejected: Broken lazy loading, problematic equals/hashCode
- Immutable entities → Rejected: JPA requires mutability

**Entity Pattern**:
```kotlin
@Entity
@Table(name = "shopping_items")
class ShoppingItem(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(nullable = false, unique = true)
    var name: String,

    @Column(nullable = false)
    var quantity: Int = 1
)
```

**Required Gradle Configuration**:
```kotlin
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}
```

---

## Final Version Summary

| Component | Package/Tool | Version |
|-----------|--------------|---------|
| Backend Runtime | Java | 25 (LTS) |
| Backend Language | Kotlin | 2.1.10 |
| Backend Framework | Spring Boot | 3.5.3 |
| Build Tool | Gradle | 9.2.1 |
| AI SDK | spring-ai-anthropic | 1.1.2 |
| Database | PostgreSQL | 16.x |
| Frontend Runtime | Node.js | 24 (LTS) |
| Frontend Framework | React | 18.3.x |
| Build/Dev Server | Vite | 6.x |
| Type System | TypeScript | 5.5.x |

---

## Resolved Clarifications

All NEEDS CLARIFICATION items from Technical Context have been resolved through this research. No outstanding unknowns remain.
