package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiawissTopBar(
    currentRoute: String,
    cartItemCount: Int,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier.testTag("diawiss_top_bar"),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DiawissNavy,
            titleContentColor = DiawissWhite,
            actionIconContentColor = DiawissGold
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                // Brand Emblem
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DiawissGold),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Emblème DIAWISS",
                        tint = DiawissNavyDark,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "DIAWISS",
                        fontWeight = FontWeight.Black,
                        fontSize = 19.sp,
                        letterSpacing = 2.sp,
                        color = DiawissGold
                    )
                    Text(
                        text = "BÉJAÏA 06 • SOUTENANCE UNIVERSITAIRE",
                        fontWeight = FontWeight.Medium,
                        fontSize = 9.sp,
                        letterSpacing = 0.5.sp,
                        color = DiawissWhite.copy(alpha = 0.85f)
                    )
                }
            }
        },
        actions = {
            // Track Order
            IconButton(
                onClick = { onNavigate("tracking") },
                modifier = Modifier.testTag("action_track_order")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Suivre ma commande",
                    tint = if (currentRoute == "tracking") DiawissGold else DiawissWhite.copy(alpha = 0.9f)
                )
            }

            // Cart with badge
            IconButton(
                onClick = { onNavigate("cart") },
                modifier = Modifier.testTag("action_cart")
            ) {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = DiawissGold,
                                contentColor = DiawissNavyDark
                            ) {
                                Text(
                                    text = "$cartItemCount",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingBag,
                        contentDescription = "Panier",
                        tint = if (currentRoute == "cart") DiawissGold else DiawissWhite.copy(alpha = 0.9f)
                    )
                }
            }

            // Admin Access
            IconButton(
                onClick = { onNavigate("admin") },
                modifier = Modifier.testTag("action_admin")
            ) {
                Icon(
                    imageVector = Icons.Outlined.AdminPanelSettings,
                    contentDescription = "Espace Administrateur",
                    tint = if (currentRoute == "admin") DiawissGold else DiawissWhite.copy(alpha = 0.7f)
                )
            }
        }
    )
}
