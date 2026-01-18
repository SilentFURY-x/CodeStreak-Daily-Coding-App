package com.fury.codestreak.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fury.codestreak.presentation.theme.SurfaceDark

@Composable
fun ShimmerCard(
    modifier: Modifier = Modifier,
    width: androidx.compose.ui.unit.Dp = Double.NaN.dp,
    height: androidx.compose.ui.unit.Dp
) {
    // 1. Define the "Shine" Colors
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.02f), // Darker base
        Color.White.copy(alpha = 0.1f),  // Bright shine
        Color.White.copy(alpha = 0.02f), // Darker base
    )

    // 2. Create the Infinite Animation
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "shimmer"
    )

    // 3. Create the Moving Brush
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    // 4. Draw the Box
    Box(
        modifier = modifier
            .then(if (width != Double.NaN.dp) Modifier.width(width) else Modifier.fillMaxWidth())
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark) // Base color
            .background(brush)       // Shimmer overlay
    )
}