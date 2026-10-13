package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlgerianWilayas
import com.example.data.model.OrderEntity
import com.example.data.model.Wilaya
import com.example.ui.theme.*
import com.example.ui.viewmodel.DiawissViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: DiawissViewModel,
    onBack: () -> Unit,
    onOrderPlaced: (OrderEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val subtotalDa by viewModel.cartSubtotalDa.collectAsState()
    val shippingFeeDa by viewModel.liveShippingFeeDa.collectAsState()
    val discountResult by viewModel.discountResult.collectAsState()
    val finalTotalDa by viewModel.finalTotalPayableDa.collectAsState()

    val firstName by viewModel.checkoutFirstName.collectAsState()
    val lastName by viewModel.checkoutLastName.collectAsState()
    val phone1 by viewModel.checkoutPhone1.collectAsState()
    val phone2 by viewModel.checkoutPhone2.collectAsState()
    val address by viewModel.checkoutAddress.collectAsState()
    val selectedWilaya by viewModel.checkoutWilaya.collectAsState()
    val commune by viewModel.checkoutCommune.collectAsState()
    val university by viewModel.checkoutUniversity.collectAsState()
    val specialty by viewModel.checkoutSpecialty.collectAsState()
    val defenseDate by viewModel.checkoutDefenseDate.collectAsState()
    val notes by viewModel.checkoutNotes.collectAsState()
    val deliveryType by viewModel.checkoutDeliveryType.collectAsState()

    val isSubmitting by viewModel.isSubmittingOrder.collectAsState()
    val errorMessage by viewModel.orderError.collectAsState()

    var showWilayaDialog by remember { mutableStateOf(false) }
    var wilayaSearchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Validation de Commande",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DiawissWhite
                        )
                        Text(
                            text = "Livraison partout en Algérie • Sans compte obligatoire",
                            fontSize = 11.sp,
                            color = DiawissGoldLight
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = DiawissWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DiawissNavy)
            )
        },
        bottomBar = {
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
                    if (errorMessage != null) {
                        Surface(
                            color = DiawissDanger.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = DiawissDanger, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = errorMessage ?: "", color = DiawissDanger, fontSize = 12.sp)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "TOTAL À PAYER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DiawissNavy)
                            Text(
                                text = "$finalTotalDa DA",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = DiawissGoldDark
                            )
                            Text(
                                text = "Paiement à la livraison",
                                fontSize = 10.sp,
                                color = DiawissTextSecondary
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.submitOrder { placedOrder ->
                                    onOrderPlaced(placedOrder)
                                }
                            },
                            enabled = !isSubmitting && cartItems.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DiawissNavy,
                                contentColor = DiawissWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(52.dp)
                                .testTag("btn_confirm_order")
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = DiawissGold,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Enregistrement...", fontSize = 13.sp)
                            } else {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DiawissGold, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "CONFIRMER MA COMMANDE",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
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
            // 1. Client Identity
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DiawissWhite),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = DiawissGoldDark, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "1. Coordonnées de l'étudiant(e)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = DiawissNavy
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = firstName,
                                onValueChange = { viewModel.checkoutFirstName.value = it },
                                label = { Text("Prénom *") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_firstname"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = lastName,
                                onValueChange = { viewModel.checkoutLastName.value = it },
                                label = { Text("Nom *") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_lastname"),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = phone1,
                            onValueChange = { viewModel.checkoutPhone1.value = it },
                            label = { Text("Numéro de téléphone principal *") },
                            placeholder = { Text("0550 12 34 56 ou +213...") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = DiawissGoldDark) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_phone1"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = phone2,
                            onValueChange = { viewModel.checkoutPhone2.value = it },
                            label = { Text("Deuxième numéro (facultatif)") },
                            placeholder = { Text("Numéro d'un proche ou WhatsApp") },
                            leadingIcon = { Icon(Icons.Default.PhoneIphone, contentDescription = null, tint = DiawissTextSecondary) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_phone2"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // 2. Shipping & Delivery in Algeria
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DiawissWhite),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = DiawissGoldDark, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "2. Mode et Adresse de Livraison",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = DiawissNavy
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Delivery options selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DeliveryTypeButton(
                                label = "À Domicile",
                                icon = Icons.Default.Home,
                                selected = deliveryType == "DOMICILE",
                                onClick = { viewModel.checkoutDeliveryType.value = "DOMICILE" },
                                modifier = Modifier.weight(1f)
                            )
                            DeliveryTypeButton(
                                label = "Point Relais",
                                icon = Icons.Default.Inventory2,
                                selected = deliveryType == "RELAIS",
                                onClick = { viewModel.checkoutDeliveryType.value = "RELAIS" },
                                modifier = Modifier.weight(1f)
                            )
                            DeliveryTypeButton(
                                label = "Atelier 06 (Gratuit)",
                                icon = Icons.Default.Storefront,
                                selected = deliveryType == "RETRAIT",
                                onClick = { viewModel.checkoutDeliveryType.value = "RETRAIT" },
                                modifier = Modifier.weight(1.2f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 58 Wilayas Selector
                        OutlinedCard(
                            onClick = { showWilayaDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("select_wilaya"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Wilaya de livraison *", fontSize = 11.sp, color = DiawissTextSecondary)
                                    Text(
                                        text = selectedWilaya.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DiawissNavy
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (deliveryType == "RETRAIT") "0 DA" else "${selectedWilaya.defaultShippingFeeDa} DA",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = DiawissGoldDark
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = DiawissNavy)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = commune,
                            onValueChange = { viewModel.checkoutCommune.value = it },
                            label = { Text("Commune *") },
                            placeholder = { Text("Ex: Béjaïa Centre, Akbou, El Kseur...") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_commune"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        if (deliveryType != "RETRAIT") {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = address,
                                onValueChange = { viewModel.checkoutAddress.value = it },
                                label = { Text("Adresse exacte ou repère *") },
                                placeholder = { Text("Cité, quartier, numéro de bâtiment...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_address"),
                                shape = RoundedCornerShape(10.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = DiawissGoldContainer,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Storefront, contentDescription = null, tint = DiawissGoldDark)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Retrait gratuit à notre atelier DIAWISS : Cité Tobbal, Béjaïa 06000. Essayage possible sur place !",
                                        fontSize = 12.sp,
                                        color = DiawissNavyDark
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Academic Details (Optional)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DiawissWhite),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.School, contentDescription = null, tint = DiawissGoldDark, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "3. Renseignements universitaires (Facultatif)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = DiawissNavy
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = university,
                            onValueChange = { viewModel.checkoutUniversity.value = it },
                            label = { Text("Université ou École") },
                            placeholder = { Text("Ex: Université de Béjaïa (Targa Ouzemmour / Amizour)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = specialty,
                            onValueChange = { viewModel.checkoutSpecialty.value = it },
                            label = { Text("Spécialité / Filière") },
                            placeholder = { Text("Ex: Médecine, Master Finance, Génie Civil...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = defenseDate,
                            onValueChange = { viewModel.checkoutDefenseDate.value = it },
                            label = { Text("Date approximative de la soutenance") },
                            placeholder = { Text("Ex: Fin Juin 2026 / Début Juillet 2026") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { viewModel.checkoutNotes.value = it },
                            label = { Text("Instructions particulières pour l'atelier") },
                            placeholder = { Text("Ex: Urgent, besoin pour le samedi, etc.") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            minLines = 2
                        )
                    }
                }
            }

            // 4. Order Recap Box
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DiawissGoldContainer),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Récapitulatif Financier",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DiawissNavyDark
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        cartItems.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${item.quantity}x ${item.productName}",
                                    fontSize = 12.sp,
                                    color = DiawissNavyDark,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${item.totalItemPriceDa} DA",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DiawissNavyDark
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DiawissGold.copy(alpha = 0.5f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Sous-total articles :", fontSize = 12.sp, color = DiawissNavyDark)
                            Text("$subtotalDa DA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DiawissNavyDark)
                        }

                        val discAmount = discountResult?.discountAmountDa ?: 0
                        if (discAmount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Remise :", fontSize = 12.sp, color = DiawissSuccess)
                                Text("-$discAmount DA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DiawissSuccess)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Frais de livraison (${selectedWilaya.name.take(12)}) :", fontSize = 12.sp, color = DiawissNavyDark)
                            Text("$shippingFeeDa DA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DiawissNavyDark)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DiawissGold)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("TOTAL À REGLER :", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = DiawissNavyDark)
                            Text("$finalTotalDa DA", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = DiawissGoldDark)
                        }
                    }
                }
            }
        }
    }

    // 58 Wilayas Dialog Picker
    if (showWilayaDialog) {
        val filteredWilayas = remember(wilayaSearchQuery) {
            AlgerianWilayas.list.filter {
                it.name.contains(wilayaSearchQuery, ignoreCase = true) || it.code.contains(wilayaSearchQuery)
            }
        }

        AlertDialog(
            onDismissRequest = { showWilayaDialog = false },
            title = {
                Column {
                    Text(text = "Sélectionnez votre Wilaya", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DiawissNavy)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = wilayaSearchQuery,
                        onValueChange = { wilayaSearchQuery = it },
                        placeholder = { Text("Rechercher (ex: 06, Béjaïa, Alger...)", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            text = {
                Box(modifier = Modifier.height(300.dp)) {
                    LazyColumn {
                        items(filteredWilayas) { wilaya ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.checkoutWilaya.value = wilaya
                                        showWilayaDialog = false
                                        wilayaSearchQuery = ""
                                    }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = wilaya.name,
                                    fontSize = 13.sp,
                                    fontWeight = if (wilaya.code == selectedWilaya.code) FontWeight.Bold else FontWeight.Normal,
                                    color = if (wilaya.code == selectedWilaya.code) DiawissGoldDark else DiawissNavy
                                )
                                Text(
                                    text = "${wilaya.defaultShippingFeeDa} DA",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DiawissTextSecondary
                                )
                            }
                            HorizontalDivider(color = DiawissBorder.copy(alpha = 0.5f))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showWilayaDialog = false }) {
                    Text("Fermer", color = DiawissNavy)
                }
            }
        )
    }
}

@Composable
fun DeliveryTypeButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = if (selected) DiawissNavy else DiawissSurfaceVariant,
        shape = RoundedCornerShape(10.dp),
        border = if (selected) ButtonDefaults.outlinedButtonBorder else null,
        modifier = modifier.height(56.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) DiawissGold else DiawissNavyLight,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) DiawissWhite else DiawissNavy
            )
        }
    }
}
