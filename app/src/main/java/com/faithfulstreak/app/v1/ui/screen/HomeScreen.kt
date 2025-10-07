package com.faithfulstreak.app.v1.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.faithfulstreak.app.R
import com.faithfulstreak.app.v1.ui.component.TargetProgressBar
import com.faithfulstreak.app.v1.viewmodel.StreakViewModel
import com.faithfulstreak.app.v1.viewmodel.UiEvent
import kotlinx.coroutines.delay
import java.time.LocalDate
import androidx.compose.ui.text.font.FontWeight
import com.faithfulstreak.app.v1.ui.theme.LoraFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    nav: NavController,
    vm: StreakViewModel
) {
    val ui by vm.ui.collectAsStateWithLifecycle()

    var showConfetti by remember { mutableStateOf(false) }
    var versePanelHeight by remember { mutableStateOf(140.dp) }
    var showExtend by remember { mutableStateOf(false) }
    var nextTarget by remember { mutableIntStateOf(ui.target) }
    var showSmoke by remember { mutableStateOf(false) }

    // tambahan untuk target awal
    var showSetTarget by remember { mutableStateOf(ui.target <= 0) }
    val presetTargets = listOf(7, 14, 30, 60, 100, 365)

    // Lottie animations
    val fireComp by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.fire))
    val smokeComp by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.smoke))
    val confettiComp by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.confetti))
    val fireAnim by animateLottieCompositionAsState(fireComp, iterations = LottieConstants.IterateForever)
    val smokeAnim by animateLottieCompositionAsState(smokeComp, iterations = LottieConstants.IterateForever)

    // Event listener
    LaunchedEffect(Unit) {
        vm.events.collect { ev ->
            when (ev) {
                UiEvent.ReachedTarget -> {
                    nextTarget = listOf(7, 14, 30, 60, 100, 365)
                        .firstOrNull { it > ui.target } ?: (ui.target + 365)
                    showExtend = true
                }

                UiEvent.Relapsed -> {
                    showSmoke = true
                    showConfetti = false
                    delay(1500)
                    showSmoke = false
                }
            }
        }
    }

    // aktifkan bypass testing otomatis
    LaunchedEffect(Unit) { vm.enableBypassTesting() }

    // Dialog naikkan target
    if (showExtend) {
        AlertDialog(
            onDismissRequest = { showExtend = false },
            title = { Text("Target tercapai") },
            text = { Text("Naikkan target jadi $nextTarget hari?") },
            confirmButton = {
                TextButton(onClick = { vm.extendTargetToNext(); showExtend = false }) {
                    Text("Naikkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExtend = false }) {
                    Text("Nanti")
                }
            }
        )
    }

    // Dialog set target pertama kali
    if (showSetTarget) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Atur Target Awal") },
            text = {
                Column {
                    Text("Pilih target hari pertama kamu:")
                    Spacer(Modifier.height(8.dp))
                    presetTargets.forEach { t ->
                        Button(
                            onClick = {
                                vm.setInitialTarget(t)
                                showSetTarget = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text("$t hari")
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Faithful Streak") },
                actions = {
                    IconButton(onClick = { nav.navigate("history") }) {
                        Icon(Icons.Rounded.History, contentDescription = "History")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))

            // Angka streak
            Text(
                text = "${ui.count} hari",
                style = MaterialTheme.typography.displayMedium,
                color = Color(0xFFFF8A00),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            // Progress bar mingguan
            val progress = ui.weekDays.size / 7f
            TargetProgressBar(progress = progress)

            Spacer(Modifier.height(10.dp))
            WeeklyProgressRow(checkedDays = ui.weekDays)
            Spacer(Modifier.height(16.dp))

            // Visual utama (🔥 fire transition fix)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                contentAlignment = Alignment.BottomCenter // ✅ sejajarkan dasar api
            ) {
                if (!showSmoke) {
                    val showFire = ui.count > 0
                    Crossfade(
                        targetState = showFire,
                        animationSpec = tween(durationMillis = 800),
                        label = "fire-crossfade"
                    ) { active ->
                        if (active) {
                            // 🔥 Lottie fire animation
                            LottieAnimation(
                                composition = fireComp,
                                progress = { fireAnim },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                                    .align(Alignment.BottomCenter)
                            )
                        } else {
                            // 🧊 Static fire.png
                            Image(
                                painter = painterResource(id = R.drawable.fire),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                                    .align(Alignment.BottomCenter),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }

                if (showSmoke) {
                    LottieAnimation(
                        composition = smokeComp,
                        progress = { smokeAnim },
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(0.8f)
                    )
                }

                if (showConfetti) {
                    val confettiAnim by animateLottieCompositionAsState(confettiComp, iterations = 1)
                    LottieAnimation(
                        composition = confettiComp,
                        progress = { confettiAnim },
                        modifier = Modifier.fillMaxSize()
                    )
                    if (confettiAnim == 1f) {
                        LaunchedEffect(Unit) { showConfetti = false }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Tombol aksi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        val before = ui.count
                        vm.checkInToday()
                        if (before + 1 == ui.target) {
                            showConfetti = true
                        }
                    },
                    enabled = !(ui.last == LocalDate.now() && !ui.bypass),
                    modifier = Modifier.weight(1f)
                ) { Text("Berhasil Hari Ini") }

                OutlinedButton(
                    onClick = { vm.relapse() },
                    modifier = Modifier.weight(1f)
                ) { Text("Relapse") }
            }

            Spacer(Modifier.height(16.dp))

            // Panel ayat
            VersePanel(
                verseText = ui.verse.firman,
                reference = "${ui.verse.kitab} ${ui.verse.pasal}:${ui.verse.ayat}",
                heightDp = versePanelHeight,
                onDrag = { deltaY ->
                    val newH = (versePanelHeight.value + deltaY / 2).coerceIn(100f, 280f)
                    versePanelHeight = newH.dp
                }
            )

            Spacer(Modifier.height(16.dp))
            Text(
                "Target: ${ui.target} hari",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )
        }
    }
}

@Composable
private fun WeeklyProgressRow(checkedDays: Set<Int>) {
    val labels = listOf("S", "S", "R", "K", "J", "S", "M") // Senin–Minggu
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        (1..7).forEach { day ->
            val active = checkedDays.contains(day)
            val bg = if (active) Color.White else Color(0xFF2C2C2C)
            val fg = if (active) Color.Black else Color.White
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = labels[day - 1],
                    style = MaterialTheme.typography.labelMedium,
                    color = fg
                )
            }
        }
    }
}

@Composable
private fun VersePanel(
    verseText: String,
    reference: String,
    heightDp: androidx.compose.ui.unit.Dp,
    onDrag: (Float) -> Unit
) {
    val scroll = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(heightDp)
            .background(
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(16.dp)
            )
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount -> onDrag(dragAmount) }
            }
            .padding(14.dp)
    ) {
        Text(
            text = verseText,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = LoraFamily,
                fontWeight = FontWeight.Normal
            ),
            color = Color.White,
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scroll)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "— $reference (TB)",
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = LoraFamily,
                fontWeight = FontWeight.Bold
            ),
            color = Color.White
        )

    }
}
