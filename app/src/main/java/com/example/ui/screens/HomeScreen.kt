package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.components.ProductImage
import com.example.ui.theme.*
import com.example.ui.viewmodel.DiawissViewModel
import java.net.URLEncoder

@Composable
fun HomeScreen(
    viewModel: DiawissViewModel,
    onNavigateToCatalog: () -> Unit,
    onConfigureProduct: (ProductEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val products by viewModel.products.collectAsState()
    val settings by viewModel.siteSettings.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DiawissOffWhite)
            .testTag("home_screen"),
        contentPadding = PaddingPaddingValues(bottom = 32.dp)
    ) {
        // 1. Promo Announcement Banner
        item {
            val bannerText = settings?.promoBannerText
                ?: "🎓 Remise spéciale : -10% sur les Packs Complets avec le code SOUTENANCE2026"
            Surface(
                color = DiawissGoldContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Promo",
                        tint = DiawissGoldDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = bannerText,
                        color = DiawissNavyDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // 2. Hero Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
            ) {
                // Background image
                ProductImage(
                    drawableName = "img_hero_banner",
                    contentDescription = "Tenue de soutenance DIAWISS",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    DiawissNavy.copy(alpha = 0.45f),
                                    DiawissNavy.copy(alpha = 0.85f),
                                    DiawissNavyDark
                                )
                            )
                        )
                )

                // Content inside Hero
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start
                ) {
                    // Badge Béjaïa 06
                    Surface(
                        color = DiawissGold.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(20.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(DiawissGold, DiawissGoldLight))
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = DiawissGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Atelier de confection • Béjaïa 06, Algérie",
                                color = DiawissGoldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Votre réussite mérite une tenue à la hauteur",
                        color = DiawissWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 30.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Personnalisez votre tenue de soutenance avec DIAWISS, Béjaïa 06. Chapeaux, écharpes brodées et robes sur mesure livrés dans 58 wilayas.",
                        color = DiawissWhite.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onNavigateToCatalog,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DiawissGold,
                                contentColor = DiawissNavyDark
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_personaliser_hero")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoFixHigh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Personnaliser ma tenue",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val wpNum = settings?.whatsappNumber ?: "+213550123456"
                                val url = "https://wa.me/${wpNum.replace("+", "").replace(" ", "")}?text=${URLEncoder.encode("Bonjour DIAWISS Béjaïa, je souhaite avoir des informations sur les tenues de soutenance.", "UTF-8")}"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = DiawissGold
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.linearGradient(listOf(DiawissGold, DiawissGoldLight))
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("btn_whatsapp_hero")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WhatsApp", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // 3. Avantages de la marque
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "Pourquoi choisir DIAWISS ?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DiawissNavy,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "L'exigence du sur-mesure pour votre grand jour",
                    fontSize = 12.sp,
                    color = DiawissTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureCard(
                        icon = Icons.Default.AutoAwesome,
                        title = "Sur-Mesure",
                        desc = "Dimensions & broderies personnalisées",
                        modifier = Modifier.weight(1f)
                    )
                    FeatureCard(
                        icon = Icons.Default.LocalShipping,
                        title = "58 Wilayas",
                        desc = "Livraison rapide à domicile ou relais",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureCard(
                        icon = Icons.Default.Handshake,
                        title = "Paiement Réception",
                        desc = "Réglez en espèces à la livraison",
                        modifier = Modifier.weight(1f)
                    )
                    FeatureCard(
                        icon = Icons.Default.Storefront,
                        title = "Atelier Béjaïa 06",
                        desc = "Retrait & essayage direct sur place",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Les Produits & Packs en vedette
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Nos Créations de Soutenance",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DiawissNavy
                        )
                        Text(
                            text = "Chapeaux, écharpes, jackets et packs complets",
                            fontSize = 12.sp,
                            color = DiawissTextSecondary
                        )
                    }

                    TextButton(onClick = onNavigateToCatalog) {
                        Text("Voir tout", color = DiawissGoldDark, fontWeight = FontWeight.Bold)
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = DiawissGoldDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(products) { product ->
                        ProductHomeCard(
                            product = product,
                            onConfigure = { onConfigureProduct(product) }
                        )
                    }
                }
            }
        }

        // 5. Comment ça marche ? (4 étapes)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = DiawissNavy),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Fonctionnement des commandes",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DiawissGold
                    )
                    Text(
                        text = "Simple, rapide et sans obligation de créer un compte",
                        fontSize = 12.sp,
                        color = DiawissWhite.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    StepRow(number = "1", title = "Composez votre tenue", desc = "Choisissez chapeau, écharpe ou robe, seuls ou en pack.")
                    Spacer(modifier = Modifier.height(12.dp))
                    StepRow(number = "2", title = "Personnalisez en direct", desc = "Mesures, couleurs, tour de tête, broderie de votre diplôme.")
                    Spacer(modifier = Modifier.height(12.dp))
                    StepRow(number = "3", title = "Renseignez votre Wilaya", desc = "Validation immédiate avec calcul automatique en DA.")
                    Spacer(modifier = Modifier.height(12.dp))
                    StepRow(number = "4", title = "Paiement à la livraison", desc = "Notre équipe confirme par WhatsApp/téléphone et expédie.")
                }
            }
        }

        // 6. Section FAQ
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Foire Aux Questions (FAQ)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DiawissNavy
                )
                Text(
                    text = "Tout ce que vous devez savoir pour votre soutenance",
                    fontSize = 12.sp,
                    color = DiawissTextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                FaqAccordion(
                    question = "Quels sont les délais de confection et de livraison ?",
                    answer = "Pour les articles en stock, l'expédition se fait sous 24h à 48h. Pour les broderies personnalisées et confections sur mesure à l'atelier de Béjaïa, comptez 3 à 5 jours ouvrés."
                )
                FaqAccordion(
                    question = "Puis-je commander un seul article (ex: juste l'écharpe ou le chapeau) ?",
                    answer = "Absolument ! Vous êtes totalement libre de commander un article à l'unité (chapeau, écharpe, ou jacket) ou d'opter pour un Pack Complet avec remise immédiate."
                )
                FaqAccordion(
                    question = "Comment personnaliser le texte brodé sur l'écharpe ?",
                    answer = "Dans le configurateur, sélectionnez l'option broderie et entrez votre texte : votre nom, votre spécialité (ex: Médecine, Droit, Génie Civil, Informatique) ou votre université."
                )
                FaqAccordion(
                    question = "Peut-on récupérer la commande directement à l'atelier de Béjaïa ?",
                    answer = "Oui ! Lors de la commande, choisissez « Retrait sur place atelier Béjaïa ». Les frais de livraison sont alors 100% gratuits et vous pouvez essayer votre tenue directement."
                )
            }
        }

        // 7. Footer avec informations légales et coordonnées
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = DiawissWhite),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "DIAWISS — BÉJAÏA 06",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DiawissNavy,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "La référence des tenues de soutenance universitaire en Algérie",
                        fontSize = 11.sp,
                        color = DiawissTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DiawissBorder)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = DiawissGoldDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = settings?.atelierAddress ?: "Cité Tobbal, Béjaïa 06000, Algérie",
                            fontSize = 12.sp,
                            color = DiawissTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = DiawissGoldDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = settings?.atelierPhone ?: "0550 12 34 56",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DiawissNavy
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val wpNum = settings?.whatsappNumber ?: "+213550123456"
                            val url = "https://wa.me/${wpNum.replace("+", "").replace(" ", "")}?text=${URLEncoder.encode("Bonjour DIAWISS, je souhaite vous poser une question.", "UTF-8")}"
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Discuter avec l'atelier sur WhatsApp", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "© 2026 DIAWISS • Tous droits réservés • Béjaïa, Algérie",
                        fontSize = 10.sp,
                        color = DiawissTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureCard(
    icon: ImageVector,
    title: String,
    desc: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DiawissWhite),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(DiawissGoldContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = DiawissGoldDark,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DiawissNavy)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = desc, fontSize = 11.sp, color = DiawissTextSecondary, lineHeight = 15.sp)
        }
    }
}

