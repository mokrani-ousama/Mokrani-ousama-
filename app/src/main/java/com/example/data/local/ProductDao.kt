package com.example.data.local

import androidx.room.*
import com.example.data.model.ProductEntity
import com.example.data.model.ProductOptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY isPack DESC, name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY isPack DESC, name ASC")
    fun getActiveProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: String): ProductEntity?

    @Query("SELECT * FROM product_options WHERE productId = :productId OR productId = '*' ORDER BY groupType, surchargeDa ASC")
    fun getOptionsForProduct(productId: String): Flow<List<ProductOptionEntity>>

    @Query("SELECT * FROM product_options WHERE productId = :productId OR productId = '*' ORDER BY groupType, surchargeDa ASC")
    suspend fun getOptionsForProductSync(productId: String): List<ProductOptionEntity>

    @Query("SELECT * FROM product_options ORDER BY productId, groupType")
    fun getAllOptions(): Flow<List<ProductOptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOption(option: ProductOptionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptions(options: List<ProductOptionEntity>)

    @Update
    suspend fun updateOption(option: ProductOptionEntity)

    @Delete
    suspend fun deleteOption(option: ProductOptionEntity)
}
