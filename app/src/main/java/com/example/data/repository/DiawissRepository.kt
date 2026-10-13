package com.example.data.repository

import android.content.Context
import com.example.data.local.DiawissDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiawissRepository(context: Context) {
    private val db = DiawissDatabase.getInstance(context)
    private val productDao = db.productDao()
    private val discountDao = db.discountDao()
    private val orderDao = db.orderDao()
    private val settingsDao = db.settingsDao()

    init {
        // Ensure initial seeds are populated if empty
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launchIfEmpty()
    }

    private fun kotlinx.coroutines.CoroutineScope.launchIfEmpty() {
        this.launch(kotlinx.coroutines.Dispatchers.IO) {
            val count = productDao.getProductById("chapeau")
            if (count == null) {
                DiawissDatabase.populateInitialData(db)
            }
        }
    }

    // Products
    fun getActiveProducts(): Flow<List<ProductEntity>> = productDao.getActiveProducts()
    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()
    fun getOptionsForProduct(productId: String): Flow<List<ProductOptionEntity>> = productDao.getOptionsForProduct(productId)
    fun getAllOptions(): Flow<List<ProductOptionEntity>> = productDao.getAllOptions()

    suspend fun getProductById(id: String): ProductEntity? = productDao.getProductById(id)
    suspend fun getOptionsForProductSync(productId: String): List<ProductOptionEntity> = productDao.getOptionsForProductSync(productId)

    suspend fun saveProduct(product: ProductEntity) = productDao.insertProduct(product)
    suspend fun deleteProduct(product: ProductEntity) = productDao.deleteProduct(product)
    suspend fun saveOption(option: ProductOptionEntity) = productDao.insertOption(option)
    suspend fun deleteOption(option: ProductOptionEntity) = productDao.deleteOption(option)

    // Discounts
    fun getAllDiscounts(): Flow<List<DiscountEntity>> = discountDao.getAllDiscounts()
    suspend fun saveDiscount(discount: DiscountEntity) = discountDao.insertDiscount(discount)
    suspend fun deleteDiscount(discount: DiscountEntity) = discountDao.deleteDiscount(discount)

    suspend fun validateAndCalculateDiscount(code: String, cartItems: List<CartItem>, subtotalDa: Int): DiscountCalculationResult {
        val trimmedCode = code.trim().uppercase()
        val discount = discountDao.getDiscountByCode(trimmedCode)
            ?: return DiscountCalculationResult(false, 0, null, "Code promo non reconnu ou invalide.")

        if (!discount.isActive) {
            return DiscountCalculationResult(false, 0, null, "Ce code promo n'est plus actif.")
        }

        if (subtotalDa < discount.minOrderDa) {
            return DiscountCalculationResult(false, 0, null, "Montant minimum requis : ${discount.minOrderDa} DA pour ce code.")
        }

        val discountAmount: Int = when (discount.targetScope) {
            "ORDER" -> {
                if (discount.discountType == "PERCENTAGE") {
                    ((subtotalDa * discount.value) / 100.0).toInt()
                } else {
                    discount.value.toInt()
                }
            }
            "CATEGORY" -> {
                val eligibleItemsSubtotal = cartItems
                    .filter { it.category.equals(discount.targetScopeValue, ignoreCase = true) }
                    .sumOf { it.totalItemPriceDa }

                if (eligibleItemsSubtotal <= 0) {
                    return DiscountCalculationResult(false, 0, null, "Ce code est réservé à la catégorie ${discount.targetScopeValue}.")
                }
                if (discount.discountType == "PERCENTAGE") {
                    ((eligibleItemsSubtotal * discount.value) / 100.0).toInt()
                } else {
                    minOf(discount.value.toInt(), eligibleItemsSubtotal)
                }
            }
            "PRODUCT" -> {
                val eligibleItemsSubtotal = cartItems
                    .filter { it.productId.equals(discount.targetScopeValue, ignoreCase = true) }
                    .sumOf { it.totalItemPriceDa }

                if (eligibleItemsSubtotal <= 0) {
                    return DiscountCalculationResult(false, 0, null, "Ce code s'applique uniquement à un produit spécifique.")
                }
                if (discount.discountType == "PERCENTAGE") {
                    ((eligibleItemsSubtotal * discount.value) / 100.0).toInt()
                } else {
                    minOf(discount.value.toInt(), eligibleItemsSubtotal)
                }
            }
            else -> 0
        }

        val finalDiscount = minOf(discountAmount, subtotalDa)
        return DiscountCalculationResult(true, finalDiscount, discount.code, null)
    }

    // Orders
    fun getAllOrders(): Flow<List<OrderEntity>> = orderDao.getAllOrders()
    suspend fun getOrderByNumber(orderNumber: String): OrderEntity? = orderDao.getOrderByNumber(orderNumber)
    suspend fun getItemsForOrder(orderNumber: String): List<OrderItemEntity> = orderDao.getItemsForOrder(orderNumber)

    suspend fun updateOrderStatus(orderNumber: String, newStatus: String) {
        orderDao.updateOrderStatus(orderNumber, newStatus)
    }

    suspend fun createOrder(
        customerFirstName: String,
        customerLastName: String,
        phone1: String,
        phone2: String,
        fullAddress: String,
        wilaya: Wilaya,
        commune: String,
        university: String,
        specialty: String,
        defenseDate: String,
        customerNotes: String,
        deliveryType: String,
        cartItems: List<CartItem>,
        discountCode: String?
    ): OrderEntity {
        val totalCount = orderDao.getOrdersCount()
        val nextSeq = totalCount + 1
        val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        val orderNumber = "DIA-$currentYear-%04d".format(nextSeq)

        val subtotalDa = cartItems.sumOf { it.totalItemPriceDa }
        val settings = settingsDao.getSettings() ?: SiteSettingsEntity()

        // Calculate delivery fee
        val rawShippingFee = if (deliveryType == "RETRAIT") {
            0
        } else if (subtotalDa >= settings.freeShippingThresholdDa) {
            0
        } else {
            wilaya.defaultShippingFeeDa
        }

        // Calculate discount
        var discountAmount = 0
        var appliedCode: String? = null
        if (!discountCode.isNullOrBlank()) {
            val res = validateAndCalculateDiscount(discountCode, cartItems, subtotalDa)
            if (res.isValid) {
                discountAmount = res.discountAmountDa
                appliedCode = res.appliedCode
                discountDao.incrementUsage(res.appliedCode ?: "")
            }
        }

        val finalTotal = maxOf(0, subtotalDa + rawShippingFee - discountAmount)

        val orderEntity = OrderEntity(
            orderNumber = orderNumber,
            customerFirstName = customerFirstName.trim(),
            customerLastName = customerLastName.trim(),
            phone1 = phone1.trim(),
            phone2 = phone2.trim(),
            fullAddress = fullAddress.trim(),
            wilayaCode = wilaya.code,
            wilayaName = wilaya.name,
            commune = commune.trim(),
            university = university.trim(),
            specialty = specialty.trim(),
            defenseDate = defenseDate.trim(),
            customerNotes = customerNotes.trim(),
            deliveryType = deliveryType,
            subtotalDa = subtotalDa,
            shippingFeeDa = rawShippingFee,
            discountAmountDa = discountAmount,
            discountCodeApplied = appliedCode,
            totalAmountDa = finalTotal,
            status = "Nouvelle commande",
            createdAtMillis = System.currentTimeMillis()
        )

        val orderItemEntities = cartItems.map { item ->
            OrderItemEntity(
                orderNumber = orderNumber,
                productId = item.productId,
                productName = item.productName,
                unitPriceDa = item.unitPriceDa,
                quantity = item.quantity,
                totalItemDa = item.totalItemPriceDa,
                selectedOptionsSummary = item.getOptionsSummary(),
                customDimensionsSummary = item.getDimensionsSummary(),
                customText = item.scarfCustomText,
                university = item.scarfUniversity,
                specialty = item.scarfSpecialty
            )
        }

        orderDao.insertOrder(orderEntity)
        orderDao.insertOrderItems(orderItemEntities)

        return orderEntity
    }

    // Settings
    fun getSettingsFlow(): Flow<SiteSettingsEntity?> = settingsDao.getSettingsFlow()
    suspend fun getSettings(): SiteSettingsEntity = settingsDao.getSettings() ?: SiteSettingsEntity()
    suspend fun updateSettings(settings: SiteSettingsEntity) = settingsDao.insertOrUpdate(settings)
}
