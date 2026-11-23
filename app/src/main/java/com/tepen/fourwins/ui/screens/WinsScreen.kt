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
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.tepen.fourwins.data.PreloadedCatalog
import com.tepen.fourwins.domain.FitnessCalculations
import com.tepen.fourwins.model.WinHabit
import com.tepen.fourwins.model.WinPillar
import com.tepen.fourwins.model.WorkoutProgram
import com.tepen.fourwins.ui.components.HeroWinsGradientCard
import com.tepen.fourwins.ui.components.KneeSafetyPill
import com.tepen.fourwins.ui.components.QuickActionGlassTile
import com.tepen.fourwins.ui.components.TacticalCard
import com.tepen.fourwins.ui.components.TemplateGreetingHeader
import com.tepen.fourwins.ui.components.pillarColor
import com.tepen.fourwins.ui.components.pillarIcon
import com.tepen.fourwins.ui.theme.TacticalColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WinsScreen(
    vm: MainViewModel,
    onStartWorkout: (WorkoutProgram) -> Unit,
    onOpenDarebee: () -> Unit = {},
    onOpenProfile: () -> Unit = {}
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
    val quickAbsProgram = remember { PreloadedCatalog.expressWorkouts.first() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Template Greeting Header ("Hello, Athlete | Streak | Avatar")
        item {
            TemplateGreetingHeader(
                userName = profile.name,
                subtitle = "${profile.weightKg} kg • Knee-Safe Recomp",
                streakDays = stats.activeDayStreak,
                onAvatarClick = onOpenProfile
            )
        }

        // Signature Luminous Cyan Hero Card
        item {
            HeroWinsGradientCard(
                pillarsWon = dayPillarsWon,
                habitsDone = habits.count { it.isActive && it.id in completedIds },
                habitsTotal = habits.count { it.isActive },
                totalWinsAllTime = stats.totalAllTimeHabitWins,
                level = stats.level,
                rankTitle = stats.rankTitle,
                xpIntoLevel = stats.xpIntoLevel,
                xpPerLevel = stats.xpPerLevel,
                onActionClick = { onStartWorkout(recommendedProgram) },
                actionLabel = "Start Gym"
            )
        }

        // 3-Tile Frosted Quick Actions (matching Pay | Cards | Bills row in template)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionGlassTile(
                    icon = Icons.Rounded.FitnessCenter,
                    label = "Today's Split",
                    accent = TacticalColors.PhysicalCrimson,
                    onClick = { onStartWorkout(recommendedProgram) },
                    modifier = Modifier.weight(1f)
                )
                QuickActionGlassTile(
                    icon = Icons.Rounded.Bolt,
                    label = "2m Core",
                    accent = TacticalColors.MentalCyan,
                    onClick = { onStartWorkout(quickAbsProgram) },
                    modifier = Modifier.weight(1f)
                )
                QuickActionGlassTile(
                    icon = Icons.AutoMirrored.Rounded.MenuBook,
                    label = "Darebee PDFs",
                    accent = TacticalColors.ArcadeGold,
                    onClick = onOpenDarebee,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Date Switcher + Recommended Session Summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "4 Daily Pillars",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TacticalColors.TextPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(TacticalColors.GlassFill)
                        .border(1.dp, TacticalColors.GlassBorderGradient, RoundedCornerShape(50))
                ) {
                    IconButton(
                        onClick = { vm.selectDate(selectedDate.minusDays(1).toString()) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Rounded.ChevronLeft, "Previous Day", tint = TacticalColors.TextSecondary, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = if (isToday) "Today" else selectedDate.format(DateTimeFormatter.ofPattern("MMM d")),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isToday) TacticalColors.MentalCyan else TacticalColors.TextPrimary,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(
                        onClick = { if (!isToday) vm.selectDate(selectedDate.plusDays(1).toString()) },
                        enabled = !isToday,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Rounded.ChevronRight,
                            "Next Day",
                            tint = if (isToday) TacticalColors.TextMuted else TacticalColors.TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // 2x2 Whiteboard Pillar Filter Tiles
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
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (won) c.copy(alpha = 0.16f) else TacticalColors.GlassFill)
                                    .border(
                                        width = 1.dp,
                                        brush = if (focused) androidx.compose.ui.graphics.SolidColor(Color.White)
                                        else if (won) androidx.compose.ui.graphics.SolidColor(c.copy(alpha = 0.75f))
                                        else TacticalColors.GlassBorderGradient,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { activePillarFilter = if (focused) null else pillar }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(pillarIcon(pillar), null, tint = c, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(pillar.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TacticalColors.TextPrimary)
                                        Text("$doneCount/${pHabits.size} done", fontSize = 11.sp, color = if (won) c else TacticalColors.TextSecondary)
                                    }
                                }
                                if (won) {
                                    Box(
                                        modifier = Modifier.size(20.dp).clip(CircleShape).background(c),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Rounded.Check, null, tint = TacticalColors.HeroOnDark, modifier = Modifier.size(13.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Perfect 4/4 Celebration Banner
        item {
            AnimatedVisibility(visible = dayPillarsWon == 4) {
                TacticalCard(
                    borderColor = TacticalColors.ArcadeGold,
                    backgroundColor = TacticalColors.ArcadeGold.copy(alpha = 0.15f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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

        // Today's Tailored Workout Preview Strip
        item {
            TacticalCard(onClick = { onStartWorkout(recommendedProgram) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recommended Next", color = TacticalColors.MentalCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    KneeSafetyPill(isSafe = true, label = recommendedProgram.kneeSafeRating)
                }
                Spacer(Modifier.height(6.dp))
                Text(recommendedProgram.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TacticalColors.TextPrimary)
                Text("${recommendedProgram.durationMinutes} min • ${recommendedProgram.focus}", fontSize = 13.sp, color = TacticalColors.TextSecondary)
            }
        }

        // Grouped Glass Checklist Cards (matching History Transaction list style)
        val visiblePillars = if (activePillarFilter != null) listOf(activePillarFilter!!) else WinPillar.entries
        visiblePillars.forEach { pillar ->
            item(key = pillar.name) {
                val pillarHabits = habits.filter { it.pillar == pillar && it.isActive }
                val pillarWon = dayPillarStatus[pillar] == true
                val accent = pillarColor(pillar)

                TacticalCard(borderColor = if (pillarWon) accent.copy(alpha = 0.7f) else null) {
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
                                    .background(accent.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(pillarIcon(pillar), null, tint = accent, modifier = Modifier.size(18.dp))
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(pillar.title, color = TacticalColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(
                                    text = if (pillarWon) "Win earned today" else pillar.description,
                                    fontSize = 12.sp,
                                    color = if (pillarWon) accent else TacticalColors.TextSecondary
                                )
                            }
                        }
                        IconButton(onClick = {
                            editingPillar = pillar
                            editingHabit = null
                            habitTextInput = ""
                        }) {
                            Icon(Icons.Rounded.Add, "Add task", tint = TacticalColors.TextSecondary)
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    pillarHabits.forEachIndexed { idx, habit ->
                        val checked = habit.id in completedIds
                        if (idx > 0) {
                            HorizontalDivider(color = TacticalColors.GlassBorderBottom.copy(alpha = 0.35f))
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { vm.toggleHabit(habit.id) }
                                .padding(vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(if (checked) accent else Color.Transparent)
                                    .border(1.8.dp, if (checked) accent else TacticalColors.TextMuted, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (checked) {
                                    Icon(Icons.Rounded.Check, null, tint = TacticalColors.HeroOnDark, modifier = Modifier.size(15.dp))
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = habit.title,
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
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Rounded.Edit, "Edit", tint = TacticalColors.TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // Daily Victory Note
        item {
            TacticalCard {
                Text("Daily Victory Note", color = TacticalColors.SpiritualAmber, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text("Write 1 specific win or gratitude line for today.", fontSize = 12.sp, color = TacticalColors.TextSecondary)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    placeholder = { Text("e.g., Finished Day 1 bench & incline walk, knee felt great.", fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TacticalColors.MentalCyan,
                        unfocusedBorderColor = TacticalColors.BorderHairline,
                        focusedTextColor = TacticalColors.TextPrimary,
                        unfocusedTextColor = TacticalColors.TextPrimary
                    )
                )
                Spacer(Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = { vm.saveVictoryNote(noteText) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TacticalColors.MentalCyan,
                            contentColor = TacticalColors.HeroOnDark
                        ),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Save Note", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(18.dp)) }
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
                    Text("Save", color = TacticalColors.HeroOnDark, fontWeight = FontWeight.Bold)
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
