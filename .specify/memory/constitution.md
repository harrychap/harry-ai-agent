/<!--
  SYNC IMPACT REPORT
  ==================
  Version change: N/A → 1.0.0 (initial ratification)

  Modified principles: N/A (initial creation)

  Added sections:
  - Core Principles (3 principles: Simplicity First, Iterative Development, Practical Observability)
  - Development Workflow
  - POC Constraints
  - Governance

  Removed sections: N/A

  Templates requiring updates:
  - .specify/templates/plan-template.md: ✅ Compatible (Constitution Check section is generic)
  - .specify/templates/spec-template.md: ✅ Compatible (no principle-specific requirements)
  - .specify/templates/tasks-template.md: ✅ Compatible (testing marked optional, aligns with POC philosophy)

  Follow-up TODOs: None
-->

# Harry AI Agent Constitution

## Core Principles

### I. Simplicity First

All implementations MUST favor the simplest solution that meets immediate requirements.
Avoid premature abstraction, over-engineering, or speculative features.

**Rationale**: As a proof-of-concept, velocity and clarity matter more than scalability.
Complex patterns (repositories, factories, extensive layering) MUST be justified by concrete,
present-day needs rather than hypothetical future requirements.

### II. Iterative Development

Features MUST be developed incrementally with working software at each step.
Each iteration SHOULD deliver demonstrable functionality.

**Rationale**: POC projects succeed by validating ideas quickly. Large upfront designs
risk building the wrong thing. Prefer small, frequent commits over comprehensive
implementations.

### III. Practical Observability

Code MUST include sufficient logging to diagnose issues during development and demos.
Use structured logging where practical. Errors MUST surface clearly (not swallowed silently).

**Rationale**: Debugging AI agent behavior requires visibility into request/response flows,
decision points, and integration boundaries. Silent failures waste development time.

## Development Workflow

**Code Organization**: Keep structure flat until complexity demands otherwise. A single
`src/` directory is acceptable until clear module boundaries emerge.

**Dependencies**: Prefer well-established libraries with good documentation. Avoid bleeding-edge
dependencies unless the POC specifically tests them.

**Documentation**: Inline comments for non-obvious logic only. README updates when user-facing
behavior changes. Avoid generating documentation for documentation's sake.

## POC Constraints

**Scope**: This project is a proof-of-concept for testing AI agent capabilities in Spring Boot.
Production concerns (high availability, horizontal scaling, comprehensive security hardening)
are explicitly out of scope unless required for the specific POC goal.

**Technology**: Stack decisions are flexible per feature. No single framework or pattern is
mandated beyond what Spring Boot provides by default.

**Testing**: Testing is optional for POC work. When tests are written, they SHOULD focus on
integration points and observable behavior rather than implementation details.

## Governance

This constitution provides guiding principles, not rigid rules. Given the POC nature:

- **Amendment Process**: Update this document when project direction shifts materially.
- **Compliance**: Principles inform decisions but do not block progress. When principles
  conflict with POC goals, document the deviation and proceed.
- **Review**: Revisit constitution if/when project transitions from POC to production.

**Version**: 1.0.0 | **Ratified**: 2026-01-11 | **Last Amended**: 2026-01-11
