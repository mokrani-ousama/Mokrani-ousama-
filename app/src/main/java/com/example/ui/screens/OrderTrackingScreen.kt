package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.DiawissViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    viewModel: DiawissViewModel,
    modifier: Modifier = Modifier
) {
    val orderNumberInput by viewModel.trackingOrderNumber.collectAsState()
    val phoneInput by viewModel.trackingPhone.collectAsState()
    val trackedOrder by viewModel.trackedOrder.collectAsState()
    val trackedItems by viewModel.trackedOrderItems.collectAsState()
    val trackingError by viewModel.trackingError.collectAsState()

    val steps = listOf(
        "Nouvelle commande",
        "Confirmée",
        "En préparation",
        "Prête",
        "Expédiée",
        "Livrée"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Suivi de Commande DIAWISS", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DiawissWhite) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DiawissNavy)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(DiawissOffWhite)
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Input Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DiawissWhite),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Consultez l'avancement de votre tenue",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = DiawissNavy
                        )
                        Text(
                            text = "Entrez votre numéro de commande et votre numéro de téléphone",
                            fontSize = 12.sp,
                            color = DiawissTextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = orderNumberInput,
                            onValueChange = { viewModel.trackingOrderNumber.value = it },
                            label = { Text("Numéro de commande") },
                            placeholder = { Text("Ex: DIA-2026-0001") },
                            leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null, tint = DiawissGoldDark) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_tracking_number"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { viewModel.trackingPhone.value = it },
                            label = { Text("Numéro de téléphone de confirmation") },
                            placeholder = { Text("Ex: 0550 12 34 56") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = DiawissGoldDark) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_tracking_phone"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { viewModel.trackOrder() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DiawissNavy,
                                contentColor = DiawissWhite
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_search_tracking")
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = DiawissGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Rechercher ma commande", fontWeight = FontWeight.Bold)
                        }

                        if (trackingError != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = trackingError ?: "",
                                color = DiawissDanger,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Results Section if order found
            trackedOrder?.let { order ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DiawissWhite),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = order.orderNumber,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = DiawissNavy
                                    )
                                    val dateStr = SimpleDateFormat("dd MMMM yyyy à HH:mm", Locale.FRENCH).format(Date(order.createdAtMillis))
                                    Text(text = "Commandé le $dateStr", fontSize = 11.sp, color = DiawissTextSecondary)
                                }

                                Surface(
                                    color = when (order.status) {
                                        "Livrée" -> DiawissSuccess.copy(alpha = 0.15f)
                                        "Annulée" -> DiawissDanger.copy(alpha = 0.15f)
                                        else -> DiawissGoldContainer
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = order.status,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (order.status) {
                                            "Livrée" -> DiawissSuccess
                                            "Annulée" -> DiawissDanger
                                            else -> DiawissNavyDark
                                        },
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DiawissBorder)

                            // Status Step Tracker
                            Text(text = "Étape de confection & livraison :", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DiawissNavy)
                            Spacer(modifier = Modifier.height(10.dp))

                            val currentIndex = steps.indexOf(order.status).let { if (it >= 0) it else 0 }
                            steps.forEachIndexed { idx, stp ->
                                val isDone = idx <= currentIndex
                                val isCurrent = idx == currentIndex

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isDone) DiawissGold else DiawissSurfaceVariant
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isDone) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = null,
                                                tint = DiawissNavyDark,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = stp,
                                        fontSize = 12.sp,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isCurrent) DiawissNavy else if (isDone) DiawissNavyLight else DiawissTextSecondary
                                    )
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DiawissBorder)

                            // Items in Order
                            Text(text = "Articles confectionnés :", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DiawissNavy)
                            Spacer(modifier = Modifier.height(6.dp))

                            trackedItems.forEach { itm ->
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "${itm.quantity}x ${itm.productName}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DiawissNavy)
                                        Text(text = "${itm.totalItemDa} DA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DiawissGoldDark)
                                    }
                                    if (itm.selectedOptionsSummary.isNotBlank()) {
                                        Text(text = "Options: ${itm.selectedOptionsSummary}", fontSize = 11.sp, color = DiawissTextSecondary)
                                    }
                                    if (itm.customDimensionsSummary.isNotBlank()) {
                                        Text(text = "Mesures: ${itm.customDimensionsSummary}", fontSize = 11.sp, color = DiawissGoldDark)
                                    }
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = DiawissBorder)

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Lieu de livraison :", fontSize = 12.sp, color = DiawissTextSecondary)
                                Text("${order.wilayaName} (${order.commune})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DiawissNavy)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Montant total :", fontSize = 12.sp, color = DiawissTextSecondary)
                                Text("${order.totalAmountDa} DA", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = DiawissGoldDark)
                            }
                        }
                    }
                }
            }
        }
    }
}
