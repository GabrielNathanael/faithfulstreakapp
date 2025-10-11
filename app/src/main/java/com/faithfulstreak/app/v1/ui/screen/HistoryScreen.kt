package com.faithfulstreak.app.v1.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.faithfulstreak.app.v1.data.local.DatabaseProvider
import com.faithfulstreak.app.v1.data.local.HistoryEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class FaithStage(
    val title: String,
    val baseGradient: List<Color>
)

val stages = listOf(
    FaithStage("Ignition", listOf(Color(0xFF4E342E), Color(0xFFFFC87A))),
    FaithStage("Commitment", listOf(Color(0xFFFFE082), Color(0xFFFFD54F))),
    FaithStage("Resilience", listOf(Color(0xFF81C784), Color(0xFFFFF59D))),
    FaithStage("Purity", listOf(Color(0xFF81D4FA), Color(0xFFE1F5FE))),
    FaithStage("Steadfast", listOf(Color(0xFF9575CD), Color(0xFFD1C4E9))),
    FaithStage("Faith Forged", listOf(Color(0xFFFFB300), Color(0xFFFFE082))),
    FaithStage("Unshaken", listOf(Color(0xFFF4511E), Color(0xFFFFB74D))),
    FaithStage("Eternal Flame", listOf(Color(0xFFFFF9C4), Color(0xFFFFF176)))
)

fun getFaithStage(days: Int): FaithStage {
    val idx = when {
        days <= 3 -> 0
        days <= 7 -> 1
        days <= 14 -> 2
        days <= 30 -> 3
        days <= 90 -> 4
        days <= 180 -> 5
        days <= 365 -> 6
        else -> 7
    }
    return stages[idx]
}

fun getFaithMilestone(days: Int): Int {
    return (days / 365)
}

fun formatDate(dateStr: String): String {
    return try {
        val date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        date.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH))
    } catch (_: Exception) {
        dateStr
    }
}

enum class BadgeType {
    CURRENT, LONGEST, SHORTEST, NONE
}

data class BadgeInfo(
    val type: BadgeType,
    val icon: ImageVector?,
    val label: String?
)

fun getBadgeInfo(type: BadgeType): BadgeInfo {
    return when (type) {
        BadgeType.CURRENT -> BadgeInfo(type, Icons.Rounded.Whatshot, "Current")
        BadgeType.LONGEST -> BadgeInfo(type, Icons.Rounded.EmojiEvents, "Longest")
        BadgeType.SHORTEST -> BadgeInfo(type, Icons.Rounded.FavoriteBorder, "Shortest")
        BadgeType.NONE -> BadgeInfo(type, null, null)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(nav: NavController) {
    val context = LocalContext.current
    val dao = remember { DatabaseProvider.db(context).historyDao() }
    val scope = rememberCoroutineScope()

    var list by remember { mutableStateOf<List<HistoryEntity>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            dao.getAll().collectLatest { logs ->
                list = logs
            }
        }
    }

    // Determine badge types
    val sortedList = remember(list) {
        val current = list.firstOrNull { it.isCurrent }
        val finished = list.filter { !it.isCurrent }

        val longest = finished.maxByOrNull { it.streakLength }
        val shortest = if (finished.size >= 2) finished.minByOrNull { it.streakLength } else null

        // Sort finished by end date (newest first)
        val sortedFinished = finished.sortedByDescending { it.endDate }

        // Build final list with badge info
        buildList {
            current?.let { add(it to BadgeType.CURRENT) }

            sortedFinished.forEach { item ->
                val badgeType = when {
                    item.id == longest?.id && item.id == shortest?.id -> BadgeType.LONGEST
                    item.id == longest?.id -> BadgeType.LONGEST
                    item.id == shortest?.id -> BadgeType.SHORTEST
                    else -> BadgeType.NONE
                }
                add(item to badgeType)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Faith Journey") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF171312)
                )
            )
        },
        containerColor = Color(0xFF171312)
    ) { pad ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
        ) {
            if (list.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Belum ada riwayat",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Mulai streak pertamamu!",
                        color = Color.Gray.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(sortedList) { (item, badgeType) ->
                        FaithHistoryCard(history = item, badgeType = badgeType)
                    }
                }
            }
        }
    }
}

@Composable
fun FaithHistoryCard(history: HistoryEntity, badgeType: BadgeType) {
    val stage = getFaithStage(history.streakLength)
    val milestone = getFaithMilestone(history.streakLength)
    val gradient = Brush.verticalGradient(colors = stage.baseGradient)
    val badgeInfo = getBadgeInfo(badgeType)

    // Divine light overlay (radial gradient from center)
    val overlayGradient = Brush.radialGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.15f),
            Color.Transparent
        ),
        center = Offset(0.5f, 0.3f),
        radius = 800f
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color.White.copy(alpha = 0.2f),
                spotColor = Color.White.copy(alpha = 0.2f)
            )
            .background(gradient, RoundedCornerShape(24.dp))
            .background(overlayGradient)
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.4f),
                        Color.White.copy(alpha = 0.15f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp)
    ) {
        // Badge (top right)
        if (badgeInfo.icon != null && badgeInfo.label != null) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(
                        Color.White.copy(alpha = 0.25f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = badgeInfo.icon,
                    contentDescription = badgeInfo.label,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = badgeInfo.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.6f),
                            offset = Offset(1f, 1f),
                            blurRadius = 3f
                        )
                    )
                )
            }
        }

        Column(
            modifier = Modifier.padding(end = if (badgeInfo.icon != null) 100.dp else 0.dp)
        ) {
            Text(
                text = stage.title + if (milestone > 0) " · Year $milestone" else "",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.7f),
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                )
            )

            Spacer(Modifier.height(12.dp))

            // Prominent day count
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${history.streakLength}",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 56.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.7f),
                            offset = Offset(2f, 2f),
                            blurRadius = 5f
                        ),
                        lineHeight = 56.sp
                    )
                )
                Text(
                    text = "Days of Faith",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White.copy(alpha = 0.95f),
                        fontWeight = FontWeight.SemiBold,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.6f),
                            offset = Offset(1.5f, 1.5f),
                            blurRadius = 3f
                        )
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "${formatDate(history.startDate)} → ${formatDate(history.endDate)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.85f),
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.6f),
                        offset = Offset(1f, 1f),
                        blurRadius = 2f
                    )
                )
            )
        }
    }
}