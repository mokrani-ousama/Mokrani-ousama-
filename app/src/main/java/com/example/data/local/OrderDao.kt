package com.example.data.local

import androidx.room.*
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAtMillis DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderNumber = :orderNumber LIMIT 1")
    suspend fun getOrderByNumber(orderNumber: String): OrderEntity?

    @Query("SELECT * FROM order_items WHERE orderNumber = :orderNumber")
    suspend fun getItemsForOrder(orderNumber: String): List<OrderItemEntity>

    @Query("SELECT * FROM order_items WHERE orderNumber = :orderNumber")
    fun getItemsForOrderFlow(orderNumber: String): Flow<List<OrderItemEntity>>

    @Query("SELECT COUNT(*) FROM orders")
    suspend fun getOrdersCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Query("UPDATE orders SET status = :status WHERE orderNumber = :orderNumber")
    suspend fun updateOrderStatus(orderNumber: String, status: String)

    @Delete
    suspend fun deleteOrder(order: OrderEntity)
}
