package com.faithfulstreak.app.v1.ui.screen.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.faithfulstreak.app.R
import com.faithfulstreak.app.v1.data.local.PrefSnapshot   // ✅ FIX: ganti UiState ke PrefSnapshot

@Composable
fun FireSection(ui: PrefSnapshot, showSmoke: Boolean) {   // ✅ pakai PrefSnapshot
    val fireComp by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fire))
    val smokeComp by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.smoke))
    val fireAnim by animateLottieCompositionAsState(fireComp, iterations = LottieConstants.IterateForever)
    val smokeAnim by animateLottieCompositionAsState(smokeComp, iterations = LottieConstants.IterateForever)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (!showSmoke) {
            if (ui.count > 0) {  // ✅ count ada di PrefSnapshot
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFF8740).copy(alpha = 0.3f),
                                    Color(0xFFF59360).copy(alpha = 0.15f),
                                    Color.Transparent
                                ),
                                radius = 400f
                            )
                        )
                )
            }

            Crossfade(
                targetState = ui.count > 0,
                animationSpec = tween(800)
            ) { active ->
                if (active) {
                    LottieAnimation(
                        composition = fireComp,
                        progress = { fireAnim },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .align(Alignment.BottomCenter)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.fire),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .align(Alignment.BottomCenter)
                            .alpha(0.5f),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        } else {
            LottieAnimation(
                composition = smokeComp,
                progress = { smokeAnim },
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.8f)
            )
        }
    }
}
