package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.DiawissViewModel

@Composable
fun CartScreen(
    viewModel: DiawissViewModel,
    onNavigateToCatalog: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val subtotalDa by viewModel.cartSubtotalDa.collectAsState()
    val discountResult by viewModel.discountResult.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val couponInput by viewModel.couponInput.collectAsState()
    val settings by viewModel.siteSettings.collectAsState()

    val discountAmountDa = discountResult?.discountAmountDa ?: 0
    val totalBeforeShippingDa = maxOf(0, subtotalDa - discountAmountDa)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DiawissOffWhite)
            .testTag("cart_screen")
    ) {
        // Top Header
        Surface(
            color = DiawissNavy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Mon Panier de Soutenance",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DiawissGold
                    )
                    Text(
                        text = "${cartItems.sumOf { it.quantity }} article(s) sélectionné(s)",
                        fontSize = 12.sp,
                        color = DiawissWhite.copy(alpha = 0.8f)
                    )
                }

                if (cartItems.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearCart() }) {
                        Text("Vider", color = DiawissGoldLight, fontSize = 12.sp)
                    }
                }
            }
        }

        if (cartItems.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(DiawissGoldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingBag,
                            contentDescription = null,
                            tint = DiawissGoldDark,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Votre panier est vide",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DiawissNavy
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Découvrez nos tenues de soutenance, écharpes brodées et chapeaux sur mesure pour composer votre tenue.",
                        fontSize = 13.sp,
                        color = DiawissTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onNavigateToCatalog,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DiawissNavy,
                            contentColor = DiawissWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_empty_catalog")
                    ) {
                        Icon(Icons.Default.Checkroom, contentDescription = null, tint = DiawissGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Explorer les tenues", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Cart contents list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cart items
                items(cartItems, key = { it.cartId }) { item ->
                    CartItemCard(
                        item = item,
                        onIncrease = { viewModel.updateCartItemQuantity(item.cartId, 1) },
                        onDecrease = { viewModel.updateCartItemQuantity(item.cartId, -1) },
                        onRemove = { viewModel.removeCartItem(item.cartId) }
                    )
                }

                // Promo code section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DiawissWhite),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalOffer, contentDescription = null, tint = DiawissGoldDark, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Code promo & Remise",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DiawissNavy
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (appliedCoupon != null) {
                                Surface(
                                    color = DiawissGoldContainer,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Code actif : $appliedCoupon",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = DiawissNavyDark
                                            )
                                            Text(
                                                text = "Remise déduite : -$discountAmountDa DA",
                                                fontSize = 11.sp,
                                                color = DiawissGoldDark,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        IconButton(onClick = { viewModel.removeCoupon() }) {
                                            Icon(Icons.Default.Close, contentDescription = "Supprimer", tint = DiawissDanger)
                                        }
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = couponInput,
                                        onValueChange = { viewModel.couponInput.value = it },
                                        placeholder = { Text("Ex: SOUTENANCE2026", fontSize = 12.sp) },
                                        singleLine = true,
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("input_coupon_code"),
                                        shape = RoundedCornerShape(10.dp)
                                    )

                                    Button(
                                        onClick = { viewModel.applyCoupon(couponInput) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = DiawissGold,
                                            contentColor = DiawissNavyDark
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("btn_apply_coupon")
                                    ) {
                                        Text("Appliquer", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }

                                if (discountResult?.errorMessage != null) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = discountResult?.errorMessage ?: "",
                                        color = DiawissDanger,
                                        fontSize = 11.sp
                                    )
                                }

                                // Quick suggestion chips
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Disponibles :", fontSize = 10.sp, color = DiawissTextSecondary)
                                    listOf("SOUTENANCE2026", "BEJAIA06").forEach { code ->
                                        SuggestionChip(
                                            onClick = {
                                                viewModel.couponInput.value = code
                                                viewModel.applyCoupon(code)
                                            },
                                            label = { Text(code, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                containerColor = DiawissGoldContainer,
                                                labelColor = DiawissNavyDark
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Sticky Total Card & Checkout Button
            Surface(
                color = DiawissWhite,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Sous-total articles :", fontSize = 13.sp, color = DiawissTextSecondary)
                        Text("$subtotalDa DA", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DiawissNavy)
                    }

                    if (discountAmountDa > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Remise promotionnelle :", fontSize = 13.sp, color = DiawissSuccess)
                            Text("-$discountAmountDa DA", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DiawissSuccess)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Livraison :", fontSize = 13.sp, color = DiawissTextSecondary)
                        Text("Calculée à l'étape suivante", fontSize = 11.sp, color = DiawissGoldDark, fontWeight = FontWeight.Medium)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = DiawissBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("TOTAL À PAYER", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DiawissNavy)
                            Text(
                                text = "$totalBeforeShippingDa DA",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = DiawissGoldDark
                            )
                        }

                        Button(
                            onClick = onNavigateToCheckout,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DiawissNavy,
                                contentColor = DiawissWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("btn_proceed_to_checkout")
                        ) {
                            Text("Commander", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = DiawissGold, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DiawissWhite),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.productName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = DiawissNavy
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val optSummary = item.getOptionsSummary()
                    if (optSummary.isNotBlank()) {
                        Text(
                            text = optSummary,
                            fontSize = 11.sp,
                            color = DiawissNavyLight,
                            lineHeight = 15.sp
                        )
                    }

                    val dimSummary = item.getDimensionsSummary()
                    if (dimSummary.isNotBlank()) {
                        Text(
                            text = dimSummary,
                            fontSize = 11.sp,
                            color = DiawissGoldDark,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 15.sp
                        )
                    }
                }

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Supprimer",
                        tint = DiawissDanger.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = DiawissBorder)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity changer
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledIconButton(
                        onClick = onDecrease,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = DiawissSurfaceVariant,
                            contentColor = DiawissNavy
                        ),
                        modifier = Modifier.size(30.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                    }

                    Text(
                        text = "${item.quantity}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DiawissNavy
                    )

                    FilledIconButton(
                        onClick = onIncrease,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = DiawissNavy,
                            contentColor = DiawissWhite
                        ),
                        modifier = Modifier.size(30.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${item.totalItemPriceDa} DA",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = DiawissGoldDark
                    )
                    if (item.quantity > 1) {
                        Text(
                            text = "${item.unitPriceDa} DA / unité",
                            fontSize = 10.sp,
                            color = DiawissTextSecondary
                        )
                    }
                }
            }
        }
    }
}
