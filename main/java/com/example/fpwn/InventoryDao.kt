package com.example.fpwn

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface InventoryDao {
    @Insert
    suspend fun addItem(item: InventoryItem)

    @Update
    suspend fun updateItem(item: InventoryItem)

    @Delete
    suspend fun deleteItem(item: InventoryItem)

    @Query("DELETE FROM inventory_table WHERE userId = :userId")
    suspend fun deleteAllItems(userId: Long)

    @Query("SELECT * FROM inventory_table WHERE userId = :userId")
    suspend fun getAllItems(userId: Long): List<InventoryItem>
}