package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "site_settings")
data class SiteSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val whatsappNumber: String = "+213550123456", // Numéro WhatsApp entreprise DIAWISS
    val atelierAddress: String = "Cité Tobbal, Béjaïa 06000, Algérie",
    val atelierPhone: String = "0550 12 34 56",
    val promoBannerText: String = "🎓 Remise spéciale rentrée : -10% sur les Packs Complets avec le code SOUTENANCE2026",
    val freeShippingThresholdDa: Int = 15000, // Livraison gratuite à partir de 15000 DA
    val adminPin: String = "0606" // Code PIN sécurisé par défaut pour l'espace administration
)
