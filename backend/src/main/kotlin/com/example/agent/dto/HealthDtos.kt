package com.example.agent.dto

import java.time.Instant

data class HealthResponse(
    val status: String,
    val timestamp: Instant = Instant.now(),
    val database: String? = null
)
