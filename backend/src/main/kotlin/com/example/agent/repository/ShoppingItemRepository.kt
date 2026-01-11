package com.example.agent.repository

import com.example.agent.model.ShoppingItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface ShoppingItemRepository : JpaRepository<ShoppingItem, UUID> {

    @Query("SELECT s FROM ShoppingItem s WHERE LOWER(s.name) = LOWER(:name)")
    fun findByNameIgnoreCase(name: String): Optional<ShoppingItem>

    fun findAllByOrderByCreatedAtDesc(): List<ShoppingItem>
}
