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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.faithfulstreak.app.v1.data.local.DatabaseProvider
import com.faithfulstreak.app.v1.data.local.HistoryEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Riwayat Streak") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { pad ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B0B0B))
                .padding(pad)
        ) {
            if (list.isEmpty()) {
                Text(
                    "Belum ada riwayat",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(list) { item ->
                        HistoryCard(item)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(item: HistoryEntity) {
    val isRelapse = item.type == "Relapse"
    val bg = if (isRelapse) Color(0x33FF3B30) else Color(0x3328CD41)
    val accent = if (isRelapse) Color(0xFFFF3B30) else Color(0xFF28CD41)

    Surface(
        color = bg,
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                text = item.type,
                color = accent,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Dari ${item.startDate} hingga ${item.endDate}",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            if (!isRelapse) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Durasi: ${item.length} hari",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
