// app/src/main/java/com/faithfulstreak/app/v1/ui/screen/HistoryScreen.kt
package com.faithfulstreak.app.v1.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.faithfulstreak.app.v1.data.local.DatabaseProvider
import com.faithfulstreak.app.v1.data.local.HistoryEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(nav: NavController) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var items by remember { mutableStateOf(emptyList<HistoryEntity>()) }
    var longest by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        scope.launch {
            val dao = DatabaseProvider.get(ctx).historyDao()
            items = dao.getAll()
            longest = dao.getLongestStreak() ?: 0
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("History Streak") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { pad ->
        if (items.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) {
                Text("Belum ada history.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pad)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items) { h ->
                    val bg = when {
                        h.length == longest -> MaterialTheme.colorScheme.secondary.copy(0.25f)
                        h.length >= 14 -> MaterialTheme.colorScheme.primary.copy(0.12f)
                        h.length >= 7 -> Color(0xFFE3F2FD)
                        else -> Color(0xFFF1F4F8)
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bg, MaterialTheme.shapes.medium)
                            .padding(14.dp)
                    ) {
                        Text("${h.startDate} – ${h.endDate}")
                        Spacer(Modifier.height(4.dp))
                        Text("${h.length} hari", style = MaterialTheme.typography.titleMedium)
                        if (h.length == longest) {
                            Text("🏅 Longest streak", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}
