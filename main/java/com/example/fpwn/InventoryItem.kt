package com.example.fpwn

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_table")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val userId: Long,
    val description: String,
    val quantity: Int,
    val unit: String
)