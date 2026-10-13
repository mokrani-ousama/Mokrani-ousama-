package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.service.DiawissNotificationHelper
import com.example.ui.components.DiawissBottomNav
import com.example.ui.components.DiawissTopBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.DiawissViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: DiawissViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DiawissNotificationHelper.createNotificationChannel(this)

        val navTarget = intent.getStringExtra("EXTRA_NAVIGATE_TO")
        val orderNum = intent.getStringExtra("EXTRA_ORDER_NUMBER")
        if (!orderNum.isNullOrBlank()) {
            viewModel.trackingOrderNumber.value = orderNum
            viewModel.trackOrder()
        }

        setContent {
            MyApplicationTheme {
                DiawissApp(
                    viewModel = viewModel,
                    initialRoute = if (navTarget == "tracking") "tracking" else "home"
                )
            }
        }
    }
}

@Composable
fun DiawissApp(
    viewModel: DiawissViewModel,
    initialRoute: String = "home"
) {
    val context = LocalContext.current
    var currentRoute by remember { mutableStateOf(initialRoute) }
    var routeBackStack by remember { mutableStateOf(listOf(initialRoute)) }

    // Request POST_NOTIFICATIONS permission on Android 13+ (API 33+)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionStatus = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val cartItems by viewModel.cartItems.collectAsState()
    val configuringProduct by viewModel.configuringProduct.collectAsState()
    var confirmedOrder by remember { mutableStateOf<OrderEntity?>(null) }

    fun navigateTo(newRoute: String) {
        if (newRoute != currentRoute) {
            routeBackStack = routeBackStack + newRoute
            currentRoute = newRoute
        }
    }

    fun navigateBack() {
        if (routeBackStack.size > 1) {
            val updatedStack = routeBackStack.dropLast(1)
            routeBackStack = updatedStack
            currentRoute = updatedStack.last()
        } else {
            currentRoute = "home"
        }
    }

    // Hardware and gesture back handler
    BackHandler(enabled = currentRoute != "home") {
        navigateBack()
    }

    val cartItemCount = cartItems.sumOf { it.quantity }
    val showBottomNav = currentRoute in listOf("home", "catalog", "cart", "tracking")
    val showTopBar = currentRoute in listOf("home", "catalog", "cart")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (showTopBar) {
                DiawissTopBar(
                    currentRoute = currentRoute,
                    cartItemCount = cartItemCount,
                    onNavigate = { route -> navigateTo(route) }
                )
            }
        },
        bottomBar = {
            if (showBottomNav) {
                DiawissBottomNav(
                    currentRoute = currentRoute,
                    cartItemCount = cartItemCount,
                    onNavigate = { route -> navigateTo(route) }
                )
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        when (currentRoute) {
            "home" -> {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToCatalog = { navigateTo("catalog") },
                    onConfigureProduct = { product ->
                        viewModel.openProductConfigurator(product)
                        navigateTo("configurator")
                    },
                    modifier = modifier
                )
            }

            "catalog" -> {
                CatalogScreen(
                    viewModel = viewModel,
                    onConfigureProduct = { product ->
                        viewModel.openProductConfigurator(product)
                        navigateTo("configurator")
                    },
                    modifier = modifier
                )
            }

            "configurator" -> {
                configuringProduct?.let { product ->
                    ProductConfiguratorScreen(
                        product = product,
                        viewModel = viewModel,
                        onBack = { navigateBack() },
                        onAddToCartSuccess = {
                            navigateTo("cart")
                        },
                        modifier = modifier
                    )
                } ?: run {
                    LaunchedEffect(Unit) {
                        navigateTo("catalog")
                    }
                }
            }

            "cart" -> {
                CartScreen(
                    viewModel = viewModel,
                    onNavigateToCatalog = { navigateTo("catalog") },
                    onNavigateToCheckout = { navigateTo("checkout") },
                    modifier = modifier
                )
            }

            "checkout" -> {
                CheckoutScreen(
                    viewModel = viewModel,
                    onBack = { navigateBack() },
                    onOrderPlaced = { order ->
                        confirmedOrder = order
                        navigateTo("confirmation")
                    },
                    modifier = modifier
                )
            }

            "confirmation" -> {
                confirmedOrder?.let { order ->
                    OrderConfirmationScreen(
                        order = order,
                        viewModel = viewModel,
                        onNavigateHome = {
                            routeBackStack = listOf("home")
                            currentRoute = "home"
                        },
                        onNavigateToTracking = {
                            viewModel.trackingOrderNumber.value = order.orderNumber
                            viewModel.trackingPhone.value = order.phone1
                            viewModel.trackOrder()
                            navigateTo("tracking")
                        },
                        modifier = modifier
                    )
                } ?: run {
                    LaunchedEffect(Unit) {
                        navigateTo("home")
                    }
                }
            }

            "tracking" -> {
                OrderTrackingScreen(
                    viewModel = viewModel,
                    modifier = modifier
                )
            }

            "admin" -> {
                AdminScreen(
                    viewModel = viewModel,
                    onBack = { navigateBack() },
                    modifier = modifier
                )
            }
        }
    }
}
