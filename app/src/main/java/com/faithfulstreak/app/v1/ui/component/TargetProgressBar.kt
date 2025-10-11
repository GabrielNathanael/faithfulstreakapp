package com.faithfulstreak.app.v1.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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

    val barHeight = 28.dp
    val radius = 9.dp

    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight)
    ) {
        // Glow layer
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    shadowElevation = 8f
                    shape = RoundedCornerShape(radius)
                    clip = false
                }
        ) {
            if (animatedProgress.value > 0f) {
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primary.copy(alpha = 0.4f),
                            tertiary.copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * animatedProgress.value / 2, size.height / 2),
                        radius = size.height * 1.5f
                    ),
                    size = Size(size.width * animatedProgress.value, size.height),
                    cornerRadius = CornerRadius(radius.toPx())
                )
            }
        }

        // Main bar
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(radius))
        ) {
            // Background (gelap navy)
            drawRoundRect(
                color = surfaceVariant,
                size = size,
                cornerRadius = CornerRadius(radius.toPx())
            )

            // Progress gradient (emas lembut)
            if (animatedProgress.value > 0f) {
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            primary,
                            tertiary,
                            Color.White.copy(alpha = 0.85f)
                        )
                    ),
                    size = Size(size.width * animatedProgress.value, size.height),
                    cornerRadius = CornerRadius(radius.toPx())
                )
            }
        }
    }
}
