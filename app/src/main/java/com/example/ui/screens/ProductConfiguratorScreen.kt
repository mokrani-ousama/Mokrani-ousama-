package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.model.ProductEntity
import com.example.data.model.ProductOptionEntity
import com.example.ui.components.ProductImage
import com.example.ui.theme.*
import com.example.ui.viewmodel.DiawissViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductConfiguratorScreen(
    product: ProductEntity,
    viewModel: DiawissViewModel,
    onBack: () -> Unit,
    onAddToCartSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentOptions by viewModel.currentOptions.collectAsState()
    val selectedOptions by viewModel.selectedOptions.collectAsState()
    val unitPrice by viewModel.configuratorUnitPriceDa.collectAsState()
    val quantity by viewModel.configuratorQuantity.collectAsState()

    val isCustomDim by viewModel.isCustomDimension.collectAsState()
    val hatCircumference by viewModel.hatCircumferenceCm.collectAsState()
    val scarfLength by viewModel.scarfLengthCm.collectAsState()
    val scarfWidth by viewModel.scarfWidthCm.collectAsState()
    val scarfText by viewModel.scarfCustomText.collectAsState()
    val scarfUniv by viewModel.scarfUniversity.collectAsState()
    val scarfSpec by viewModel.scarfSpecialty.collectAsState()
    val jacketSize by viewModel.jacketSize.collectAsState()
    val jacketLength by viewModel.jacketLengthCm.collectAsState()
    val jacketBust by viewModel.jacketBustCm.collectAsState()
    val jacketSleeve by viewModel.jacketSleeveCm.collectAsState()

    var showSizeGuideDialog by remember { mutableStateOf(false) }

    val optionsByGroup = remember(currentOptions) {
        currentOptions.groupBy { it.groupType }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Personnalisation DIAWISS",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DiawissWhite
                        )
                        Text(
                            text = product.name,
                            fontSize = 11.sp,
                            color = DiawissGoldLight,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour",
                            tint = DiawissWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DiawissNavy)
            )
        },
        bottomBar = {
            // Sticky Price Summary & Add to Cart button
            Surface(
                color = DiawissWhite,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Sous-total article",
                            fontSize = 11.sp,
                            color = DiawissTextSecondary
                        )
                        Text(
                            text = "${unitPrice * quantity} DA",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = DiawissGoldDark
                        )
                        if (quantity > 1) {
                            Text(
                                text = "($unitPrice DA × $quantity)",
                                fontSize = 10.sp,
                                color = DiawissTextSecondary
                            )
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.addConfiguredItemToCart()
                            onAddToCartSuccess()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DiawissNavy,
                            contentColor = DiawissWhite
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("btn_add_to_cart_final")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = null,
                            tint = DiawissGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ajouter au Panier",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
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
            // Product Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DiawissWhite),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            ProductImage(
                                drawableName = product.drawableName,
                                contentDescription = product.name,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = product.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = DiawissNavy
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = product.description,
                                fontSize = 13.sp,
                                color = DiawissTextSecondary,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Prix unitaire de base : ${product.basePriceDa} DA",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DiawissGoldDark
                            )
                        }
                    }
                }
            }

            // Dynamic Options Groups (Style, Color, Size, Embroidery)
            optionsByGroup.forEach { (groupType, options) ->
                item {
                    val groupLabel = options.firstOrNull()?.groupLabel ?: groupType
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DiawissWhite),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = groupLabel,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = DiawissNavy
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            // If group is COLOR, show visual swatches + text
                            if (groupType == "COLOR") {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    options.forEach { opt ->
                                        val isSelected = selectedOptions[groupType]?.id == opt.id
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSelected) DiawissGoldContainer else DiawissSurfaceVariant)
                                                .border(
                                                    width = if (isSelected) 1.5.dp else 1.dp,
                                                    color = if (isSelected) DiawissGold else Color.Transparent,
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .clickable { viewModel.selectOption(opt) }
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                // Color dot
                                                val parsedColor = try {
                                                    Color(android.graphics.Color.parseColor(opt.colorHex ?: "#D4AF37"))
                                                } catch (_: Exception) {
                                                    DiawissGold
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .size(22.dp)
                                                        .clip(CircleShape)
                                                        .background(parsedColor)
                                                        .border(1.dp, DiawissBorder, CircleShape)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    text = opt.optionName,
                                                    fontSize = 13.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = DiawissNavy
                                                )
                                            }

                                            if (opt.surchargeDa > 0) {
                                                Text(
                                                    text = "+${opt.surchargeDa} DA",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = DiawissGoldDark
                                                )
                                            } else {
                                                Text(
                                                    text = "Inclus",
                                                    fontSize = 11.sp,
                                                    color = DiawissTextSecondary
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Group is STYLE, SIZE, or LENGTH: show selectable chips/tiles
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    options.forEach { opt ->
                                        val isSelected = selectedOptions[groupType]?.id == opt.id
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSelected) DiawissGoldContainer else DiawissSurfaceVariant)
                                                .border(
                                                    width = if (isSelected) 1.5.dp else 1.dp,
                                                    color = if (isSelected) DiawissGold else Color.Transparent,
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .clickable { viewModel.selectOption(opt) }
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = opt.optionName,
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = DiawissNavy
                                            )
                                            if (opt.surchargeDa > 0) {
                                                Text(
                                                    text = "+${opt.surchargeDa} DA",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = DiawissGoldDark
                                                )
                                            } else {
                                                Text(
                                                    text = "Inclus",
                                                    fontSize = 11.sp,
                                                    color = DiawissTextSecondary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Custom Dimensions / Specific Inputs per Product Category
            when (product.category) {
                "CHAPEAU" -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DiawissWhite),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Dimensions du Chapeau",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DiawissNavy
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    RadioButton(
                                        selected = !isCustomDim,
                                        onClick = { viewModel.isCustomDimension.value = false }
                                    )
                                    Text(
                                        text = "Taille standard réglable (24×24 cm - 56 à 58 cm)",
                                        fontSize = 13.sp,
                                        color = DiawissNavy
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    RadioButton(
                                        selected = isCustomDim,
                                        onClick = { viewModel.isCustomDimension.value = true }
                                    )
                                    Text(
                                        text = "Sur-mesure (Tour de tête personnalisé) (+200 DA)",
                                        fontSize = 13.sp,
                                        color = DiawissNavy
                                    )
                                }

                                if (isCustomDim) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = hatCircumference,
                                        onValueChange = { viewModel.hatCircumferenceCm.value = it },
                                        label = { Text("Tour de tête en centimètres (ex: 59 cm)") },
                                        placeholder = { Text("58") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                "ECHARPE" -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DiawissWhite),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Personnalisation & Broderie de l'Écharpe",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DiawissNavy
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = scarfText,
                                    onValueChange = { viewModel.scarfCustomText.value = it },
                                    label = { Text("Texte brodé personnalisé") },
                                    placeholder = { Text("Ex: Dr. Sarah BOUZID / Ingénieur 2026") },
                                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = DiawissGoldDark) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = scarfUniv,
                                    onValueChange = { viewModel.scarfUniversity.value = it },
                                    label = { Text("Université") },
                                    placeholder = { Text("Ex: Univ. Abderrahmane Mira - Béjaïa") },
                                    leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = DiawissGoldDark) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = scarfSpec,
                                    onValueChange = { viewModel.scarfSpecialty.value = it },
                                    label = { Text("Spécialité / Faculté") },
                                    placeholder = { Text("Ex: Faculté de Médecine / Informatique") },
                                    leadingIcon = { Icon(Icons.Default.Book, contentDescription = null, tint = DiawissGoldDark) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    }
                }

                "JACKET" -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DiawissWhite),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Taille & Mesures du Jacket",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DiawissNavy
                                    )

                                    TextButton(onClick = { showSizeGuideDialog = true }) {
                                        Icon(Icons.Default.Straighten, contentDescription = null, tint = DiawissGoldDark, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Guide des tailles", fontSize = 11.sp, color = DiawissGoldDark, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    RadioButton(
                                        selected = !isCustomDim,
                                        onClick = { viewModel.isCustomDimension.value = false }
                                    )
                                    Text(text = "Taille standard (XS à 3XL)", fontSize = 13.sp, color = DiawissNavy)
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    RadioButton(
                                        selected = isCustomDim,
                                        onClick = { viewModel.isCustomDimension.value = true }
                                    )
                                    Text(text = "Confection Sur-Mesure Atelier (+600 DA)", fontSize = 13.sp, color = DiawissNavy)
                                }

                                if (isCustomDim) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Indiquez vos mensurations en centimètres :",
                                        fontSize = 12.sp,
                                        color = DiawissTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = jacketLength,
                                        onValueChange = { viewModel.jacketLengthCm.value = it },
                                        label = { Text("Longueur souhaitée (cm)") },
                                        placeholder = { Text("Ex: 110 cm") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = jacketBust,
                                        onValueChange = { viewModel.jacketBustCm.value = it },
                                        label = { Text("Tour de poitrine / largeur (cm)") },
                                        placeholder = { Text("Ex: 100 cm") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = jacketSleeve,
                                        onValueChange = { viewModel.jacketSleeveCm.value = it },
                                        label = { Text("Longueur des manches (cm)") },
                                        placeholder = { Text("Ex: 62 cm") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quantity selector
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DiawissWhite),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Quantité",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = DiawissNavy
                            )
                            Text(
                                text = "Pour vous ou commande groupée",
                                fontSize = 11.sp,
                                color = DiawissTextSecondary
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilledIconButton(
                                onClick = {
                                    if (quantity > 1) viewModel.configuratorQuantity.value = quantity - 1
                                },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = DiawissSurfaceVariant,
                                    contentColor = DiawissNavy
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Diminuer")
                            }

                            Text(
                                text = "$quantity",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = DiawissNavy
                            )

                            FilledIconButton(
                                onClick = {
                                    viewModel.configuratorQuantity.value = quantity + 1
                                },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = DiawissNavy,
                                    contentColor = DiawissWhite
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Augmenter")
                            }
                        }
                    }
                }
            }

            // Summary breakdown card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DiawissGoldContainer),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Détail du calcul",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DiawissNavyDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Prix de base :", fontSize = 12.sp, color = DiawissNavyDark)
                            Text("${product.basePriceDa} DA", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DiawissNavyDark)
                        }
                        val surcharges = selectedOptions.values.sumOf { it.surchargeDa }
                        if (surcharges > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Options & finitions sélectionnées :", fontSize = 12.sp, color = DiawissNavyDark)
                                Text("+$surcharges DA", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DiawissGoldDark)
                            }
                        }
                        if (isCustomDim) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Option sur-mesure confection :", fontSize = 12.sp, color = DiawissNavyDark)
                                Text("+ Supplément appliqué", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DiawissGoldDark)
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DiawissGold.copy(alpha = 0.5f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Prix unitaire configuré :", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DiawissNavyDark)
                            Text("$unitPrice DA", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DiawissNavyDark)
                        }
                    }
                }
            }
        }
    }

    // Size Guide Dialog
    if (showSizeGuideDialog) {
        AlertDialog(
            onDismissRequest = { showSizeGuideDialog = false },
            title = {
                Text(
                    text = "Guide des Tailles DIAWISS (en cm)",
                    fontWeight = FontWeight.Bold,
                    color = DiawissNavy
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("• Taille S : Poitrine 88-92 cm, Longueur 100 cm, Manches 58 cm", fontSize = 12.sp)
                    Text("• Taille M : Poitrine 96-100 cm, Longueur 105 cm, Manches 60 cm", fontSize = 12.sp)
                    Text("• Taille L : Poitrine 104-108 cm, Longueur 110 cm, Manches 62 cm", fontSize = 12.sp)
                    Text("• Taille XL : Poitrine 112-116 cm, Longueur 115 cm, Manches 64 cm", fontSize = 12.sp)
                    Text("• Taille XXL : Poitrine 120-124 cm, Longueur 118 cm, Manches 65 cm", fontSize = 12.sp)
                    Text("• Taille XXXL : Poitrine 128-132 cm, Longueur 120 cm, Manches 66 cm", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("💡 Pour toute taille atypique, cochez « Sur-Mesure » et notre atelier adaptera le patron à vos mesures exactes !", fontSize = 11.sp, color = DiawissGoldDark, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                TextButton(onClick = { showSizeGuideDialog = false }) {
                    Text("Fermer", color = DiawissNavy, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
