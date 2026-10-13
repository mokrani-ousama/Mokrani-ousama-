package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class ChartBarData(
    val label: String,
    val valueDa: Int,
    val orderCount: Int = 1
)

data class CategoryShareData(
    val categoryName: String,
    val amountDa: Int,
    val percentage: Float,
    val color: Color
)

@Composable
fun DiawissSalesBarChart(
    data: List<ChartBarData>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val maxValue = remember(data) { maxOf(1000, data.maxOf { it.valueDa }) }
    var selectedBarIndex by remember { mutableStateOf<Int?>(null) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DiawissWhite),
        shape = RoundedCornerShape(16.dp),
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
                        text = "Évolution des Ventes (DA)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DiawissNavy
                    )
                    Text(
                        text = "Touchez une barre pour afficher le montant exact",
                        fontSize = 11.sp,
                        color = DiawissTextSecondary
                    )
                }

                Surface(
                    color = DiawissGoldContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Max: $maxValue DA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = DiawissNavyDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tooltip preview if bar is tapped
            selectedBarIndex?.let { idx ->
                val item = data.getOrNull(idx)
                if (item != null) {
                    Surface(
                        color = DiawissNavy,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${item.label} : ${item.valueDa} DA",
                                color = DiawissGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${item.orderCount} commande(s)",
                                color = DiawissWhite,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Canvas Bar Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(data) {
                            detectTapGestures { offset ->
                                val barWidthTotal = size.width / data.size
                                val tappedIndex = (offset.x / barWidthTotal).toInt()
                                if (tappedIndex in data.indices) {
                                    selectedBarIndex = if (selectedBarIndex == tappedIndex) null else tappedIndex
                                }
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height - 30.dp.toPx()
                    val barCount = data.size
                    val slotWidth = width / barCount
                    val barWidth = slotWidth * 0.55f

                    // Draw 3 horizontal guideline reference lines
                    val steps = 3
                    for (i in 0..steps) {
                        val y = height * (i.toFloat() / steps)
                        drawLine(
                            color = DiawissBorder.copy(alpha = 0.6f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Draw each bar with gradient and rounded tops
                    data.forEachIndexed { index, item ->
                        val barHeight = if (maxValue > 0) (item.valueDa.toFloat() / maxValue) * height else 0f
                        val x = index * slotWidth + (slotWidth - barWidth) / 2f
                        val y = height - barHeight
                        val isSelected = selectedBarIndex == index

                        val barBrush = if (isSelected) {
                            Brush.verticalGradient(
                                colors = listOf(DiawissGold, DiawissGoldDark),
                                startY = y,
                                endY = height
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(DiawissGold, DiawissNavyLight),
                                startY = y,
                                endY = height
                            )
                        }

                        drawRoundRect(
                            brush = barBrush,
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )
                    }
                }
            }

            // X-Axis labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                data.forEachIndexed { index, item ->
                    val isSelected = selectedBarIndex == index
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) DiawissGoldDark else DiawissTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun DiawissCategoryDonutChart(
    shares: List<CategoryShareData>,
    totalAmountDa: Int,
    modifier: Modifier = Modifier
) {
    if (shares.isEmpty()) return

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DiawissWhite),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Répartition du Chiffre d'Affaires",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = DiawissNavy
            )
            Text(
                text = "Par typologie de produit et pack",
                fontSize = 11.sp,
                color = DiawissTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Donut Chart Canvas
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        var startAngle = -90f
                        val strokeWidth = 26.dp.toPx()
                        val diameter = size.minDimension - strokeWidth
                        val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
                        val arcSize = Size(diameter, diameter)

                        shares.forEach { share ->
                            val sweepAngle = share.percentage * 360f
                            drawArc(
                                color = share.color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = strokeWidth)
                            )
                            startAngle += sweepAngle
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TOTAL",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = DiawissTextSecondary
                        )
                        Text(
                            text = "${totalAmountDa / 1000}k DA",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = DiawissNavy
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Legends column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    shares.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(item.color)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.categoryName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = DiawissNavy
                                )
                            }
                            Text(
                                text = "${(item.percentage * 100).toInt()}% (${item.amountDa} DA)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = DiawissTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
