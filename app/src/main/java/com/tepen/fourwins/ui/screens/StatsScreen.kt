package com.tepen.fourwins.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tepen.fourwins.MainViewModel
import com.tepen.fourwins.domain.FitnessCalculations
import com.tepen.fourwins.model.WinPillar
import com.tepen.fourwins.ui.components.MetricChip
import com.tepen.fourwins.ui.components.TacticalCard
import com.tepen.fourwins.ui.components.pillarColor
import com.tepen.fourwins.ui.theme.TacticalColors
import java.time.LocalDate

@Composable
fun StatsScreen(vm: MainViewModel) {
    val context = LocalContext.current
    val profile by vm.profile.collectAsState()
    val stats by vm.stats.collectAsState()
    val habits by vm.habits.collectAsState()
    val dailyWins by vm.dailyWins.collectAsState()
    val weightLogs by vm.weightLogs.collectAsState()
    val workoutLogs by vm.workoutLogs.collectAsState()
    val metrics by vm.recompMetrics.collectAsState()

    var showWeightDialog by remember { mutableStateOf(false) }
    var weightInput by remember(profile.weightKg) { mutableStateOf(profile.weightKg.toString()) }
    var backupStatus by remember { mutableStateOf<String?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openOutputStream(uri)?.use { out ->
                    out.write(vm.repo.exportBackupJson().toByteArray())
                }
            }.onSuccess {
                backupStatus = "Backup exported successfully."
            }.onFailure {
                backupStatus = "Export failed: ${it.message}"
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            runCatching {
                val txt = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                    ?: error("Unreadable file")
                vm.repo.importBackupJson(txt).getOrThrow()
            }.onSuccess { count ->
                backupStatus = "Restored $count daily win records."
            }.onFailure {
                backupStatus = "Import failed: ${it.message}"
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TacticalColors.AmoledBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text("TACTICAL STATS", style = MaterialTheme.typography.headlineMedium, color = TacticalColors.TextPrimary)
                Text("Win streaks, recomp progress & knee comfort logs", fontSize = 12.sp, color = TacticalColors.TextSecondary)
            }
        }

        // Top KPI Row
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricChip("TOTAL WINS", "${stats.totalAllTimeHabitWins}", TacticalColors.MentalCyan, Modifier.weight(1f))
                MetricChip("4/4 DAYS", "${stats.totalPerfectDays}", TacticalColors.ArcadeGold, Modifier.weight(1f))
                MetricChip("BEST STREAK", "${stats.bestActiveStreak}d", TacticalColors.PhysicalCrimson, Modifier.weight(1f))
            }
        }

        // 30-Day Heatmap
        item {
            TacticalCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("30-DAY WIN HEATMAP", style = MaterialTheme.typography.labelMedium, color = TacticalColors.MentalCyan)
                    Text("0 → 4 PILLARS", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = TacticalColors.TextMuted)
                }
                Spacer(Modifier.height(10.dp))
                val today = LocalDate.now()
                val days = (29 downTo 0).map { today.minusDays(it.toLong()) }
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    for (row in 0 until 3) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            for (col in 0 until 10) {
                                val d = days[row * 10 + col]
                                val entry = dailyWins[d.toString()]
                                val doneIds = entry?.completedHabitIds ?: emptySet()
                                val pillarsWon = WinPillar.entries.count { p ->
                                    FitnessCalculations.evaluatePillarWon(p, habits, doneIds, profile.pillarWinRule)
                                }
                                val cellColor = when (pillarsWon) {
                                    4 -> TacticalColors.ArcadeGold
                                    3 -> TacticalColors.FinancialEmerald
                                    2 -> TacticalColors.MentalCyan
                                    1 -> TacticalColors.PhysicalCrimson.copy(alpha = 0.7f)
                                    else -> TacticalColors.AmoledBlack
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(cellColor)
                                        .border(1.dp, TacticalColors.BorderHairline, RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${d.dayOfMonth}",
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (pillarsWon >= 2) TacticalColors.AmoledBlack else TacticalColors.TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4-Pillar Balance (Last 30 Days)
        item {
            TacticalCard {
                Text("4-PILLAR BALANCE (30D)", style = MaterialTheme.typography.labelMedium, color = TacticalColors.TextSecondary)
                Spacer(Modifier.height(10.dp))
                WinPillar.entries.forEach { pillar ->
                    val count = stats.pillarWinCountsLast30[pillar] ?: 0
                    val c = pillarColor(pillar)
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(pillar.title, color = c, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("$count / 30 days", color = TacticalColors.TextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { (count / 30f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(50)),
                            color = c,
                            trackColor = TacticalColors.AmoledBlack
                        )
                    }
                }
            }
        }

        // Body Recomp & Weight Trend
        item {
            TacticalCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("BODY RECOMP TRACKER", style = MaterialTheme.typography.labelMedium, color = TacticalColors.FinancialEmerald)
                        Text(
                            text = "${String.format("%.1f", profile.weightKg)} kg • BMI ${String.format("%.1f", metrics.bmi)} • Target ${String.format("%.1f", profile.targetWeightKg)} kg",
                            fontSize = 12.sp,
                            color = TacticalColors.TextPrimary
                        )
                    }
                    Button(
                        onClick = { showWeightDialog = true },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TacticalColors.FinancialEmerald)
                    ) {
                        Icon(Icons.Rounded.Add, null, tint = TacticalColors.AmoledBlack, modifier = Modifier.size(16.dp))
                        Text("LOG KG", color = TacticalColors.AmoledBlack, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                if (weightLogs.size >= 2) {
                    Spacer(Modifier.height(12.dp))
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(TacticalColors.AmoledBlack)
                            .padding(12.dp)
                    ) {
                        val minW = (weightLogs.minOf { it.weightKg } - 1f)
                        val maxW = (weightLogs.maxOf { it.weightKg } + 1f).coerceAtLeast(minW + 2f)
                        val path = Path()
                        weightLogs.forEachIndexed { i, entry ->
                            val x = (i.toFloat() / (weightLogs.size - 1)) * size.width
                            val y = size.height - ((entry.weightKg - minW) / (maxW - minW)) * size.height
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            drawCircle(TacticalColors.FinancialEmerald, radius = 5.dp.toPx(), center = Offset(x, y))
                        }
                        drawPath(path, color = TacticalColors.FinancialEmerald, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                    }
                }
            }
        }

        // Recent Workout Logs + Knee Status
        item {
            Text(
                text = "RECENT TRAINING LOGS (${workoutLogs.size})",
                style = MaterialTheme.typography.labelMedium,
                color = TacticalColors.TextSecondary
            )
        }
        if (workoutLogs.isEmpty()) {
            item {
                TacticalCard {
                    Text(
                        text = "No workouts logged yet. Complete a session from the Today or Train tab to track duration & knee comfort.",
                        fontSize = 12.sp,
                        color = TacticalColors.TextMuted
                    )
                }
            }
        } else {
            items(workoutLogs.take(10), key = { it.id }) { log ->
                TacticalCard {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(log.programTitle, color = TacticalColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(log.dateIso, color = TacticalColors.TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${log.durationMinutes} min • ${log.setsCompleted} sets", fontSize = 12.sp, color = TacticalColors.TextSecondary)
                        Text(
                            text = "Knee: ${log.kneeComfort.label}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(log.kneeComfort.colorHex)
                        )
                    }
                }
            }
        }

        // Offline JSON Backup / Restore
        item {
            TacticalCard {
                Text("OFFLINE BACKUP & RESTORE", style = MaterialTheme.typography.labelMedium, color = TacticalColors.MentalCyan)
                Text(
                    text = "Export your 4-Wins history and weight logs to a JSON file anytime.",
                    fontSize = 12.sp,
                    color = TacticalColors.TextSecondary
                )
                Spacer(Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { exportLauncher.launch("4wins_backup_${LocalDate.now()}.json") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Rounded.Download, null, modifier = Modifier.size(16.dp), tint = TacticalColors.MentalCyan)
                        Spacer(Modifier.width(4.dp))
                        Text("EXPORT", color = TacticalColors.MentalCyan, fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = { importLauncher.launch(arrayOf("application/json", "*/*")) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Rounded.Upload, null, modifier = Modifier.size(16.dp), tint = TacticalColors.ArcadeGold)
                        Spacer(Modifier.width(4.dp))
                        Text("IMPORT", color = TacticalColors.ArcadeGold, fontSize = 11.sp)
                    }
                }
                if (backupStatus != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(backupStatus!!, fontSize = 11.sp, color = TacticalColors.FinancialEmerald)
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }

    if (showWeightDialog) {
        AlertDialog(
            onDismissRequest = { showWeightDialog = false },
            containerColor = TacticalColors.CardDark,
            title = { Text("Log Current Weight (kg)", color = TacticalColors.TextPrimary) },
            text = {
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val w = weightInput.toFloatOrNull()
                        if (w != null && w in 30f..250f) {
                            vm.logWeight(w)
                        }
                        showWeightDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TacticalColors.FinancialEmerald)
                ) {
                    Text("SAVE", color = TacticalColors.AmoledBlack, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWeightDialog = false }) { Text("Cancel") }
            }
        )
    }
}
