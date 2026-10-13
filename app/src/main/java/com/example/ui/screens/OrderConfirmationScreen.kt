package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.DiawissViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderConfirmationScreen(
    order: OrderEntity,
    viewModel: DiawissViewModel,
    onNavigateHome: () -> Unit,
    onNavigateToTracking: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var items by remember { mutableStateOf<List<OrderItemEntity>>(emptyList()) }

    LaunchedEffect(order.orderNumber) {
        items = viewModel.repository.getItemsForOrder(order.orderNumber)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmation DIAWISS", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DiawissWhite) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DiawissNavy)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(DiawissOffWhite)
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Checkmark Icon & Success Card
            item {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(DiawissGoldContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Succès",
                        tint = DiawissGoldDark,
                        modifier = Modifier.size(50.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Merci pour votre confiance !",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DiawissNavy,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Votre commande DIAWISS a bien été enregistrée.",
                    fontSize = 14.sp,
                    color = DiawissTextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            // Order Number Badge
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DiawissNavy),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "NUMÉRO DE COMMANDE UNIQUE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DiawissGold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = order.orderNumber,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = DiawissWhite,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Conservez ce numéro pour suivre votre commande.",
                            fontSize = 11.sp,
                            color = DiawissWhite.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // WhatsApp Share Button (Requirement 9)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DiawissWhite),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Accélérez le traitement de votre commande :",
                            fontSize = 12.sp,
                            color = DiawissTextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                viewModel.openWhatsApp(context, order, items)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF25D366),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_send_whatsapp_order")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Envoyer ma commande sur WhatsApp",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Pré-remplit vos articles, mesures et wilaya pour l'atelier.",
                            fontSize = 10.sp,
                            color = DiawissTextSecondary
                        )
                    }
                }
            }

            // Official Confirmation Text
            item {
                Surface(
                    color = DiawissGoldContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Notre équipe vous contactera pour confirmer les détails et les modalités de livraison.\n\nDIAWISS — Béjaïa 06.",
                            fontSize = 13.sp,
                            color = DiawissNavyDark,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Order Details Recap
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DiawissWhite),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Détail de votre commande",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DiawissNavy
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        items.forEach { itm ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${itm.productName} (x${itm.quantity})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = DiawissNavy
                                    )
                                    if (itm.selectedOptionsSummary.isNotBlank()) {
                                        Text(text = itm.selectedOptionsSummary, fontSize = 11.sp, color = DiawissNavyLight)
                                    }
                                    if (itm.customDimensionsSummary.isNotBlank()) {
                                        Text(text = itm.customDimensionsSummary, fontSize = 11.sp, color = DiawissGoldDark)
                                    }
                                }
                                Text(
                                    text = "${itm.totalItemDa} DA",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = DiawissNavy
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DiawissBorder)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Client :", fontSize = 12.sp, color = DiawissTextSecondary)
                            Text("${order.customerLastName.uppercase()} ${order.customerFirstName}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DiawissNavy)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Téléphone :", fontSize = 12.sp, color = DiawissTextSecondary)
                            Text(order.phone1, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DiawissNavy)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Wilaya :", fontSize = 12.sp, color = DiawissTextSecondary)
                            Text(order.wilayaName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DiawissNavy)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Frais livraison :", fontSize = 12.sp, color = DiawissTextSecondary)
                            Text("${order.shippingFeeDa} DA", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DiawissNavy)
                        }

                        if (order.discountAmountDa > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Remise déduite :", fontSize = 12.sp, color = DiawissSuccess)
                                Text("-${order.discountAmountDa} DA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DiawissSuccess)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DiawissGold)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("TOTAL À REGLER :", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = DiawissNavy)
                            Text("${order.totalAmountDa} DA", fontSize = 18.sp, fontWeight = FontWeight.Black, color = DiawissGoldDark)
                        }
                    }
                }
            }

            // Navigation Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateToTracking,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = DiawissNavy, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Suivre la commande", color = DiawissNavy, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onNavigateHome,
                        colors = ButtonDefaults.buttonColors(containerColor = DiawissNavy, contentColor = DiawissWhite),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Retour à l'accueil", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
