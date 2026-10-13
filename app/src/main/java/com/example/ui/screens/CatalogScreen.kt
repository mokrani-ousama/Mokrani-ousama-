package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.components.ProductImage
import com.example.ui.theme.*
import com.example.ui.viewmodel.DiawissViewModel

@Composable
fun CatalogScreen(
    viewModel: DiawissViewModel,
    onConfigureProduct: (ProductEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    val categories = listOf(
        "TOUT" to "Toutes les tenues",
        "PACK" to "Packs Complets 🎓",
        "CHAPEAU" to "Chapeaux",
        "ECHARPE" to "Écharpes",
        "JACKET" to "Jackets & Robes"
    )

    val filteredProducts = remember(products, selectedCategory) {
        if (selectedCategory == "TOUT") products
        else products.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DiawissOffWhite)
            .testTag("catalog_screen")
    ) {
        // Header info
        Surface(
            color = DiawissNavy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Collection Soutenance DIAWISS",
                    color = DiawissGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Choisissez vos tenues et personnalisez vos options et dimensions",
                    color = DiawissWhite.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )
            }
        }

        // Category filter chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { (key, label) ->
                val isSelected = selectedCategory == key
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectCategory(key) },
                    label = {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DiawissGold,
                        selectedLabelColor = DiawissNavyDark,
                        containerColor = DiawissWhite,
                        labelColor = DiawissNavy
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) DiawissGold else DiawissBorder
                    ),
                    modifier = Modifier.testTag("filter_chip_$key")
                )
            }
        }

        // Product list
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredProducts) { product ->
                CatalogProductCard(
                    product = product,
                    onConfigure = { onConfigureProduct(product) }
                )
            }
        }
    }
}

@Composable
fun CatalogProductCard(
    product: ProductEntity,
    onConfigure: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onConfigure() }
            .testTag("product_card_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = DiawissWhite),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                ProductImage(
                    drawableName = product.drawableName,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize()
                )

                if (product.badge != null) {
                    Surface(
                        color = DiawissGold,
                        shape = RoundedCornerShape(bottomEnd = 12.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = product.badge,
                            color = DiawissNavyDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                Surface(
                    color = DiawissNavy.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(topStart = 10.dp),
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = DiawissGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sur mesure & Options",
                            color = DiawissWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DiawissNavy
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = product.description,
                    fontSize = 12.sp,
                    color = DiawissTextSecondary,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tarif de base",
                            fontSize = 10.sp,
                            color = DiawissTextSecondary
                        )
                        Text(
                            text = "${product.basePriceDa} DA",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            color = DiawissGoldDark
                        )
                    }

                    Button(
                        onClick = onConfigure,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DiawissNavy,
                            contentColor = DiawissWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_configure_${product.id}")
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = DiawissGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Personnaliser", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
