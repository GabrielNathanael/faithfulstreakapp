package com.faithfulstreak.app.v1.ui.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun WeeklyProgressRow(checkedDays: Set<Int>) {
    val labels = listOf("S", "S", "R", "K", "J", "S", "M")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        (1..7).forEach { day ->
            val active = checkedDays.contains(day)

            val bgColor = if (active) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }

            val textColor = if (active) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                if (active) {
                    Text(
                        text = "✓",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = textColor
                    )
                } else {
                    Text(
                        text = labels[day - 1],
                        style = MaterialTheme.typography.bodyLarge.copy( // ← lebih besar dari labelMedium
                            fontWeight = FontWeight.Bold                 // ← biar tebel
                        ),
                        color = textColor
                    )
                }
            }
        }
    }
}
