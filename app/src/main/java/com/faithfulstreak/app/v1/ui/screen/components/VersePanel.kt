package com.faithfulstreak.app.v1.ui.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.faithfulstreak.app.v1.ui.theme.LoraFamily
import com.faithfulstreak.app.v1.util.Ayat
import com.faithfulstreak.app.v1.ui.screen.buildVerseText

@Composable
fun VersePanel(
    verse: Ayat,
    heightDp: Dp,
    onDrag: (Float) -> Unit
) {
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(heightDp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(20.dp)
            )
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount -> onDrag(dragAmount) }
            }
            .padding(16.dp)
    ) {
        Text(
            text = buildVerseText(verse),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = LoraFamily,
                fontWeight = FontWeight.Normal
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scroll)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "— ${verse.referenceString()} (TB)",
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = LoraFamily,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}
