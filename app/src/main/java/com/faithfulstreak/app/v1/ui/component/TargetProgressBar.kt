package com.faithfulstreak.app.v1.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TargetProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val animatedProgress = animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        label = "progressAnim"
    )

    val barHeight = 20.dp
    val radius = 8.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight)
            .clip(RoundedCornerShape(radius))
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            // background abu gelap
            drawRoundRect(
                color = Color(0xFF2C2C2C),
                size = size,
                cornerRadius = CornerRadius(radius.toPx())
            )

            // progress aktif putih
            drawRoundRect(
                color = Color.White,
                size = Size(size.width * animatedProgress.value, size.height),
                cornerRadius = CornerRadius(radius.toPx())
            )
        }
    }
}
