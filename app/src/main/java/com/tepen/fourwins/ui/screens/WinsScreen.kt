package com.tepen.fourwins.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tepen.fourwins.MainViewModel
import com.tepen.fourwins.domain.FitnessCalculations
import com.tepen.fourwins.model.WinHabit
import com.tepen.fourwins.model.WinPillar
import com.tepen.fourwins.model.WorkoutProgram
import com.tepen.fourwins.ui.components.FourWinsArcadeWheel
import com.tepen.fourwins.ui.components.KneeSafetyPill
import com.tepen.fourwins.ui.components.TacticalCard
import com.tepen.fourwins.ui.components.pillarColor
import com.tepen.fourwins.ui.components.pillarIcon
import com.tepen.fourwins.ui.theme.TacticalColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WinsScreen(
    vm: MainViewModel,
    onStartWorkout: (WorkoutProgram) -> Unit
) {
    val profile by vm.profile.collectAsState()
    val habits by vm.habits.collectAsState()
    val dailyWins by vm.dailyWins.collectAsState()
    val stats by vm.stats.collectAsState()
    val selectedDateIso by vm.selectedDate.collectAsState()

    val selectedDate = remember(selectedDateIso) {
        runCatching { LocalDate.parse(selectedDateIso) }.getOrDefault(LocalDate.now())
    }
    val isToday = selectedDate == LocalDate.now()
    val entry = dailyWins[selectedDateIso]
    val completedIds = entry?.completedHabitIds ?: emptySet()

    val dayPillarStatus = remember(habits, completedIds, profile.pillarWinRule) {
        WinPillar.entries.associateWith { pillar ->
            FitnessCalculations.evaluatePillarWon(pillar, habits, completedIds, profile.pillarWinRule)
        }
    }
    val dayPillarsWon = dayPillarStatus.values.count { it }

    var editingPillar by remember { mutableStateOf<WinPillar?>(null) }
    var editingHabit by remember { mutableStateOf<WinHabit?>(null) }
    var habitTextInput by remember { mutableStateOf("") }

    var noteText by remember(selectedDateIso, entry?.victoryNote) {
        mutableStateOf(entry?.victoryNote.orEmpty())
    }

    val recommendedProgram = remember(vm.workoutLogs.collectAsState().value) {
        vm.recommendedTodayProgram()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TacticalColors.AmoledBlack),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header + Date Switcher
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "4 DAILY WINS",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TacticalColors.TextPrimary
                    )
                    Text(
                        text = "${profile.weightKg}kg • Recomp & Knee-Safe Mode",
                        fontSize = 12.sp,
                        color = TacticalColors.TextSecondary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(TacticalColors.CardDark)
                        .border(1.dp, TacticalColors.BorderHairline, RoundedCornerShape(12.dp))
                ) {
                    IconButton(
                        onClick = { vm.selectDate(selectedDate.minusDays(1).toString()) },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(Icons.Rounded.ChevronLeft, "Previous Day", tint = TacticalColors.TextSecondary)
                    }
                    Text(
                        text = if (isToday) "TODAY" else selectedDate.format(DateTimeFormatter.ofPattern("MMM dd")),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (isToday) TacticalColors.MentalCyan else TacticalColors.TextPrimary,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(
                        onClick = {
                            if (!isToday) vm.selectDate(selectedDate.plusDays(1).toString())
                        },
                        enabled = !isToday,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            Icons.Rounded.ChevronRight,
                            "Next Day",
                            tint = if (isToday) TacticalColors.TextMuted else TacticalColors.TextSecondary
                        )
                    }
                }
            }
        }

        // Hero 4-Wins Quadrant Wheel
        item {
            FourWinsArcadeWheel(
                pillarStatus = dayPillarStatus,
                pillarsWon = dayPillarsWon,
                habitsDone = habits.count { it.isActive && it.id in completedIds },
                habitsTotal = habits.count { it.isActive },
                streakDays = stats.activeDayStreak,
                totalWinsAllTime = stats.totalAllTimeHabitWins,
                rankTitle = stats.rankTitle,
                level = stats.level,
                xpIntoLevel = stats.xpIntoLevel,
                xpPerLevel = stats.xpPerLevel
            )
        }

        // Perfect 4/4 Banner
        item {
            AnimatedVisibility(visible = dayPillarsWon == 4) {
                TacticalCard(
                    borderColor = TacticalColors.ArcadeGold,
                    backgroundColor = TacticalColors.ArcadeGold.copy(alpha = 0.11f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.EmojiEvents,
                            contentDescription = null,
                            tint = TacticalColors.ArcadeGold,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "4/4 DAILY WINS LOCKED IN!",
                                color = TacticalColors.ArcadeGold,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "+50 Bonus XP earned. Perfect-day streak: ${stats.perfectDayStreak}d",
                                color = TacticalColors.TextPrimary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Today's Tailored Workout Quick-Launch
        item {
            TacticalCard(
                borderColor = TacticalColors.PhysicalCrimson.copy(alpha = 0.55f),
                backgroundColor = TacticalColors.CardElevated
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TODAY'S TAILORED SESSION",
                        color = TacticalColors.PhysicalCrimson,
                        style = MaterialTheme.typography.labelMedium
                    )
                    KneeSafetyPill(isSafe = true, label = recommendedProgram.kneeSafeRating.uppercase())
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = recommendedProgram.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TacticalColors.TextPrimary
                )
                Text(
                    text = "${recommendedProgram.durationMinutes} min • ${recommendedProgram.focus}",
                    fontSize = 12.sp,
                    color = TacticalColors.TextSecondary
                )
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = { onStartWorkout(recommendedProgram) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TacticalColors.PhysicalCrimson,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("START GUIDED WORKOUT", fontWeight = FontWeight.Bold)
                }
            }
        }

        // The 4 Pillars Checklist (Physical, Mental, Financial, Spiritual)
        WinPillar.entries.forEach { pillar ->
            item(key = pillar.name) {
                val pillarHabits = habits.filter { it.pillar == pillar && it.isActive }
                val pillarWon = dayPillarStatus[pillar] == true
                val accent = pillarColor(pillar)

                TacticalCard(
                    borderColor = if (pillarWon) accent.copy(alpha = 0.7f) else TacticalColors.BorderHairline
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(accent.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = pillarIcon(pillar),
                                    contentDescription = null,
                                    tint = accent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = pillar.title.uppercase(),
                                        color = accent,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp
                                    )
                                    if (pillarWon) {
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = "WIN ✓",
                                            color = accent,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(accent.copy(alpha = 0.18f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = pillar.description,
                                    fontSize = 11.sp,
                                    color = TacticalColors.TextMuted
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                editingPillar = pillar
                                editingHabit = null
                                habitTextInput = ""
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Add,
                                contentDescription = "Add task",
                                tint = TacticalColors.TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    pillarHabits.forEachIndexed { idx, habit ->
                        val checked = habit.id in completedIds
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (checked) accent.copy(alpha = 0.11f) else TacticalColors.AmoledBlack
                                )
                                .border(
                                    1.dp,
                                    if (checked) accent.copy(alpha = 0.45f) else TacticalColors.BorderHairline,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { vm.toggleHabit(habit.id) }
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(if (checked) accent else Color.Transparent)
                                    .border(1.5.dp, if (checked) accent else TacticalColors.TextMuted, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (checked) {
                                    Icon(
                                        Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = TacticalColors.AmoledBlack,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "${idx + 1}. ${habit.title}",
                                color = if (checked) TacticalColors.TextPrimary else TacticalColors.TextSecondary,
                                fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal,
                                textDecoration = if (checked) TextDecoration.None else TextDecoration.None,
                                modifier = Modifier.weight(1f),
                                fontSize = 14.sp
                            )
                            IconButton(
                                onClick = {
                                    editingPillar = pillar
                                    editingHabit = habit
                                    habitTextInput = habit.title
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.Edit,
                                    contentDescription = "Edit task",
                                    tint = TacticalColors.TextMuted,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Daily Victory / Gratitude Log (Counting Victories)
        item {
            TacticalCard {
                Text(
                    text = "DAILY VICTORY LOG",
                    color = TacticalColors.SpiritualAmber,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = "Name 1 specific win or gratitude line for today.",
                    fontSize = 12.sp,
                    color = TacticalColors.TextSecondary
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    placeholder = { Text("e.g., Completed 45m row + bench session, knee felt great.", fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TacticalColors.SpiritualAmber,
                        unfocusedBorderColor = TacticalColors.BorderHairline,
                        focusedTextColor = TacticalColors.TextPrimary,
                        unfocusedTextColor = TacticalColors.TextPrimary
                    )
                )
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = { vm.saveVictoryNote(noteText) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TacticalColors.SpiritualAmber,
                            contentColor = TacticalColors.AmoledBlack
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("SAVE VICTORY", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(18.dp)) }
    }

    // Add / Edit Habit Dialog
    if (editingPillar != null) {
        val targetPillar = editingPillar!!
        AlertDialog(
            onDismissRequest = { editingPillar = null },
            containerColor = TacticalColors.CardDark,
            title = {
                Text(
                    text = if (editingHabit == null) "Add ${targetPillar.title} Win" else "Edit ${targetPillar.title} Win",
                    color = TacticalColors.TextPrimary
                )
            },
            text = {
                OutlinedTextField(
                    value = habitTextInput,
                    onValueChange = { habitTextInput = it },
                    label = { Text("Habit / Daily Win") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.addOrUpdateHabit(targetPillar, habitTextInput, editingHabit?.id)
                        editingPillar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = pillarColor(targetPillar))
                ) {
                    Text("Save", color = TacticalColors.AmoledBlack, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row {
                    if (editingHabit != null) {
                        TextButton(
                            onClick = {
                                vm.deleteHabit(editingHabit!!.id)
                                editingPillar = null
                            }
                        ) {
                            Icon(Icons.Rounded.DeleteOutline, null, tint = TacticalColors.AlertRed, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Delete", color = TacticalColors.AlertRed)
                        }
                    }
                    TextButton(onClick = { editingPillar = null }) {
                        Text("Cancel", color = TacticalColors.TextSecondary)
                    }
                }
            }
        )
    }
}
