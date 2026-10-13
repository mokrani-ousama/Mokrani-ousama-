package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProductEntity::class,
        ProductOptionEntity::class,
        DiscountEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        SiteSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DiawissDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun discountDao(): DiscountDao
    abstract fun orderDao(): OrderDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: DiawissDatabase? = null

        fun getInstance(context: Context): DiawissDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DiawissDatabase::class.java,
                    "diawiss_database.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(getInstance(context))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialData(database: DiawissDatabase) {
            val productDao = database.productDao()
            val discountDao = database.discountDao()
            val settingsDao = database.settingsDao()

            // 1. Initial Products
            val initialProducts = listOf(
                ProductEntity(
                    id = "pack_complet",
                    name = "Pack Complet DIAWISS (Chapeau + Écharpe + Jacket)",
                    category = "PACK",
                    description = "La panoplie complète pour briller lors de votre soutenance. Comprend le chapeau avec pompon doré, l'écharpe brodée sur mesure et le jacket académique.",
                    basePriceDa = 7500,
                    drawableName = "img_hero_banner",
                    isPack = true,
                    isActive = true,
                    allowsCustomDimensions = true,
                    badge = "Le Plus Populaire"
                ),
                ProductEntity(
                    id = "pack_duo",
                    name = "Pack Duo Élégance (Chapeau + Écharpe)",
                    category = "PACK",
                    description = "L'ensemble essentiel pour immortaliser votre remise de diplôme. Chapeau universitaire et écharpe coordonnée avec finitions dorées.",
                    basePriceDa = 3700,
                    drawableName = "img_echarpe",
                    isPack = true,
                    isActive = true,
                    allowsCustomDimensions = true,
                    badge = "Économique"
                ),
                ProductEntity(
                    id = "chapeau",
                    name = "Chapeau de Soutenance (Mortier Académique)",
                    category = "CHAPEAU",
                    description = "Chapeau carré traditionnel de remise de diplôme avec pompon doré métallisé. Confortable et ajustable.",
                    basePriceDa = 1800,
                    drawableName = "img_chapeau",
                    isPack = false,
                    isActive = true,
                    allowsCustomDimensions = true,
                    badge = null
                ),
                ProductEntity(
                    id = "echarpe",
                    name = "Écharpe de Soutenance Personnalisée",
                    category = "ECHARPE",
                    description = "Écharpe en satin soyeux haute qualité. Personnalisable avec vos couleurs, le nom de votre université, spécialité et broderie texte.",
                    basePriceDa = 2200,
                    drawableName = "img_echarpe",
                    isPack = false,
                    isActive = true,
                    allowsCustomDimensions = true,
                    badge = "Personnalisable"
                ),
                ProductEntity(
                    id = "jacket",
                    name = "Jacket & Robe de Soutenance",
                    category = "JACKET",
                    description = "Jacket académique raffiné avec revers satinés et finitions soignées. Disponible de la taille XS à 3XL ou confectionné sur mesure.",
                    basePriceDa = 4500,
                    drawableName = "img_jacket",
                    isPack = false,
                    isActive = true,
                    allowsCustomDimensions = true,
                    badge = "Haute Couture"
                )
            )
            productDao.insertProducts(initialProducts)

            // 2. Initial Variants and Options
            val initialOptions = listOf(
                // Chapeau options
                ProductOptionEntity(productId = "chapeau", groupType = "STYLE", groupLabel = "Style du Chapeau", optionName = "Classique Traditionnel", surchargeDa = 0, isDefault = true),
                ProductOptionEntity(productId = "chapeau", groupType = "STYLE", groupLabel = "Style du Chapeau", optionName = "Velours Royal", surchargeDa = 300, isDefault = false),
                ProductOptionEntity(productId = "chapeau", groupType = "STYLE", groupLabel = "Style du Chapeau", optionName = "Finition Broderie Prestige", surchargeDa = 500, isDefault = false),

                ProductOptionEntity(productId = "chapeau", groupType = "COLOR", groupLabel = "Couleur du Pompon", optionName = "Or Métallisé", surchargeDa = 0, isDefault = true, colorHex = "#D4AF37"),
                ProductOptionEntity(productId = "chapeau", groupType = "COLOR", groupLabel = "Couleur du Pompon", optionName = "Bleu Nuit Profond", surchargeDa = 0, isDefault = false, colorHex = "#0B1B3D"),
                ProductOptionEntity(productId = "chapeau", groupType = "COLOR", groupLabel = "Couleur du Pompon", optionName = "Bordeaux Cérémonie", surchargeDa = 100, isDefault = false, colorHex = "#800020"),
                ProductOptionEntity(productId = "chapeau", groupType = "COLOR", groupLabel = "Couleur du Pompon", optionName = "Blanc Neige", surchargeDa = 100, isDefault = false, colorHex = "#FFFFFF"),

                // Écharpe options
                ProductOptionEntity(productId = "echarpe", groupType = "COLOR", groupLabel = "Couleur de l'Écharpe", optionName = "Bleu Nuit & Or", surchargeDa = 0, isDefault = true, colorHex = "#0B1B3D"),
                ProductOptionEntity(productId = "echarpe", groupType = "COLOR", groupLabel = "Couleur de l'Écharpe", optionName = "Blanc Satin & Or", surchargeDa = 150, isDefault = false, colorHex = "#F8FAFC"),
                ProductOptionEntity(productId = "echarpe", groupType = "COLOR", groupLabel = "Couleur de l'Écharpe", optionName = "Bordeaux Impérial & Or", surchargeDa = 150, isDefault = false, colorHex = "#780016"),
                ProductOptionEntity(productId = "echarpe", groupType = "COLOR", groupLabel = "Couleur de l'Écharpe", optionName = "Vert Émeraude & Or", surchargeDa = 150, isDefault = false, colorHex = "#0F5132"),
                ProductOptionEntity(productId = "echarpe", groupType = "COLOR", groupLabel = "Couleur de l'Écharpe", optionName = "Noir Onyx & Or", surchargeDa = 150, isDefault = false, colorHex = "#111827"),

                ProductOptionEntity(productId = "echarpe", groupType = "LENGTH", groupLabel = "Longueur de l'Écharpe", optionName = "Standard 150 cm", surchargeDa = 0, isDefault = true),
                ProductOptionEntity(productId = "echarpe", groupType = "LENGTH", groupLabel = "Longueur de l'Écharpe", optionName = "Grande Longueur 180 cm", surchargeDa = 200, isDefault = false),

                ProductOptionEntity(productId = "echarpe", groupType = "EMBROIDERY", groupLabel = "Broderie Personnalisée", optionName = "Sans texte brodé", surchargeDa = 0, isDefault = true),
                ProductOptionEntity(productId = "echarpe", groupType = "EMBROIDERY", groupLabel = "Broderie Personnalisée", optionName = "Broderie Nom + Titre / Diplôme", surchargeDa = 450, isDefault = false),

                // Jacket options
                ProductOptionEntity(productId = "jacket", groupType = "STYLE", groupLabel = "Modèle Jacket", optionName = "Revers Satin Classique", surchargeDa = 0, isDefault = true),
                ProductOptionEntity(productId = "jacket", groupType = "STYLE", groupLabel = "Modèle Jacket", optionName = "Modèle Prestige avec Galons Dorés", surchargeDa = 600, isDefault = false),

                ProductOptionEntity(productId = "jacket", groupType = "SIZE", groupLabel = "Taille Jacket", optionName = "XS", surchargeDa = 0, isDefault = false),
                ProductOptionEntity(productId = "jacket", groupType = "SIZE", groupLabel = "Taille Jacket", optionName = "S", surchargeDa = 0, isDefault = false),
                ProductOptionEntity(productId = "jacket", groupType = "SIZE", groupLabel = "Taille Jacket", optionName = "M", surchargeDa = 0, isDefault = true),
                ProductOptionEntity(productId = "jacket", groupType = "SIZE", groupLabel = "Taille Jacket", optionName = "L", surchargeDa = 0, isDefault = false),
                ProductOptionEntity(productId = "jacket", groupType = "SIZE", groupLabel = "Taille Jacket", optionName = "XL", surchargeDa = 0, isDefault = false),
                ProductOptionEntity(productId = "jacket", groupType = "SIZE", groupLabel = "Taille Jacket", optionName = "XXL", surchargeDa = 250, isDefault = false),
                ProductOptionEntity(productId = "jacket", groupType = "SIZE", groupLabel = "Taille Jacket", optionName = "XXXL", surchargeDa = 450, isDefault = false),
                ProductOptionEntity(productId = "jacket", groupType = "SIZE", groupLabel = "Taille Jacket", optionName = "Sur-Mesure (Confection Atelier)", surchargeDa = 600, isDefault = false)
            )
            productDao.insertOptions(initialOptions)

            // 3. Initial Discounts & Promo Codes
            val initialDiscounts = listOf(
                DiscountEntity(
                    code = "SOUTENANCE2026",
                    description = "-10% sur toute la commande (Spécial Diplôme 2026)",
                    discountType = "PERCENTAGE",
                    value = 10.0,
                    targetScope = "ORDER",
                    minOrderDa = 3500,
                    isActive = true
                ),
                DiscountEntity(
                    code = "BEJAIA06",
                    description = "Remise de 500 DA (Offre spéciale Béjaïa 06)",
                    discountType = "FIXED",
                    value = 500.0,
                    targetScope = "ORDER",
                    minOrderDa = 3000,
                    isActive = true
                ),
                DiscountEntity(
                    code = "PACK15",
                    description = "-15% sur les Packs Complets et Duo",
                    discountType = "PERCENTAGE",
                    value = 15.0,
                    targetScope = "CATEGORY",
                    targetScopeValue = "PACK",
                    minOrderDa = 3500,
                    isActive = true
                ),
                DiscountEntity(
                    code = "BIENVENUE",
                    description = "300 DA de réduction immédiate pour votre première commande",
                    discountType = "FIXED",
                    value = 300.0,
                    targetScope = "ORDER",
                    minOrderDa = 2000,
                    isActive = true
                )
            )
            discountDao.insertDiscounts(initialDiscounts)

            // 4. Site Settings
            settingsDao.insertOrUpdate(SiteSettingsEntity())
        }
    }
}
