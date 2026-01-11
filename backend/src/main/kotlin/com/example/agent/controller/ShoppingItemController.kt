package com.example.agent.controller

import com.example.agent.dto.AddItemRequest
import com.example.agent.dto.ShoppingItemResponse
import com.example.agent.dto.UpdateItemRequest
import com.example.agent.service.ShoppingItemService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * REST controller for shopping item management.
 *
 * Provides CRUD endpoints for managing the shopping list.
 * All endpoints are prefixed with /api/items.
 */
@RestController
@RequestMapping("/api/items")
class ShoppingItemController(
    private val shoppingItemService: ShoppingItemService
) {
    private val logger = LoggerFactory.getLogger(ShoppingItemController::class.java)

    /**
     * Get all shopping items.
     *
     * GET /api/items
     *
     * @return List of all shopping items
     */
    @GetMapping
    fun getAllItems(): ResponseEntity<List<ShoppingItemResponse>> {
        logger.debug("GET /api/items - Fetching all items")
        val items = shoppingItemService.getAllItems().map { item ->
            ShoppingItemResponse(
                id = item.id!!,
                name = item.name,
                quantity = item.quantity,
                createdAt = item.createdAt!!,
                updatedAt = item.updatedAt!!
            )
        }
        return ResponseEntity.ok(items)
    }

    /**
     * Add a new item to the shopping list.
     *
     * POST /api/items
     *
     * If an item with the same name exists, quantity is incremented.
     *
     * @param request The item to add (name required, quantity optional)
     * @return The created or updated item
     */
    @PostMapping
    fun addItem(@Valid @RequestBody request: AddItemRequest): ResponseEntity<ShoppingItemResponse> {
        logger.info("POST /api/items - Adding item: {}", request.name)

        val item = shoppingItemService.addItem(
            name = request.name,
            quantity = request.quantity ?: 1
        )

        val response = ShoppingItemResponse(
            id = item.id!!,
            name = item.name,
            quantity = item.quantity,
            createdAt = item.createdAt!!,
            updatedAt = item.updatedAt!!
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /**
     * Get a single item by ID.
     *
     * GET /api/items/{id}
     *
     * @param id The item UUID
     * @return The item if found, 404 otherwise
     */
    @GetMapping("/{id}")
    fun getItem(@PathVariable id: UUID): ResponseEntity<ShoppingItemResponse> {
        logger.debug("GET /api/items/{} - Fetching item", id)

        val item = shoppingItemService.getItem(id)
            ?: return ResponseEntity.notFound().build()

        val response = ShoppingItemResponse(
            id = item.id!!,
            name = item.name,
            quantity = item.quantity,
            createdAt = item.createdAt!!,
            updatedAt = item.updatedAt!!
        )

        return ResponseEntity.ok(response)
    }

    /**
     * Update an item's quantity.
     *
     * PUT /api/items/{id}
     *
     * @param id The item UUID
     * @param request The update request containing new quantity
     * @return The updated item if found, 404 otherwise
     */
    @PutMapping("/{id}")
    fun updateItem(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateItemRequest
    ): ResponseEntity<ShoppingItemResponse> {
        logger.info("PUT /api/items/{} - Updating item", id)

        val item = shoppingItemService.updateItem(id, request.quantity)
            ?: return ResponseEntity.notFound().build()

        val response = ShoppingItemResponse(
            id = item.id!!,
            name = item.name,
            quantity = item.quantity,
            createdAt = item.createdAt!!,
            updatedAt = item.updatedAt!!
        )

        return ResponseEntity.ok(response)
    }

    /**
     * Delete an item from the shopping list.
     *
     * DELETE /api/items/{id}
     *
     * @param id The item UUID
     * @return 204 No Content if deleted, 404 if not found
     */
    @DeleteMapping("/{id}")
    fun deleteItem(@PathVariable id: UUID): ResponseEntity<Void> {
        logger.info("DELETE /api/items/{} - Deleting item", id)

        return if (shoppingItemService.removeItem(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Clear all items from the shopping list.
     *
     * DELETE /api/items
     *
     * @return 204 No Content
     */
    @DeleteMapping
    fun clearAll(): ResponseEntity<Void> {
        logger.info("DELETE /api/items - Clearing all items")
        shoppingItemService.clearAll()
        return ResponseEntity.noContent().build()
    }
}
