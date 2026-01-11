package com.example.agent.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.UUID

data class ShoppingItemDto(
    val id: UUID?,
    val name: String,
    val quantity: Int,
    val createdAt: Instant?,
    val updatedAt: Instant?
)

data class AddItemRequest(
    @field:NotBlank(message = "Item name cannot be blank")
    @field:Size(max = 255, message = "Item name cannot exceed 255 characters")
    val name: String,

    @field:Min(value = 1, message = "Quantity must be at least 1")
    val quantity: Int? = 1
)

data class UpdateItemRequest(
    @field:Min(value = 1, message = "Quantity must be at least 1")
    val quantity: Int
)

data class ShoppingItemListResponse(
    val items: List<ShoppingItemDto>,
    val count: Int
)

data class RemoveItemResponse(
    val action: String,
    val item: ShoppingItemDto?
)

data class ShoppingItemResponse(
    val id: UUID,
    val name: String,
    val quantity: Int,
    val createdAt: Instant,
    val updatedAt: Instant
)
