package com.faithfulstreak.app.v1.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.faithfulstreak.app.v1.ui.component.TargetProgressBar
import com.faithfulstreak.app.v1.ui.screen.components.ConfettiDialog
import com.faithfulstreak.app.v1.ui.screen.components.FireSection
import com.faithfulstreak.app.v1.ui.screen.components.ModernDialog
import com.faithfulstreak.app.v1.ui.screen.components.VersePanel
import com.faithfulstreak.app.v1.ui.screen.components.WeeklyProgressRow
import com.faithfulstreak.app.v1.ui.theme.LoraFamily
import com.faithfulstreak.app.v1.util.Ayat
import com.faithfulstreak.app.v1.util.MotivationProvider
import com.faithfulstreak.app.v1.util.nextThreeTargets
import com.faithfulstreak.app.v1.viewmodel.StreakViewModel
import com.faithfulstreak.app.v1.viewmodel.UiEvent
import kotlinx.coroutines.delay
import java.time.LocalDate
import androidx.compose.foundation.layout.systemBars
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
    var nextTargets by remember { mutableStateOf(listOf<Int>()) }
    var showSmoke by remember { mutableStateOf(false) }
    var showSetTargetDialog by remember { mutableStateOf(false) }


    var showMotivation by remember { mutableStateOf(false) }
    var motivationText by remember { mutableStateOf("") }
    var motivationVerse by remember { mutableStateOf(Ayat("Kejadian", 1, 1, 1, "")) }

    // Event listener
    LaunchedEffect(Unit) {
        vm.events.collect { ev ->
            when (ev) {
                UiEvent.ReachedTarget -> {
                    nextTargets = nextThreeTargets(ui.target)
                    showExtend = true
                }

                UiEvent.Relapsed -> {
                    showSmoke = true
                    showConfetti = false
                    delay(1000)
                    motivationText = MotivationProvider.random()
                    motivationVerse = vm.getSingleVerse()
                    showMotivation = true
                }
            }
        }
    }

//    LaunchedEffect(Unit) { vm.enableBypassTesting() }
    LaunchedEffect(Unit) {

        vm.disableBypassTesting()
    }
    // ==============================
    // DIALOG: Target Tercapai
    // ==============================
    if (showExtend) {
        ModernDialog(
            onDismissRequest = { showExtend = false },
            title = "Target Tercapai!",
            content = {
                Column {
                    Text(
                        "Pilih target berikutnya:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Spacer(Modifier.height(16.dp))

                    // Top Row - 2 buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        nextTargets.take(2).forEach { target ->
                            Button(
                                onClick = {
                                    vm.extendTargetTo(target)
                                    showExtend = false
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                ),
                                border = ButtonDefaults.outlinedButtonBorder(enabled = true),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        "$target",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 22.sp
                                        ),
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Bottom Row - 1 button (centered)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        nextTargets.drop(2).forEach { target ->
                            Button(
                                onClick = {
                                    vm.extendTargetTo(target)
                                    showExtend = false
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                ),
                                border = ButtonDefaults.outlinedButtonBorder(enabled = true),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        "$target",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 22.sp
                                        ),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        "hari",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExtend = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                ) { Text("Nanti") }
            }
        )
    }

    // ==============================
    // DIALOG: Motivasi Setelah Relapse
    // ==============================
    if (showMotivation) {
        ModernDialog(
            onDismissRequest = {
                showMotivation = false
                showSmoke = false
            },
            title = "Bangkit Lagi",
            content = {
                Column {
                    Text(
                        motivationText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = buildVerseText(motivationVerse),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = LoraFamily
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.95f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "— ${motivationVerse.referenceString()} (TB)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = LoraFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showMotivation = false
                        showSmoke = false
                        showSetTargetDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) { Text("Mulai Lagi") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showMotivation = false
                        showSmoke = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                ) { Text("Nanti") }
            }
        )
    }

    // ==============================
    // DIALOG: Set Target Manual
    // ==============================
    if (showSetTargetDialog) {
        ModernDialog(
            onDismissRequest = { showSetTargetDialog = false },
            title = "Atur Target",
            content = {
                Column {
                    Text(
                        "Pilih target hari:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf(7, 14, 30).forEach { t ->
                            Button(
                                onClick = {
                                    vm.setTarget(t)
                                    showSetTargetDialog = false
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                ),
                                border = ButtonDefaults.outlinedButtonBorder(enabled = true),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    "$t",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSetTargetDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                ) { Text("Batal") }
            }
        )
    }


    // ==============================
    // MAIN UI
    // ==============================
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Faithful Streak")
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Target: ${ui.target} hari",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { nav.navigate("history") }) {
                        Icon(
                            Icons.Rounded.History,
                            contentDescription = "History",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,

        contentWindowInsets = WindowInsets.systemBars
    )
    { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                val years = ui.count / 365
                val remainingDays = ui.count % 365

                if (years > 0) {
                    // Tampilkan tahun
                    Text(
                        text = "$years",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (years == 1) "Year" else "Years",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    // Tampilkan sisa hari jika ada
                    if (remainingDays > 0) {
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "$remainingDays",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (remainingDays == 1) "Day" else "Days",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Tampilkan hari saja jika kurang dari setahun
                    Text(
                        text = "${ui.count}",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (ui.count == 1) "Day" else "Days",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            val progress = ui.weekDays.size / 7f
            TargetProgressBar(progress)
            Spacer(Modifier.height(10.dp))

            WeeklyProgressRow(ui.weekDays)
            Spacer(Modifier.height(16.dp))

            FireSection(ui = ui, showSmoke = showSmoke)

            Spacer(Modifier.height(8.dp))

            // Tombol aksi
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        val before = ui.count
                        vm.checkInToday()
                        if (before + 1 == ui.target) showConfetti = true
                    },
                    enabled = ui.target > 0 && (ui.count == 0 || ui.last != LocalDate.now() || ui.bypass),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Berhasil Hari Ini",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Button Relapse - cuma muncul kalau count > 0
                    if (ui.count > 0) {
                        OutlinedButton(
                            onClick = { vm.relapse() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true),
                            shape = RoundedCornerShape(14.dp)
                        ) { Text("Relapse") }
                    }

                    // Button Set Target - cuma muncul kalau count == 0
                    if (ui.target == 0) {
                        OutlinedButton(
                            onClick = { showSetTargetDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true),
                            shape = RoundedCornerShape(14.dp)
                        ) { Text("Set Target") }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            VersePanel(
                verse = ui.verse,
                heightDp = versePanelHeight,
                onDrag = { deltaY ->
                    val newH = (versePanelHeight.value + deltaY / 2).coerceIn(100f, 280f)
                    versePanelHeight = newH.dp
                }
            )
        }
    }

    // Confetti overlay
    ConfettiDialog(showConfetti) { showConfetti = false }
}

@Composable
fun buildVerseText(verse: Ayat) = buildAnnotatedString {
    if (verse.isSingleVerse()) {
        append(verse.firman)
    } else {
        verse.detailAyat.forEachIndexed { index, detail ->
            withStyle(
                style = SpanStyle(
                    fontSize = 10.sp,
                    baselineShift = BaselineShift.Superscript,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            ) { append(detail.ayat.toString()) }
            append(detail.teks)
            if (index < verse.detailAyat.size - 1) append(" ")
        }
    }
}