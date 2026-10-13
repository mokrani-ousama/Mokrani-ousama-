package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String, // "CHAPEAU", "ECHARPE", "JACKET", "PACK"
    val description: String,
    val basePriceDa: Int,
    val drawableName: String,
    val isPack: Boolean = false,
    val isActive: Boolean = true,
    val allowsCustomDimensions: Boolean = true,
    val badge: String? = null
)

@Entity(tableName = "product_options")
data class ProductOptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: String, // foreign product id or "*" for all
    val groupType: String, // "STYLE", "COLOR", "SIZE", "LENGTH", "FABRIC", "EMBROIDERY"
    val groupLabel: String,
    val optionName: String,
    val surchargeDa: Int = 0,
    val isDefault: Boolean = false,
    val colorHex: String? = null
)

data class ProductWithOptions(
    val product: ProductEntity,
    val options: List<ProductOptionEntity>
)
