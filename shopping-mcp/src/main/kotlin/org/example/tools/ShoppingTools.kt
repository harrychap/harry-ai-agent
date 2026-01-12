package org.example.tools

import org.example.service.ShoppingItemService
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Component

@Component
class ShoppingTools(private val shoppingItemService: ShoppingItemService) {

    @Tool(description = "Add an item to the shopping list. If the item already exists, its quantity will be increased.")
    fun addItem(
        @ToolParam(description = "Name of the item to add") name: String,
        @ToolParam(description = "Quantity to add (default 1)") quantity: Int = 1
    ): String {

        val item = shoppingItemService.addItem(name, quantity)
        return "Added ${item.name} (quantity: ${item.quantity}) to your shopping list."
    }

    @Tool(description = "List all items in the shopping list")
    fun listItems(): String {
        val items = shoppingItemService.getAllItems()
        if (items.isEmpty()) return "Your shopping list is empty."
        return items.joinToString("\n") { "- ${it.name}: ${it.quantity}" }
    }

    @Tool(description = "Remove an item from the shopping list")
    fun removeItem(
        @ToolParam(description = "ID of the item to remove") name: String
    ): String {
        val items = shoppingItemService.getAllItems()
        val item = items.find { it.name.equals(name, ignoreCase = true) }
        return if (item?.id != null && shoppingItemService.removeItem(item.id!!)) {
            "Removed ${item.name} from your shopping list."
        } else {
            "Could not find '${name}' on your shopping list."
        }
    }

    @Tool(description = "Update the quantity of an item on the shopping list")
    fun updateItemQuantity(
        @ToolParam(description = "Name of the item to update") name: String,
        @ToolParam(description = "New quantity") quantity: Int
    ): String {
        val items = shoppingItemService.getAllItems()
        val item = items.find { it.name.equals(name, ignoreCase = true) }
        return if (item != null) {
            shoppingItemService.updateItem(item.id!!, quantity)
            "Updated ${item.name} to quantity: $quantity"
        } else {
            "Could not find '$name' on your shopping list."
        }
    }

    @Tool(description = "Clear all items from the shopping list")
    fun clearList(): String {
        shoppingItemService.clearAll()
        return "Cleared all items from your shopping list."
    }
}