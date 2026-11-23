package com.tepen.fourwins.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tepen.fourwins.MainViewModel
import com.tepen.fourwins.model.Equipment
import com.tepen.fourwins.model.PillarWinRule
import com.tepen.fourwins.ui.components.MetricChip
import com.tepen.fourwins.ui.components.TacticalCard
import com.tepen.fourwins.ui.theme.TacticalColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(vm: MainViewModel) {
    val profile by vm.profile.collectAsState()
    val metrics by vm.recompMetrics.collectAsState()

    var ageInput by remember(profile.age) { mutableStateOf(profile.age.toString()) }
    var heightInput by remember(profile.heightCm) { mutableStateOf(profile.heightCm.toString()) }
    var weightInput by remember(profile.weightKg) { mutableStateOf(profile.weightKg.toString()) }
    var targetInput by remember(profile.targetWeightKg) { mutableStateOf(profile.targetWeightKg.toString()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text("Personal Protocol", style = MaterialTheme.typography.headlineMedium, color = TacticalColors.TextPrimary)
                Text("Tailored biometrics, knee protection, audio & reminders", fontSize = 13.sp, color = TacticalColors.TextSecondary)
            }
        }

        // Computed Recomp Targets
        item {
            TacticalCard(borderColor = TacticalColors.MentalCyan.copy(alpha = 0.5f)) {
                Text("TAILORED RECOMP TARGETS", style = MaterialTheme.typography.labelMedium, color = TacticalColors.MentalCyan)
                Text(metrics.bmiLabel, fontSize = 13.sp, color = TacticalColors.ArcadeGold, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricChip("DAILY KCAL", "${metrics.targetKcal}", TacticalColors.PhysicalCrimson, Modifier.weight(1f))
                    MetricChip("PROTEIN", "${metrics.proteinGrams}g", TacticalColors.FinancialEmerald, Modifier.weight(1f))
                    MetricChip("BMI", String.format("%.1f", metrics.bmi), TacticalColors.MentalCyan, Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Est. BMR: ${metrics.bmrKcal} kcal • TDEE: ${metrics.tdeeKcal} kcal\nDaily Target: ${metrics.stepsOrCardioTarget}",
                    fontSize = 11.sp,
                    color = TacticalColors.TextSecondary
                )
            }
        }

        // Biometrics Form
        item {
            TacticalCard {
                Text("BODY STATS (5'4\" = 162.5 CM)", style = MaterialTheme.typography.labelMedium, color = TacticalColors.TextSecondary)
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ageInput,
                        onValueChange = { ageInput = it.filter { c -> c.isDigit() } },
                        label = { Text("Age") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = heightInput,
                        onValueChange = { heightInput = it },
                        label = { Text("Height cm") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Weight kg") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = targetInput,
                        onValueChange = { targetInput = it },
                        label = { Text("Goal kg") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("BMR Formula Profile", fontSize = 13.sp, color = TacticalColors.TextPrimary)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = profile.isMale,
                            onClick = { vm.updateProfile(profile.copy(isMale = true)) },
                            label = { Text("Male", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = !profile.isMale,
                            onClick = { vm.updateProfile(profile.copy(isMale = false)) },
                            label = { Text("Female", fontSize = 11.sp) }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        vm.updateProfile(
                            profile.copy(
                                age = ageInput.toIntOrNull()?.coerceIn(16, 99) ?: profile.age,
                                heightCm = heightInput.toFloatOrNull()?.coerceIn(120f, 230f) ?: profile.heightCm,
                                weightKg = weightInput.toFloatOrNull()?.coerceIn(35f, 250f) ?: profile.weightKg,
                                targetWeightKg = targetInput.toFloatOrNull()?.coerceIn(35f, 250f) ?: profile.targetWeightKg
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TacticalColors.MentalCyan)
                ) {
                    Text("UPDATE BIOMETRICS", color = TacticalColors.AmoledBlack, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Win Rules & Knee Protection
        item {
            TacticalCard {
                Text("TRAINING & WIN RULES", style = MaterialTheme.typography.labelMedium, color = TacticalColors.PhysicalCrimson)
                Spacer(Modifier.height(6.dp))
                SettingToggleRow(
                    title = "Knee Protection Mode",
                    subtitle = "Prioritizes low-impact swaps & flags jumping workouts",
                    checked = profile.kneeSafeMode,
                    onCheckedChange = { vm.updateProfile(profile.copy(kneeSafeMode = it)) }
                )
                Spacer(Modifier.height(8.dp))
                Text("Pillar Win Requirement", fontSize = 13.sp, color = TacticalColors.TextPrimary, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PillarWinRule.entries.forEach { rule ->
                        FilterChip(
                            selected = profile.pillarWinRule == rule,
                            onClick = { vm.updateProfile(profile.copy(pillarWinRule = rule)) },
                            label = { Text(rule.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TacticalColors.ArcadeGold,
                                selectedLabelColor = TacticalColors.AmoledBlack
                            )
                        )
                    }
                }
            }
        }

        // Guided Timer Audio & Haptics
        item {
            TacticalCard {
                Text("GUIDED TIMER CUES", style = MaterialTheme.typography.labelMedium, color = TacticalColors.SpiritualAmber)
                Spacer(Modifier.height(6.dp))
                SettingToggleRow(
                    title = "Voice Coach (TTS)",
                    subtitle = "Speaks exercise transitions, halfway & 3-2-1 countdown",
                    checked = profile.voiceCoachEnabled,
                    onCheckedChange = { vm.updateProfile(profile.copy(voiceCoachEnabled = it)) }
                )
                SettingToggleRow(
                    title = "Countdown Beeps",
                    subtitle = "Crisp audio cues at 3-2-1 and set completion",
                    checked = profile.beepEnabled,
                    onCheckedChange = { vm.updateProfile(profile.copy(beepEnabled = it)) }
                )
                SettingToggleRow(
                    title = "Tactile Haptics",
                    subtitle = "Vibration pulses on checkoff and timer transitions",
                    checked = profile.hapticsEnabled,
                    onCheckedChange = { vm.updateProfile(profile.copy(hapticsEnabled = it)) }
                )
            }
        }

        // Daily Notifications
        item {
            TacticalCard {
                Text("DAILY REMINDER NOTIFICATIONS", style = MaterialTheme.typography.labelMedium, color = TacticalColors.FinancialEmerald)
                Spacer(Modifier.height(6.dp))
                SettingToggleRow(
                    title = "Morning Kickoff (${String.format("%02d:%02d", profile.morningHour, profile.morningMinute)})",
                    subtitle = "Daily prompt to start walk or gym session",
                    checked = profile.morningReminderEnabled,
                    onCheckedChange = { vm.updateProfile(profile.copy(morningReminderEnabled = it)) }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(6, 7, 8, 9).forEach { hr ->
                        FilterChip(
                            selected = profile.morningHour == hr,
                            onClick = { vm.updateProfile(profile.copy(morningHour = hr, morningMinute = 30)) },
                            label = { Text("${hr}:30 AM", fontSize = 11.sp) }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                SettingToggleRow(
                    title = "Evening 4-Wins Check-In (${String.format("%02d:%02d", profile.eveningHour, profile.eveningMinute)})",
                    subtitle = "Reminder to lock in Physical, Mental, Financial & Spiritual wins",
                    checked = profile.eveningReminderEnabled,
                    onCheckedChange = { vm.updateProfile(profile.copy(eveningReminderEnabled = it)) }
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(19, 20, 21, 22).forEach { hr ->
                        FilterChip(
                            selected = profile.eveningHour == hr,
                            onClick = { vm.updateProfile(profile.copy(eveningHour = hr, eveningMinute = 30)) },
                            label = { Text("${hr - 12}:30 PM", fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // Equipment Checklist
        item {
            TacticalCard {
                Text("MY GYM EQUIPMENT", style = MaterialTheme.typography.labelMedium, color = TacticalColors.TextSecondary)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Equipment.entries.forEach { eq ->
                        val active = eq in profile.availableEquipment
                        FilterChip(
                            selected = active,
                            onClick = {
                                val next = profile.availableEquipment.toMutableSet()
                                if (active) next.remove(eq) else next.add(eq)
                                vm.updateProfile(profile.copy(availableEquipment = next))
                            },
                            label = { Text(eq.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TacticalColors.PhysicalCrimson.copy(alpha = 0.24f),
                                selectedLabelColor = TacticalColors.TextPrimary
                            )
                        )
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TacticalColors.TextPrimary)
            Text(subtitle, fontSize = 11.sp, color = TacticalColors.TextSecondary)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TacticalColors.AmoledBlack,
                checkedTrackColor = TacticalColors.MentalCyan
            )
        )
    }
}
