package org.example.service

import org.example.model.ShoppingItem
import org.example.repository.ShoppingItemRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Service for managing shopping list items.
 *
 * Handles CRUD operations for shopping items with duplicate detection.
 * When an item with the same name already exists, the quantity is incremented
 * rather than creating a duplicate entry.
 */
@Service
class ShoppingItemService(
    private val shoppingItemRepository: ShoppingItemRepository
) {
    private val logger = LoggerFactory.getLogger(ShoppingItemService::class.java)

    /**
     * Get all shopping items ordered by creation date.
     */
    fun getAllItems(): List<ShoppingItem> {
        logger.info("Fetching all shopping items")
        return shoppingItemRepository.findAllByOrderByCreatedAtDesc()
    }

    /**
     * Add an item to the shopping list.
     *
     * If an item with the same name already exists (case-insensitive),
     * the quantity is incremented instead of creating a duplicate.
     *
     * @param name The item name
     * @param quantity The quantity to add (default: 1)
     * @return The created or updated shopping item
     */
    @Transactional
    fun addItem(name: String, quantity: Int = 1): ShoppingItem {
        val normalizedName = name.trim()
        logger.info("Adding item: {} (quantity: {})", normalizedName, quantity)

        // Check for existing item with same name (case-insensitive)
        val existingItemOptional = shoppingItemRepository.findByNameIgnoreCase(normalizedName)

        return if (existingItemOptional.isPresent) {
            // Increment quantity of existing item
            val existingItem = existingItemOptional.get()
            logger.info("Item '{}' already exists, incrementing quantity by {}", normalizedName, quantity)
            existingItem.quantity += quantity
            shoppingItemRepository.save(existingItem)
        } else {
            // Create new item
            val newItem = ShoppingItem(
                name = normalizedName,
                quantity = quantity
            )
            shoppingItemRepository.save(newItem)
        }
    }

    /**
     * Get a single item by ID.
     *
     * @param id The item UUID
     * @return The item if found, null otherwise
     */
    fun getItem(id: UUID): ShoppingItem? {
        logger.info("Fetching item with id: {}", id)
        return shoppingItemRepository.findById(id).orElse(null)
    }

    /**
     * Update an item's quantity.
     *
     * @param id The item UUID
     * @param quantity The new quantity
     * @return The updated item if found, null otherwise
     */
    @Transactional
    fun updateItem(id: UUID, quantity: Int): ShoppingItem? {
        logger.info("Updating item {} with quantity: {}", id, quantity)
        val item = shoppingItemRepository.findById(id).orElse(null) ?: return null
        item.quantity = quantity
        return shoppingItemRepository.save(item)
    }

    /**
     * Remove an item from the shopping list.
     *
     * @param id The item UUID
     * @return true if the item was deleted, false if not found
     */
    @Transactional
    fun removeItem(id: UUID): Boolean {
        logger.info("Removing item with id: {}", id)
        return if (shoppingItemRepository.existsById(id)) {
            shoppingItemRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    /**
     * Clear all items from the shopping list.
     */
    @Transactional
    fun clearAll() {
        logger.info("Clearing all shopping items")
        shoppingItemRepository.deleteAll()
    }
}