package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun DiawissBottomNav(
    currentRoute: String,
    cartItemCount: Int,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("diawiss_bottom_nav"),
        containerColor = DiawissNavy,
        contentColor = DiawissWhite
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { onNavigate("home") },
            icon = {
                Icon(
                    imageVector = if (currentRoute == "home") Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Accueil"
                )
            },
            label = { Text("Accueil", fontSize = 11.sp, fontWeight = if (currentRoute == "home") FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DiawissNavyDark,
                selectedTextColor = DiawissGold,
                indicatorColor = DiawissGold,
                unselectedIconColor = DiawissWhite.copy(alpha = 0.7f),
                unselectedTextColor = DiawissWhite.copy(alpha = 0.7f)
            ),
            modifier = Modifier.testTag("nav_home")
        )

        NavigationBarItem(
            selected = currentRoute == "catalog",
            onClick = { onNavigate("catalog") },
            icon = {
                Icon(
                    imageVector = if (currentRoute == "catalog") Icons.Filled.Checkroom else Icons.Outlined.Checkroom,
                    contentDescription = "Catalogue"
                )
            },
            label = { Text("Tenues", fontSize = 11.sp, fontWeight = if (currentRoute == "catalog") FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DiawissNavyDark,
                selectedTextColor = DiawissGold,
                indicatorColor = DiawissGold,
                unselectedIconColor = DiawissWhite.copy(alpha = 0.7f),
                unselectedTextColor = DiawissWhite.copy(alpha = 0.7f)
            ),
            modifier = Modifier.testTag("nav_catalog")
        )

        NavigationBarItem(
            selected = currentRoute == "cart",
            onClick = { onNavigate("cart") },
            icon = {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = DiawissGold,
                                contentColor = DiawissNavyDark
                            ) {
                                Text("$cartItemCount", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentRoute == "cart") Icons.Filled.ShoppingBag else Icons.Outlined.ShoppingBag,
                        contentDescription = "Panier"
                    )
                }
            },
            label = { Text("Panier", fontSize = 11.sp, fontWeight = if (currentRoute == "cart") FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DiawissNavyDark,
                selectedTextColor = DiawissGold,
                indicatorColor = DiawissGold,
                unselectedIconColor = DiawissWhite.copy(alpha = 0.7f),
                unselectedTextColor = DiawissWhite.copy(alpha = 0.7f)
            ),
            modifier = Modifier.testTag("nav_cart")
        )

        NavigationBarItem(
            selected = currentRoute == "tracking",
            onClick = { onNavigate("tracking") },
            icon = {
                Icon(
                    imageVector = if (currentRoute == "tracking") Icons.Filled.LocalShipping else Icons.Outlined.LocalShipping,
                    contentDescription = "Suivi"
                )
            },
            label = { Text("Suivi", fontSize = 11.sp, fontWeight = if (currentRoute == "tracking") FontWeight.Bold else FontWeight.Normal) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DiawissNavyDark,
                selectedTextColor = DiawissGold,
                indicatorColor = DiawissGold,
                unselectedIconColor = DiawissWhite.copy(alpha = 0.7f),
                unselectedTextColor = DiawissWhite.copy(alpha = 0.7f)
            ),
            modifier = Modifier.testTag("nav_tracking")
        )
    }
}
