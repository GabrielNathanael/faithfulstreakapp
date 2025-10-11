package com.faithfulstreak.app.v1.ui.screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.*
import androidx.compose.runtime.*
import com.faithfulstreak.app.R

@Composable
fun ConfettiDialog(showConfetti: Boolean, onEnd: () -> Unit) {
    if (!showConfetti) return

    val confettiComp by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.confetti))
    val confettiAnim by animateLottieCompositionAsState(confettiComp, iterations = 1)

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(alpha = 0.99f),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = confettiComp,
                progress = { confettiAnim },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    if (confettiAnim == 1f) {
        LaunchedEffect(Unit) { onEnd() }
    }
}
