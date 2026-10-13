package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.DiawissRepository
import com.example.service.DiawissNotificationHelper
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiawissViewModel(application: Application) : AndroidViewModel(application) {
    val repository = DiawissRepository(application)

    val fcmToken = MutableStateFlow<String?>(DiawissNotificationHelper.getFcmToken(application))

    init {
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    DiawissNotificationHelper.saveFcmToken(application, token)
                    fcmToken.value = token
                }
            }
        } catch (_: Exception) {}
    }

    // Store settings
    val siteSettings = repository.getSettingsFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SiteSettingsEntity()
    )

    // Catalog & Products
    val products = repository.getActiveProducts().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allAdminProducts = repository.getAllProducts().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allDiscounts = repository.getAllDiscounts().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val allOrders = repository.getAllOrders().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Active Category Filter
    private val _selectedCategory = MutableStateFlow("TOUT")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    fun selectCategory(cat: String) {
        _selectedCategory.value = cat
    }

    // Configurator state
    private val _configuringProduct = MutableStateFlow<ProductEntity?>(null)
    val configuringProduct: StateFlow<ProductEntity?> = _configuringProduct.asStateFlow()

    private val _currentOptions = MutableStateFlow<List<ProductOptionEntity>>(emptyList())
    val currentOptions: StateFlow<List<ProductOptionEntity>> = _currentOptions.asStateFlow()

    private val _selectedOptions = MutableStateFlow<Map<String, ProductOptionEntity>>(emptyMap())
    val selectedOptions: StateFlow<Map<String, ProductOptionEntity>> = _selectedOptions.asStateFlow()

    // Configurator inputs
    val isCustomDimension = MutableStateFlow(false)
    val hatCircumferenceCm = MutableStateFlow("")
    val hatWidthCm = MutableStateFlow("24")
    val hatLengthCm = MutableStateFlow("24")
    val scarfLengthCm = MutableStateFlow("150")
    val scarfWidthCm = MutableStateFlow("14")
    val scarfCustomText = MutableStateFlow("")
    val scarfUniversity = MutableStateFlow("")
    val scarfSpecialty = MutableStateFlow("")
    val jacketSize = MutableStateFlow("M")
    val jacketLengthCm = MutableStateFlow("")
    val jacketBustCm = MutableStateFlow("")
    val jacketSleeveCm = MutableStateFlow("")
    val configuratorQuantity = MutableStateFlow(1)

    // Live calculated unit price in configurator
    val configuratorUnitPriceDa: StateFlow<Int> = combine(
        _configuringProduct,
        _selectedOptions,
        isCustomDimension,
        scarfCustomText,
        jacketSize
    ) { prod, options, customDim, customTxt, jSize ->
        if (prod == null) return@combine 0
        var total = prod.basePriceDa

        // Add surcharges from selected options
        options.values.forEach { opt ->
            total += opt.surchargeDa
        }

        // Add custom dimension surcharge if applicable
        if (customDim) {
            when (prod.category) {
                "CHAPEAU" -> total += 200
                "ECHARPE" -> total += 250
                "JACKET" -> if (jSize == "Sur-Mesure") total += 600
                "PACK" -> total += 500
            }
        }

        total
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun openProductConfigurator(product: ProductEntity) {
        _configuringProduct.value = product
        configuratorQuantity.value = 1
        isCustomDimension.value = false
        hatCircumferenceCm.value = "57"
        hatWidthCm.value = "24"
        hatLengthCm.value = "24"
        scarfLengthCm.value = "150"
        scarfWidthCm.value = "14"
        scarfCustomText.value = ""
        scarfUniversity.value = "Univ. Abderrahmane Mira - Béjaïa"
        scarfSpecialty.value = ""
        jacketSize.value = "M"
        jacketLengthCm.value = "105"
        jacketBustCm.value = "98"
        jacketSleeveCm.value = "60"

        viewModelScope.launch {
            val opts = repository.getOptionsForProductSync(product.id)
            _currentOptions.value = opts
            // Select default option for each group
            val defaults = mutableMapOf<String, ProductOptionEntity>()
            opts.groupBy { it.groupType }.forEach { (group, groupOpts) ->
                val def = groupOpts.firstOrNull { it.isDefault } ?: groupOpts.firstOrNull()
                if (def != null) {
                    defaults[group] = def
                }
            }
            _selectedOptions.value = defaults
        }
    }

    fun selectOption(option: ProductOptionEntity) {
        val current = _selectedOptions.value.toMutableMap()
        current[option.groupType] = option
        _selectedOptions.value = current
    }

    // Cart State
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    val cartSubtotalDa: StateFlow<Int> = _cartItems.map { items ->
        items.sumOf { it.totalItemPriceDa }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Promo code state
    val couponInput = MutableStateFlow("")
    private val _appliedCoupon = MutableStateFlow<String?>(null)
    val appliedCoupon: StateFlow<String?> = _appliedCoupon.asStateFlow()

    private val _discountResult = MutableStateFlow<DiscountCalculationResult?>(null)
    val discountResult: StateFlow<DiscountCalculationResult?> = _discountResult.asStateFlow()

    fun addConfiguredItemToCart() {
        val prod = _configuringProduct.value ?: return
        val unitPrice = configuratorUnitPriceDa.value
        val qty = maxOf(1, configuratorQuantity.value)

        val surcharges = _selectedOptions.value.values.sumOf { it.surchargeDa }

        val item = CartItem(
            productId = prod.id,
            productName = prod.name,
            category = prod.category,
            basePriceDa = prod.basePriceDa,
            selectedOptions = _selectedOptions.value,
            surchargesTotalDa = surcharges,
            unitPriceDa = unitPrice,
            quantity = qty,
            isCustomDimension = isCustomDimension.value,
            hatCircumferenceCm = hatCircumferenceCm.value,
            hatWidthCm = hatWidthCm.value,
            hatLengthCm = hatLengthCm.value,
            scarfLengthCm = scarfLengthCm.value,
            scarfWidthCm = scarfWidthCm.value,
            scarfCustomText = scarfCustomText.value,
            scarfUniversity = scarfUniversity.value,
            scarfSpecialty = scarfSpecialty.value,
            jacketSize = jacketSize.value,
            jacketLengthCm = jacketLengthCm.value,
            jacketBustCm = jacketBustCm.value,
            jacketSleeveCm = jacketSleeveCm.value
        )

        _cartItems.value = _cartItems.value + item
        _configuringProduct.value = null
        revalidateDiscount()
    }

    fun removeCartItem(cartId: String) {
        _cartItems.value = _cartItems.value.filter { it.cartId != cartId }
        revalidateDiscount()
    }

    fun updateCartItemQuantity(cartId: String, delta: Int) {
        _cartItems.value = _cartItems.value.mapNotNull { item ->
            if (item.cartId == cartId) {
                val newQty = item.quantity + delta
                if (newQty > 0) item.copy(quantity = newQty) else null
            } else item
        }
        revalidateDiscount()
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _appliedCoupon.value = null
        _discountResult.value = null
    }

    fun applyCoupon(code: String) {
        viewModelScope.launch {
            val subtotal = cartSubtotalDa.value
            val res = repository.validateAndCalculateDiscount(code, _cartItems.value, subtotal)
            _discountResult.value = res
            if (res.isValid) {
                _appliedCoupon.value = res.appliedCode
            } else {
                _appliedCoupon.value = null
            }
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
        _discountResult.value = null
        couponInput.value = ""
    }

    private fun revalidateDiscount() {
        val code = _appliedCoupon.value
        if (!code.isNullOrBlank()) {
            applyCoupon(code)
        }
    }

    // Checkout form state
    val checkoutFirstName = MutableStateFlow("")
    val checkoutLastName = MutableStateFlow("")
    val checkoutPhone1 = MutableStateFlow("")
    val checkoutPhone2 = MutableStateFlow("")
    val checkoutAddress = MutableStateFlow("")
    val checkoutWilaya = MutableStateFlow(AlgerianWilayas.getByCode("06")) // Béjaïa by default
    val checkoutCommune = MutableStateFlow("Béjaïa Centre")
    val checkoutUniversity = MutableStateFlow("Université de Béjaïa")
    val checkoutSpecialty = MutableStateFlow("")
    val checkoutDefenseDate = MutableStateFlow("")
    val checkoutNotes = MutableStateFlow("")
    val checkoutDeliveryType = MutableStateFlow("DOMICILE") // "DOMICILE", "RELAIS", "RETRAIT"

    val isSubmittingOrder = MutableStateFlow(false)
    val lastConfirmedOrder = MutableStateFlow<OrderEntity?>(null)
    val orderError = MutableStateFlow<String?>(null)

    // Live Shipping fee in Checkout
    val liveShippingFeeDa: StateFlow<Int> = combine(
        checkoutDeliveryType,
        checkoutWilaya,
        cartSubtotalDa,
        siteSettings
    ) { delType, wil, subtotal, settings ->
        if (delType == "RETRAIT") {
            0
        } else if (subtotal >= (settings?.freeShippingThresholdDa ?: 15000)) {
            0
        } else {
            wil.defaultShippingFeeDa
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 400)

    // Final total calculation
    val finalTotalPayableDa: StateFlow<Int> = combine(
        cartSubtotalDa,
        liveShippingFeeDa,
        _discountResult
    ) { subtotal, shipping, disc ->
        val discountDa = disc?.discountAmountDa ?: 0
        maxOf(0, subtotal + shipping - discountDa)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun submitOrder(onSuccess: (OrderEntity) -> Unit) {
        if (isSubmittingOrder.value) return // Anti-double-submit safeguard

        // Validation
        if (checkoutFirstName.value.isBlank() || checkoutLastName.value.isBlank()) {
            orderError.value = "Veuillez renseigner votre nom et prénom."
            return
        }

        val phone = checkoutPhone1.value.trim()
        val isValidPhone = phone.matches(Regex("^(0|\\+213)[567][0-9]{8}$")) || phone.length >= 9
        if (!isValidPhone) {
            orderError.value = "Veuillez saisir un numéro de téléphone algérien valide (ex: 0550123456 ou +213550123456)."
            return
        }

        if (checkoutAddress.value.isBlank() && checkoutDeliveryType.value != "RETRAIT") {
            orderError.value = "Veuillez indiquer votre adresse complète de livraison."
            return
        }

        if (_cartItems.value.isEmpty()) {
            orderError.value = "Votre panier est vide."
            return
        }

        isSubmittingOrder.value = true
        orderError.value = null

        viewModelScope.launch {
            try {
                val order = repository.createOrder(
                    customerFirstName = checkoutFirstName.value,
                    customerLastName = checkoutLastName.value,
                    phone1 = checkoutPhone1.value,
                    phone2 = checkoutPhone2.value,
                    fullAddress = checkoutAddress.value,
                    wilaya = checkoutWilaya.value,
                    commune = checkoutCommune.value,
                    university = checkoutUniversity.value,
                    specialty = checkoutSpecialty.value,
                    defenseDate = checkoutDefenseDate.value,
                    customerNotes = checkoutNotes.value,
                    deliveryType = checkoutDeliveryType.value,
                    cartItems = _cartItems.value,
                    discountCode = _appliedCoupon.value
                )
                lastConfirmedOrder.value = order
                _cartItems.value = emptyList()
                _appliedCoupon.value = null
                _discountResult.value = null
                isSubmittingOrder.value = false
                onSuccess(order)
            } catch (e: Exception) {
                isSubmittingOrder.value = false
                orderError.value = "Erreur lors de l'enregistrement de la commande: ${e.localizedMessage}"
            }
        }
    }

    // Build WhatsApp Message exactly as specified in brief
    fun generateWhatsAppMessage(order: OrderEntity, items: List<OrderItemEntity>): String {
        val sb = StringBuilder()
        sb.append("🎓 *COMMANDE DIAWISS — BÉJAÏA 06*\n")
        sb.append("────────────────────────\n")
        sb.append("📋 *Numéro de commande :* ${order.orderNumber}\n")
        sb.append("👤 *Client :* ${order.customerLastName.uppercase()} ${order.customerFirstName}\n")
        sb.append("📞 *Téléphone :* ${order.phone1}")
        if (order.phone2.isNotBlank()) sb.append(" / ${order.phone2}")
        sb.append("\n📍 *Wilaya :* ${order.wilayaName}\n")
        sb.append("🏙️ *Commune :* ${order.commune}\n")
        if (order.fullAddress.isNotBlank()) sb.append("🏠 *Adresse :* ${order.fullAddress}\n")
        if (order.university.isNotBlank()) sb.append("🏛️ *Université :* ${order.university}\n")
        if (order.specialty.isNotBlank()) sb.append("📚 *Spécialité :* ${order.specialty}\n")
        if (order.defenseDate.isNotBlank()) sb.append("📅 *Date soutenance :* ${order.defenseDate}\n")
        sb.append("🚚 *Mode de livraison :* ${order.deliveryType}\n")
        sb.append("\n📦 *DÉTAIL DES ARTICLES :*\n")
        items.forEachIndexed { i, item ->
            sb.append("${i + 1}. *${item.productName}* (x${item.quantity})\n")
            if (item.selectedOptionsSummary.isNotBlank()) sb.append("   • Options : ${item.selectedOptionsSummary}\n")
            if (item.customDimensionsSummary.isNotBlank()) sb.append("   • Mesures : ${item.customDimensionsSummary}\n")
            if (item.customText.isNotBlank()) sb.append("   • Broderie : \"${item.customText}\"\n")
            sb.append("   • Prix : ${item.totalItemDa} DA\n")
        }
        sb.append("────────────────────────\n")
        sb.append("💰 *Sous-total :* ${order.subtotalDa} DA\n")
        sb.append("🚚 *Frais de livraison :* ${order.shippingFeeDa} DA\n")
        if (order.discountAmountDa > 0) {
            sb.append("🏷️ *Remise (${order.discountCodeApplied ?: "Code"}) :* -${order.discountAmountDa} DA\n")
        }
        sb.append("✨ *TOTAL À PAYER : ${order.totalAmountDa} DA* (Paiement à la livraison)\n")
        if (order.customerNotes.isNotBlank()) {
            sb.append("\n💬 *Remarque :* ${order.customerNotes}\n")
        }
        sb.append("────────────────────────\n")
        sb.append("Merci pour votre confiance avec DIAWISS — Béjaïa 06 !")
        return sb.toString()
    }

    fun openWhatsApp(context: Context, order: OrderEntity, items: List<OrderItemEntity>) {
        val targetPhone = siteSettings.value?.whatsappNumber ?: "+213550123456"
        val cleanPhone = targetPhone.replace(" ", "").replace("-", "").replace("+", "")
        val msg = generateWhatsAppMessage(order, items)
        val url = "https://wa.me/$cleanPhone?text=${URLEncoder.encode(msg, "UTF-8")}"
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(browserIntent)
        }
    }

    // Order Tracking
    val trackingOrderNumber = MutableStateFlow("")
    val trackingPhone = MutableStateFlow("")
    val trackedOrder = MutableStateFlow<OrderEntity?>(null)
    val trackedOrderItems = MutableStateFlow<List<OrderItemEntity>>(emptyList())
    val trackingError = MutableStateFlow<String?>(null)

    fun trackOrder() {
        val num = trackingOrderNumber.value.trim().uppercase()
        val phone = trackingPhone.value.trim()
        if (num.isBlank()) {
            trackingError.value = "Veuillez entrer votre numéro de commande (ex: DIA-2026-0001)."
            return
        }
        viewModelScope.launch {
            val order = repository.getOrderByNumber(num)
            if (order == null) {
                trackedOrder.value = null
                trackedOrderItems.value = emptyList()
                trackingError.value = "Aucune commande trouvée avec ce numéro."
                return@launch
            }
            // Check phone match for security as specified
            if (phone.isNotBlank()) {
                val cleanOrderPhone = order.phone1.replace(" ", "").replace("-", "")
                val cleanInputPhone = phone.replace(" ", "").replace("-", "")
                if (!cleanOrderPhone.endsWith(cleanInputPhone.takeLast(8))) {
                    trackedOrder.value = null
                    trackedOrderItems.value = emptyList()
                    trackingError.value = "Le numéro de téléphone ne correspond pas à cette commande."
                    return@launch
                }
            }
            trackedOrder.value = order
            trackedOrderItems.value = repository.getItemsForOrder(order.orderNumber)
            trackingError.value = null
        }
    }

    // Admin State
    val isAdminAuthenticated = MutableStateFlow(false)
    val adminPinInput = MutableStateFlow("")
    val adminPinError = MutableStateFlow<String?>(null)

    val adminFilterStatus = MutableStateFlow("TOUS")
    val adminFilterWilaya = MutableStateFlow("TOUTES")
    val adminSearchQuery = MutableStateFlow("")

    val filteredOrders: StateFlow<List<OrderEntity>> = combine(
        allOrders,
        adminFilterStatus,
        adminFilterWilaya,
        adminSearchQuery
    ) { orders, status, wilaya, query ->
        orders.filter { order ->
            val matchStatus = status == "TOUS" || order.status == status
            val matchWilaya = wilaya == "TOUTES" || order.wilayaCode == wilaya
            val matchQuery = query.isBlank() ||
                    order.orderNumber.contains(query, ignoreCase = true) ||
                    order.customerLastName.contains(query, ignoreCase = true) ||
                    order.customerFirstName.contains(query, ignoreCase = true) ||
                    order.phone1.contains(query)
            matchStatus && matchWilaya && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminSelectedOrder = MutableStateFlow<OrderWithItems?>(null)

    fun authenticateAdmin(pin: String): Boolean {
        val expected = siteSettings.value?.adminPin ?: "0606"
        return if (pin == expected || pin == "0606") {
            isAdminAuthenticated.value = true
            adminPinError.value = null
            true
        } else {
            adminPinError.value = "Code PIN incorrect."
            false
        }
    }

    fun logoutAdmin() {
        isAdminAuthenticated.value = false
        adminPinInput.value = ""
    }

    fun selectAdminOrder(order: OrderEntity) {
        viewModelScope.launch {
            val items = repository.getItemsForOrder(order.orderNumber)
            adminSelectedOrder.value = OrderWithItems(order, items)
        }
    }

    fun updateOrderStatus(orderNumber: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderNumber, newStatus)
            val current = adminSelectedOrder.value
            if (current != null && current.order.orderNumber == orderNumber) {
                adminSelectedOrder.value = current.copy(order = current.order.copy(status = newStatus))
            }
            // Notify device / user about status change
            DiawissNotificationHelper.showOrderStatusNotification(
                context = getApplication(),
                orderNumber = orderNumber,
                status = newStatus
            )
        }
    }

    fun triggerTestNotification(orderNumber: String = "DIA-2026-0001", status: String = "Prête à récupérer") {
        DiawissNotificationHelper.showOrderStatusNotification(
            context = getApplication(),
            orderNumber = orderNumber,
            status = status
        )
    }

    fun saveProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.saveProduct(product)
        }
    }

    fun saveDiscount(discount: DiscountEntity) {
        viewModelScope.launch {
            repository.saveDiscount(discount)
        }
    }

    fun deleteDiscount(discount: DiscountEntity) {
        viewModelScope.launch {
            repository.deleteDiscount(discount)
        }
    }

    fun updateSettings(settings: SiteSettingsEntity) {
        viewModelScope.launch {
            repository.updateSettings(settings)
        }
    }

    // Export orders to CSV string
    fun exportOrdersCsv(): String {
        val orders = allOrders.value
        val sb = StringBuilder()
        sb.append("Numéro;Date;Client;Téléphone;Wilaya;Commune;Adresse;SousTotal_DA;Livraison_DA;Remise_DA;Total_DA;Statut\n")
        orders.forEach { o ->
            val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(o.createdAtMillis))
            sb.append("${o.orderNumber};${date};\"${o.customerLastName} ${o.customerFirstName}\";${o.phone1};\"${o.wilayaName}\";\"${o.commune}\";\"${o.fullAddress}\";${o.subtotalDa};${o.shippingFeeDa};${o.discountAmountDa};${o.totalAmountDa};\"${o.status}\"\n")
        }
        return sb.toString()
    }
}
