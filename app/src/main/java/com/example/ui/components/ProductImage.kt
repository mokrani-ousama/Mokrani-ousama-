package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.theme.DiawissGold
import com.example.ui.theme.DiawissNavyLight

@Composable
fun ProductImage(
    drawableName: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    val resId = when (drawableName) {
        "img_hero_banner" -> R.drawable.img_hero_banner
        "img_chapeau" -> R.drawable.img_chapeau
        "img_echarpe" -> R.drawable.img_echarpe
        "img_jacket" -> R.drawable.img_jacket
        else -> {
            val identifier = context.resources.getIdentifier(drawableName, "drawable", context.packageName)
            if (identifier != 0) identifier else R.drawable.img_hero_banner
        }
    }

    if (resId != 0) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        Box(
            modifier = modifier.background(DiawissNavyLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = contentDescription,
                tint = DiawissGold
            )
        }
    }
}
