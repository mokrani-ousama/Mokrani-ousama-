package com.example.data.local

import androidx.room.*
import com.example.data.model.DiscountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiscountDao {
    @Query("SELECT * FROM discounts ORDER BY code ASC")
    fun getAllDiscounts(): Flow<List<DiscountEntity>>

    @Query("SELECT * FROM discounts WHERE code = :code COLLATE NOCASE LIMIT 1")
    suspend fun getDiscountByCode(code: String): DiscountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiscount(discount: DiscountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiscounts(discounts: List<DiscountEntity>)

    @Update
    suspend fun updateDiscount(discount: DiscountEntity)

    @Delete
    suspend fun deleteDiscount(discount: DiscountEntity)

    @Query("UPDATE discounts SET usageCount = usageCount + 1 WHERE code = :code COLLATE NOCASE")
    suspend fun incrementUsage(code: String)
}
