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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tepen.fourwins.MainViewModel
import com.tepen.fourwins.domain.FitnessCalculations
import com.tepen.fourwins.model.WinHabit
import com.tepen.fourwins.model.WinPillar
import com.tepen.fourwins.model.WorkoutProgram
import com.tepen.fourwins.ui.components.FourWinsArcadeWheel
import com.tepen.fourwins.ui.components.KneeSafetyPill
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
    var activePillarFilter by remember { mutableStateOf<WinPillar?>(null) }

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
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        text = "4 Daily Wins",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TacticalColors.TextPrimary
                    )
                    Text(
                        text = "${profile.weightKg} kg • Recomp & Knee-Safe",
                        fontSize = 13.sp,
                        color = TacticalColors.TextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TacticalColors.CardDark
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { vm.selectDate(selectedDate.minusDays(1).toString()) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Rounded.ChevronLeft, "Previous Day", tint = TacticalColors.TextSecondary)
                        }
                        Text(
                            text = if (isToday) "Today" else selectedDate.format(DateTimeFormatter.ofPattern("MMM d")),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isToday) TacticalColors.MentalCyan else TacticalColors.TextPrimary,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        IconButton(
                            onClick = { if (!isToday) vm.selectDate(selectedDate.plusDays(1).toString()) },
                            enabled = !isToday,
                            modifier = Modifier.size(36.dp)
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
        }

        // Hero 4-Quadrant Wheel
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

        // 2x2 Quick Whiteboard Pillar Grid (mirrors the 4 Daily Wins board)
        item {
            val rows = listOf(
                WinPillar.PHYSICAL to WinPillar.FINANCIAL,
                WinPillar.MENTAL to WinPillar.SPIRITUAL
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                rows.forEach { (left, right) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(left, right).forEach { pillar ->
                            val c = pillarColor(pillar)
                            val won = dayPillarStatus[pillar] == true
                            val pHabits = habits.filter { it.pillar == pillar && it.isActive }
                            val doneCount = pHabits.count { it.id in completedIds }
                            val focused = activePillarFilter == pillar
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        activePillarFilter = if (focused) null else pillar
                                    },
                                shape = RoundedCornerShape(16.dp),
                                color = if (won) c.copy(alpha = 0.14f) else TacticalColors.CardDark,
                                border = BorderStroke(
                                    width = if (focused || won) 1.5.dp else 0.dp,
                                    color = if (focused) Color.White.copy(alpha = 0.7f) else if (won) c.copy(alpha = 0.6f) else Color.Transparent
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(pillarIcon(pillar), null, tint = c, modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = pillar.title,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TacticalColors.TextPrimary
                                            )
                                            Text(
                                                text = "$doneCount/${pHabits.size} done",
                                                fontSize = 11.sp,
                                                color = if (won) c else TacticalColors.TextSecondary
                                            )
                                        }
                                    }
                                    if (won) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(c),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Rounded.Check, null, tint = TacticalColors.AmoledBlack, modifier = Modifier.size(13.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Perfect 4/4 Banner
        item {
            AnimatedVisibility(visible = dayPillarsWon == 4) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = TacticalColors.ArcadeGold.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, TacticalColors.ArcadeGold)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.EmojiEvents, null, tint = TacticalColors.ArcadeGold, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("4/4 Daily Wins Complete!", color = TacticalColors.ArcadeGold, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Perfect-day streak: ${stats.perfectDayStreak} days • +50 Bonus XP", color = TacticalColors.TextPrimary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Today's Tailored Workout Quick-Launch
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = TacticalColors.CardElevated
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today's Workout",
                            color = TacticalColors.PhysicalCrimson,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        KneeSafetyPill(isSafe = true, label = recommendedProgram.kneeSafeRating)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = recommendedProgram.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = TacticalColors.TextPrimary
                    )
                    Text(
                        text = "${recommendedProgram.durationMinutes} min • ${recommendedProgram.focus}",
                        fontSize = 13.sp,
                        color = TacticalColors.TextSecondary
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { onStartWorkout(recommendedProgram) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TacticalColors.PhysicalCrimson,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Rounded.PlayArrow, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Start Guided Session", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        // Checklist Cards (Filtered by activePillarFilter if user tapped a quadrant)
        val visiblePillars = if (activePillarFilter != null) listOf(activePillarFilter!!) else WinPillar.entries
        visiblePillars.forEach { pillar ->
            item(key = pillar.name) {
                val pillarHabits = habits.filter { it.pillar == pillar && it.isActive }
                val pillarWon = dayPillarStatus[pillar] == true
                val accent = pillarColor(pillar)

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = TacticalColors.CardDark
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(accent.copy(alpha = 0.16f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(pillarIcon(pillar), null, tint = accent, modifier = Modifier.size(18.dp))
                                }
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = pillar.title,
                                        color = TacticalColors.TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = if (pillarWon) "Win earned today" else pillar.description,
                                        fontSize = 12.sp,
                                        color = if (pillarWon) accent else TacticalColors.TextSecondary
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    editingPillar = pillar
                                    editingHabit = null
                                    habitTextInput = ""
                                }
                            ) {
                                Icon(Icons.Rounded.Add, "Add task", tint = TacticalColors.TextSecondary)
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        pillarHabits.forEachIndexed { idx, habit ->
                            val checked = habit.id in completedIds
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (checked) accent.copy(alpha = 0.13f) else TacticalColors.SurfaceDark)
                                    .clickable { vm.toggleHabit(habit.id) }
                                    .padding(horizontal = 12.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (checked) accent else Color.Transparent)
                                        .border(1.8.dp, if (checked) accent else TacticalColors.TextMuted, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (checked) {
                                        Icon(Icons.Rounded.Check, null, tint = TacticalColors.AmoledBlack, modifier = Modifier.size(15.dp))
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = "${idx + 1}. ${habit.title}",
                                    color = if (checked) TacticalColors.TextPrimary else TacticalColors.TextSecondary,
                                    fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 15.sp
                                )
                                IconButton(
                                    onClick = {
                                        editingPillar = pillar
                                        editingHabit = habit
                                        habitTextInput = habit.title
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Rounded.Edit, "Edit", tint = TacticalColors.TextMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Daily Victory / Gratitude Log
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = TacticalColors.CardDark
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Daily Victory Note", color = TacticalColors.SpiritualAmber, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Write 1 specific win or gratitude line for today.", fontSize = 12.sp, color = TacticalColors.TextSecondary)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        placeholder = { Text("e.g., Finished Day 1 bench & incline walk, knee felt great.", fontSize = 13.sp) },
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
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Save Note", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }

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
                        TextButton(onClick = {
                            vm.deleteHabit(editingHabit!!.id)
                            editingPillar = null
                        }) {
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
