package com.example.agent.controller

import com.example.agent.dto.HealthResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.sql.DataSource

@RestController
@RequestMapping("/health")
class HealthController(
    private val dataSource: DataSource
) {
    private val logger = LoggerFactory.getLogger(HealthController::class.java)

    @GetMapping
    fun health(): ResponseEntity<HealthResponse> {
        val dbStatus = try {
            dataSource.connection.use { connection ->
                connection.isValid(5)
            }
            "connected"
        } catch (e: Exception) {
            logger.error("Database health check failed", e)
            "disconnected"
        }

        val status = if (dbStatus == "connected") "healthy" else "unhealthy"

        logger.debug("Health check: status=$status, database=$dbStatus")

        return ResponseEntity.ok(
            HealthResponse(
                status = status,
                database = dbStatus
            )
        )
    }
}
