package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderNumber: String, // format DIA-2026-0001
    val customerFirstName: String,
    val customerLastName: String,
    val phone1: String,
    val phone2: String = "",
    val fullAddress: String,
    val wilayaCode: String,
    val wilayaName: String,
    val commune: String,
    val university: String = "",
    val specialty: String = "",
    val defenseDate: String = "",
    val customerNotes: String = "",
    val deliveryType: String, // "DOMICILE", "RELAIS", "RETRAIT"
    val subtotalDa: Int,
    val shippingFeeDa: Int,
    val discountAmountDa: Int,
    val discountCodeApplied: String? = null,
    val totalAmountDa: Int,
    val status: String, // "Nouvelle commande", "À confirmer", "Confirmée", "En préparation", "Prête", "Expédiée", "Livrée", "Annulée"
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: String,
    val productId: String,
    val productName: String,
    val unitPriceDa: Int,
    val quantity: Int,
    val totalItemDa: Int,
    val selectedOptionsSummary: String,
    val customDimensionsSummary: String,
    val customText: String = "",
    val university: String = "",
    val specialty: String = ""
)

data class OrderWithItems(
    val order: OrderEntity,
    val items: List<OrderItemEntity>
)

data class CartItem(
    val cartId: String = java.util.UUID.randomUUID().toString(),
    val productId: String,
    val productName: String,
    val category: String,
    val basePriceDa: Int,
    val selectedOptions: Map<String, ProductOptionEntity> = emptyMap(), // groupType -> Option
    val surchargesTotalDa: Int = 0,
    val unitPriceDa: Int, // base + surcharges
    val quantity: Int = 1,
    // Custom dimensions & text
    val isCustomDimension: Boolean = false,
    val hatCircumferenceCm: String = "",
    val hatWidthCm: String = "24",
    val hatLengthCm: String = "24",
    val scarfLengthCm: String = "150",
    val scarfWidthCm: String = "14",
    val scarfCustomText: String = "",
    val scarfUniversity: String = "",
    val scarfSpecialty: String = "",
    val jacketSize: String = "M",
    val jacketLengthCm: String = "",
    val jacketBustCm: String = "",
    val jacketSleeveCm: String = "",
    val packSelectionsSummary: String = ""
) {
    val totalItemPriceDa: Int get() = unitPriceDa * quantity

    fun getOptionsSummary(): String {
        val parts = mutableListOf<String>()
        selectedOptions.values.forEach { opt ->
            if (opt.surchargeDa > 0) {
                parts.add("${opt.groupLabel}: ${opt.optionName} (+${opt.surchargeDa} DA)")
            } else {
                parts.add("${opt.groupLabel}: ${opt.optionName}")
            }
        }
        if (packSelectionsSummary.isNotBlank()) {
            parts.add(packSelectionsSummary)
        }
        return parts.joinToString(" • ")
    }

    fun getDimensionsSummary(): String {
        val parts = mutableListOf<String>()
        when (category) {
            "CHAPEAU" -> {
                if (isCustomDimension && hatCircumferenceCm.isNotBlank()) {
                    parts.add("Tour de tête sur-mesure: ${hatCircumferenceCm}cm")
                } else {
                    parts.add("Taille standard (${hatLengthCm}x${hatWidthCm}cm)")
                }
            }
            "ECHARPE" -> {
                parts.add("Dimensions: ${scarfLengthCm}x${scarfWidthCm}cm")
                if (scarfCustomText.isNotBlank()) parts.add("Texte brodé: \"$scarfCustomText\"")
                if (scarfUniversity.isNotBlank()) parts.add("Univ: $scarfUniversity")
                if (scarfSpecialty.isNotBlank()) parts.add("Spécialité: $scarfSpecialty")
            }
            "JACKET" -> {
                if (isCustomDimension) {
                    parts.add("Sur mesure (Long: ${jacketLengthCm}cm, Poitrine: ${jacketBustCm}cm, Manches: ${jacketSleeveCm}cm)")
                } else {
                    parts.add("Taille standard: $jacketSize")
                }
            }
            "PACK" -> {
                if (isCustomDimension) {
                    parts.add("Options personnalisées incluses")
                }
            }
        }
        return parts.joinToString(" • ")
    }
}
