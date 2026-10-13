package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "discounts")
data class DiscountEntity(
    @PrimaryKey val code: String, // e.g. "DIAWISS2026", "PROMO10"
    val description: String,
    val discountType: String, // "PERCENTAGE" or "FIXED"
    val value: Double, // 10.0 for 10%, or 500.0 for 500 DA
    val targetScope: String, // "ORDER", "CATEGORY", "PRODUCT"
    val targetScopeValue: String? = null, // e.g. "JACKET" or product ID, or null for ORDER
    val minOrderDa: Int = 0,
    val isActive: Boolean = true,
    val usageCount: Int = 0
)

data class DiscountCalculationResult(
    val isValid: Boolean,
    val discountAmountDa: Int,
    val appliedCode: String?,
    val errorMessage: String? = null
)
