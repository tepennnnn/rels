package com.tepen.fourwins.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tepen.fourwins.MainViewModel
import com.tepen.fourwins.data.PreloadedCatalog
import com.tepen.fourwins.model.Challenge30Day
import com.tepen.fourwins.model.WorkoutExercise
import com.tepen.fourwins.model.WorkoutProgram
import com.tepen.fourwins.ui.components.ExerciseHowToDialog
import com.tepen.fourwins.ui.components.KneeSafetyPill
import com.tepen.fourwins.ui.components.TacticalCard
import com.tepen.fourwins.ui.components.TemplateUnderlineTabs
import com.tepen.fourwins.ui.components.pillarColor
import com.tepen.fourwins.ui.theme.TacticalColors

@Composable
fun TrainScreen(
    vm: MainViewModel,
    onStartWorkout: (WorkoutProgram) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val challenges by vm.challenges.collectAsState()
    val workoutLogs by vm.workoutLogs.collectAsState()
    var expandedChallengeId by remember { mutableStateOf<String?>(challenges.firstOrNull()?.id) }
    var expandedProgramId by remember { mutableStateOf<String?>(null) }
    var selectedHowToExercise by remember { mutableStateOf<WorkoutExercise?>(null) }

    val tabs = listOf("5-Day Split", "Express", "30-Day Challenges")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Training Protocol",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TacticalColors.TextPrimary
                )
                Text(
                    text = "Knee-protected minimal gym + Darebee routines (45–60 min)",
                    fontSize = 13.sp,
                    color = TacticalColors.TextSecondary
                )
            }
        }

        // Clean Underline Tab Switcher (matching template's Credit Card | Debit Card)
        item {
            TemplateUnderlineTabs(
                tabs = tabs,
                selectedIndex = selectedTab,
                onSelect = { selectedTab = it }
            )
        }

        when (selectedTab) {
            0 -> {
                items(PreloadedCatalog.tailoredPrograms, key = { it.id }) { program ->
                    val doneCount = workoutLogs.count { it.programId == program.id }
                    ProgramCard(
                        program = program,
                        completedCount = doneCount,
                        expanded = expandedProgramId == program.id,
                        onToggleExpand = {
                            expandedProgramId = if (expandedProgramId == program.id) null else program.id
                        },
                        onInspectExercise = { selectedHowToExercise = it },
                        onStart = { onStartWorkout(program) }
                    )
                }
            }
            1 -> {
                items(PreloadedCatalog.expressWorkouts, key = { it.id }) { program ->
                    val doneCount = workoutLogs.count { it.programId == program.id }
                    ProgramCard(
                        program = program,
                        completedCount = doneCount,
                        expanded = expandedProgramId == program.id,
                        onToggleExpand = {
                            expandedProgramId = if (expandedProgramId == program.id) null else program.id
                        },
                        onInspectExercise = { selectedHowToExercise = it },
                        onStart = { onStartWorkout(program) }
                    )
                }
            }
            2 -> {
                items(challenges, key = { it.id }) { challenge ->
                    ChallengePosterCard(
                        challenge = challenge,
                        expanded = expandedChallengeId == challenge.id,
                        onToggleExpand = {
                            expandedChallengeId = if (expandedChallengeId == challenge.id) null else challenge.id
                        },
                        onToggleDay = { day -> vm.toggleChallengeDay(challenge.id, day) }
                    )
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }

    if (selectedHowToExercise != null) {
        ExerciseHowToDialog(
            exercise = selectedHowToExercise!!,
            onSpeakSteps = { txt -> vm.coach.speak(txt, true) },
            onDismiss = { selectedHowToExercise = null }
        )
    }
}

@Composable
private fun ProgramCard(
    program: WorkoutProgram,
    completedCount: Int,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onInspectExercise: (WorkoutExercise) -> Unit,
    onStart: () -> Unit
) {
    TacticalCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            KneeSafetyPill(isSafe = true, label = program.kneeSafeRating.uppercase())
            if (completedCount > 0) {
                Text(
                    text = "DONE ${completedCount}x",
                    color = TacticalColors.FinancialEmerald,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = program.title,
            style = MaterialTheme.typography.titleLarge,
            color = TacticalColors.TextPrimary
        )
        Text(
            text = program.subtitle,
            fontSize = 12.sp,
            color = TacticalColors.TextSecondary
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Schedule, null, tint = TacticalColors.MentalCyan, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("${program.durationMinutes} min", fontSize = 12.sp, color = TacticalColors.TextPrimary)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.FitnessCenter, null, tint = TacticalColors.PhysicalCrimson, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("${program.exercises.size} exercises", fontSize = 12.sp, color = TacticalColors.TextPrimary)
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(TacticalColors.AmoledBlack)
                .border(1.dp, TacticalColors.BorderHairline, RoundedCornerShape(8.dp))
                .clickable(onClick = onToggleExpand)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (expanded) "HIDE EXERCISES & HOW-TO GUIDES" else "VIEW EXERCISES & FORM HOW-TO (${program.exercises.size})",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = TacticalColors.MentalCyan
            )
            Icon(
                if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                null,
                tint = TacticalColors.MentalCyan,
                modifier = Modifier.size(18.dp)
            )
        }

        if (expanded) {
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                program.exercises.forEachIndexed { idx, ex ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(TacticalColors.AmoledBlack)
                            .border(1.dp, TacticalColors.BorderHairline, RoundedCornerShape(8.dp))
                            .clickable { onInspectExercise(ex) }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${idx + 1}. ${ex.name}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TacticalColors.TextPrimary
                            )
                            Text(
                                text = "${ex.sets} × ${ex.repsOrSeconds}",
                                fontSize = 11.sp,
                                color = TacticalColors.TextSecondary
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(TacticalColors.MentalCyan.copy(alpha = 0.14f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Rounded.PlayCircle, null, tint = TacticalColors.MentalCyan, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "HOW-TO",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = TacticalColors.MentalCyan
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TacticalColors.CardElevated,
                contentColor = TacticalColors.TextPrimary
            )
        ) {
            Icon(Icons.Rounded.PlayArrow, null, tint = TacticalColors.PhysicalCrimson)
            Spacer(Modifier.width(6.dp))
            Text("LAUNCH GUIDED SESSION", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ChallengePosterCard(
    challenge: Challenge30Day,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onToggleDay: (Int) -> Unit
) {
    val accent = pillarColor(challenge.linkedPillar)
    val nextDay = (1..30).firstOrNull { it !in challenge.completedDays } ?: 30

    TacticalCard(
        borderColor = if (challenge.completedDays.size == 30) TacticalColors.ArcadeGold else TacticalColors.BorderHairline,
        onClick = onToggleExpand
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Rounded.Bolt, null, tint = accent, modifier = Modifier.size(16.dp))
                Text(
                    text = challenge.darebeeRef.uppercase(),
                    color = accent,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "${challenge.completedDays.size}/30 DAYS",
                color = TacticalColors.TextPrimary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = challenge.title,
            style = MaterialTheme.typography.titleLarge,
            color = TacticalColors.TextPrimary
        )
        Text(
            text = challenge.subtitle,
            fontSize = 12.sp,
            color = TacticalColors.TextSecondary
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Next Target → ${challenge.dailyTargets[nextDay - 1]}",
            fontSize = 12.sp,
            color = accent,
            fontWeight = FontWeight.SemiBold
        )

        if (expanded) {
            Spacer(Modifier.height(12.dp))
            // 6x5 Darebee-style 30-day poster grid
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (row in 0 until 5) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (col in 0 until 6) {
                            val day = row * 6 + col + 1
                            val done = day in challenge.completedDays
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1.1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (done) accent else TacticalColors.AmoledBlack)
                                    .border(
                                        1.dp,
                                        if (done) accent else TacticalColors.BorderHairline,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { onToggleDay(day) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (done) {
                                    Icon(
                                        Icons.Rounded.Check,
                                        contentDescription = "Day $day complete",
                                        tint = TacticalColors.AmoledBlack,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else {
                                    Text(
                                        text = "$day",
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = TacticalColors.TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