@Composable
fun ProductHomeCard(
    product: ProductEntity,
    onConfigure: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(220.dp)
            .clickable { onConfigure() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DiawissWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                ProductImage(
                    drawableName = product.drawableName,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize()
                )

                if (product.badge != null) {
                    Surface(
                        color = DiawissGold,
                        shape = RoundedCornerShape(bottomEnd = 10.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = product.badge,
                            color = DiawissNavyDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DiawissNavy,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = product.description,
                    fontSize = 11.sp,
                    color = DiawissTextSecondary,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "À partir de",
                            fontSize = 9.sp,
                            color = DiawissTextSecondary
                        )
                        Text(
                            text = "${product.basePriceDa} DA",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DiawissGoldDark
                        )
                    }

                    FilledTonalButton(
                        onClick = onConfigure,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = DiawissNavyLight,
                            contentColor = DiawissWhite
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Configurer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StepRow(
    number: String,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(DiawissGold),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = DiawissNavyDark,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, color = DiawissWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = desc, color = DiawissWhite.copy(alpha = 0.8f), fontSize = 11.sp)
        }
    }
}

@Composable
fun FaqAccordion(
    question: String,
    answer: String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = DiawissWhite),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = DiawissNavy,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = DiawissGoldDark
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = answer,
                    fontSize = 12.sp,
                    color = DiawissTextSecondary,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

private fun PaddingPaddingValues(bottom: androidx.compose.ui.unit.Dp) = PaddingValues(bottom = bottom)
