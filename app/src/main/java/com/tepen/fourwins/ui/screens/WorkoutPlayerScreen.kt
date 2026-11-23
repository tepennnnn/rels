package com.tepen.fourwins.ui.screens

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tepen.fourwins.MainViewModel
import com.tepen.fourwins.model.KneeComfort
import com.tepen.fourwins.model.WorkoutProgram
import com.tepen.fourwins.ui.components.KneeSafetyPill
import com.tepen.fourwins.ui.components.TacticalCard
import com.tepen.fourwins.ui.theme.TacticalColors
import kotlinx.coroutines.delay

private enum class TimerPhase {
    WORK, REST, DONE
}

@Composable
fun WorkoutPlayerScreen(
    vm: MainViewModel,
    program: WorkoutProgram,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val profile by vm.profile.collectAsState()

    var voiceOn by remember { mutableStateOf(profile.voiceCoachEnabled) }
    var beepOn by remember { mutableStateOf(profile.beepEnabled) }
    var hapticsOn by remember { mutableStateOf(profile.hapticsEnabled) }

    var exerciseIdx by remember { mutableIntStateOf(0) }
    var currentSet by remember { mutableIntStateOf(1) }
    var useKneeSwap by remember { mutableStateOf(false) }

    val exercise = program.exercises[exerciseIdx]
    var phase by remember { mutableStateOf(TimerPhase.WORK) }
    var totalSeconds by remember(exerciseIdx, currentSet, phase) {
        mutableIntStateOf(
            when (phase) {
                TimerPhase.WORK -> if (exercise.isTimed) exercise.durationSeconds else 0
                TimerPhase.REST -> exercise.restSeconds.coerceAtLeast(15)
                TimerPhase.DONE -> 0
            }
        )
    }
    var remainingSeconds by remember(exerciseIdx, currentSet, phase) {
        mutableIntStateOf(totalSeconds)
    }
    var timerRunning by remember(exerciseIdx, currentSet, phase) {
        mutableStateOf(phase == TimerPhase.REST) // auto-start rest timer; user starts timed work or taps Complete Set
    }

    var totalSetsLogged by remember { mutableIntStateOf(0) }
    val sessionStartMs = remember { System.currentTimeMillis() }
    var showFinishDialog by remember { mutableStateOf(false) }
    var selectedKneeComfort by remember { mutableStateOf(KneeComfort.GREAT) }

    // Keep screen awake during active training
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        vm.coach.speak("Starting ${program.title}. First up: ${exercise.name}.", voiceOn)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    fun advanceAfterRest() {
        if (currentSet < exercise.sets) {
            currentSet++
            phase = TimerPhase.WORK
            vm.coach.beepStartOrFinish(beepOn)
            vm.coach.vibratePulse(hapticsOn)
            vm.coach.speak("Set $currentSet of ${exercise.sets}. Go!", voiceOn)
        } else if (exerciseIdx < program.exercises.lastIndex) {
            exerciseIdx++
            currentSet = 1
            useKneeSwap = false
            phase = TimerPhase.WORK
            val nextEx = program.exercises[exerciseIdx]
            vm.coach.beepStartOrFinish(beepOn)
            vm.coach.vibratePulse(hapticsOn)
            vm.coach.speak("Next exercise: ${nextEx.name}.", voiceOn)
        } else {
            phase = TimerPhase.DONE
            showFinishDialog = true
        }
    }

    fun finishSet() {
        totalSetsLogged++
        val isFinalSetOfAll = exerciseIdx == program.exercises.lastIndex && currentSet >= exercise.sets
        if (isFinalSetOfAll) {
            phase = TimerPhase.DONE
            showFinishDialog = true
            return
        }
        val restSec = exercise.restSeconds
        if (restSec <= 0) {
            advanceAfterRest()
        } else {
            phase = TimerPhase.REST
            vm.coach.beepStartOrFinish(beepOn)
            vm.coach.vibratePulse(hapticsOn)
            vm.coach.speak("Rest $restSec seconds.", voiceOn)
        }
    }

    // Tick loop
    LaunchedEffect(timerRunning, phase, exerciseIdx, currentSet) {
        while (timerRunning && remainingSeconds > 0 && phase != TimerPhase.DONE) {
            delay(1000L)
            remainingSeconds--
            if (remainingSeconds in 1..3) {
                vm.coach.beepCountdown(beepOn)
                vm.coach.vibrateTick(hapticsOn)
                vm.coach.speak("$remainingSeconds", voiceOn)
            } else if (phase == TimerPhase.WORK && totalSeconds >= 30 && remainingSeconds == totalSeconds / 2) {
                vm.coach.speak("Halfway there.", voiceOn)
            }
            if (remainingSeconds == 0) {
                timerRunning = false
                if (phase == TimerPhase.WORK) {
                    finishSet()
                } else if (phase == TimerPhase.REST) {
                    advanceAfterRest()
                }
            }
        }
    }

    val displayName = if (useKneeSwap && exercise.kneeSwapName != null) exercise.kneeSwapName else exercise.name
    val displayCue = if (useKneeSwap && exercise.kneeSwapCue != null) exercise.kneeSwapCue else exercise.formCue

    val overallProgress = ((exerciseIdx.toFloat() + (currentSet - 1).toFloat() / exercise.sets.coerceAtLeast(1)) /
        program.exercises.size.coerceAtLeast(1)).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TacticalColors.AmoledBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top bar + audio/haptic toggles
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Exit", tint = TacticalColors.TextPrimary)
                }
                Text(
                    text = "EX ${exerciseIdx + 1}/${program.exercises.size} • SET $currentSet/${exercise.sets}",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TacticalColors.MentalCyan
                )
                Row {
                    IconButton(onClick = { voiceOn = !voiceOn }) {
                        Icon(
                            if (voiceOn) Icons.AutoMirrored.Rounded.VolumeUp else Icons.AutoMirrored.Rounded.VolumeOff,
                            "Voice Coach",
                            tint = if (voiceOn) TacticalColors.MentalCyan else TacticalColors.TextMuted
                        )
                    }
                    IconButton(onClick = { beepOn = !beepOn }) {
                        Icon(
                            Icons.Rounded.NotificationsActive,
                            "Beep",
                            tint = if (beepOn) TacticalColors.SpiritualAmber else TacticalColors.TextMuted
                        )
                    }
                    IconButton(onClick = { hapticsOn = !hapticsOn }) {
                        Icon(
                            Icons.Rounded.Vibration,
                            "Haptics",
                            tint = if (hapticsOn) TacticalColors.FinancialEmerald else TacticalColors.TextMuted
                        )
                    }
                }
            }

            LinearProgressIndicator(
                progress = { overallProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
                color = TacticalColors.PhysicalCrimson,
                trackColor = TacticalColors.CardDark
            )
        }

        // Center Stage: Phase Ring + Exercise Info
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (phase == TimerPhase.REST) "RECOVERY REST" else exercise.equipment.label.uppercase(),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (phase == TimerPhase.REST) TacticalColors.MentalCyan else TacticalColors.PhysicalCrimson
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = displayName,
                style = MaterialTheme.typography.headlineMedium,
                color = TacticalColors.TextPrimary
            )
            Spacer(Modifier.height(6.dp))
            KneeSafetyPill(isSafe = true, label = "KNEE-SAFE PROTOCOL")

            Spacer(Modifier.height(20.dp))

            // Countdown Circle or Rep Target Display
            Box(
                modifier = Modifier.size(196.dp),
                contentAlignment = Alignment.Center
            ) {
                val ringColor = if (phase == TimerPhase.REST) TacticalColors.MentalCyan else TacticalColors.PhysicalCrimson
                val frac = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds else 1f
                Canvas(modifier = Modifier.size(188.dp)) {
                    val stroke = 12.dp.toPx()
                    drawArc(
                        color = ringColor.copy(alpha = 0.16f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = ringColor,
                        startAngle = -90f,
                        sweepAngle = 360f * frac,
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (phase == TimerPhase.REST || exercise.isTimed) {
                        val mins = remainingSeconds / 60
                        val secs = remainingSeconds % 60
                        Text(
                            text = String.format("%02d:%02d", mins, secs),
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = TacticalColors.TextPrimary
                        )
                        Text(
                            text = if (phase == TimerPhase.REST) "NEXT: SET ${if (currentSet < exercise.sets) currentSet + 1 else 1}" else exercise.repsOrSeconds,
                            fontSize = 12.sp,
                            color = TacticalColors.TextSecondary
                        )
                    } else {
                        Text(
                            text = exercise.repsOrSeconds,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = TacticalColors.TextPrimary
                        )
                        Text(
                            text = "SET $currentSet OF ${exercise.sets}",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = TacticalColors.TextSecondary
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Form + Knee Safety Card
            TacticalCard {
                Text(
                    text = "FORM CUE",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = TacticalColors.MentalCyan
                )
                Text(text = displayCue, fontSize = 13.sp, color = TacticalColors.TextPrimary)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "KNEE NOTE: ${exercise.kneeSafetyNote}",
                    fontSize = 12.sp,
                    color = TacticalColors.KneeSafeGreen
                )

                if (exercise.kneeSwapName != null) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { useKneeSwap = !useKneeSwap },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, TacticalColors.CautionAmber.copy(alpha = 0.6f))
                    ) {
                        Icon(Icons.Rounded.SwapHoriz, null, tint = TacticalColors.CautionAmber, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (useKneeSwap) "Switch back to ${exercise.name}" else "Swap to ${exercise.kneeSwapName}",
                            color = TacticalColors.CautionAmber,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Bottom Controls
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (phase == TimerPhase.REST) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { remainingSeconds += 15; totalSeconds = maxOf(totalSeconds, remainingSeconds) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+15s", color = TacticalColors.TextPrimary, fontFamily = FontFamily.Monospace)
                    }
                    OutlinedButton(
                        onClick = { timerRunning = !timerRunning },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            if (timerRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            null,
                            tint = TacticalColors.MentalCyan
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(if (timerRunning) "PAUSE" else "RESUME", color = TacticalColors.MentalCyan)
                    }
                    Button(
                        onClick = { timerRunning = false; advanceAfterRest() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TacticalColors.MentalCyan)
                    ) {
                        Icon(Icons.Rounded.SkipNext, null, tint = TacticalColors.AmoledBlack)
                        Text("SKIP", color = TacticalColors.AmoledBlack, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (exercise.isTimed) {
                        Button(
                            onClick = {
                                if (!timerRunning) {
                                    vm.coach.beepStartOrFinish(beepOn)
                                    vm.coach.vibratePulse(hapticsOn)
                                    vm.coach.speak("Timer started.", voiceOn)
                                }
                                timerRunning = !timerRunning
                            },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (timerRunning) TacticalColors.CardElevated else TacticalColors.MentalCyan
                            )
                        ) {
                            Icon(
                                if (timerRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                null,
                                tint = if (timerRunning) TacticalColors.TextPrimary else TacticalColors.AmoledBlack
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (timerRunning) "PAUSE" else "START TIMER",
                                color = if (timerRunning) TacticalColors.TextPrimary else TacticalColors.AmoledBlack,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = {
                            timerRunning = false
                            finishSet()
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TacticalColors.PhysicalCrimson)
                    ) {
                        Icon(Icons.Rounded.CheckCircle, null)
                        Spacer(Modifier.width(6.dp))
                        Text("COMPLETE SET", fontWeight = FontWeight.Bold)
                    }
                }
            }

            TextButton(
                onClick = { showFinishDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Finish & Log Session Early", color = TacticalColors.TextMuted, fontSize = 12.sp)
            }
        }
    }

    // Completion + Knee Comfort Check-in Dialog
    if (showFinishDialog) {
        val elapsedMin = ((System.currentTimeMillis() - sessionStartMs) / 60000L).toInt().coerceAtLeast(1)
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            containerColor = TacticalColors.CardDark,
            title = {
                Text("Session Complete — Knee Check", color = TacticalColors.TextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "How did your knee feel during ${program.title}?",
                        fontSize = 13.sp,
                        color = TacticalColors.TextSecondary
                    )
                    KneeComfort.entries.forEach { kc ->
                        val active = selectedKneeComfort == kc
                        val c = Color(kc.colorHex)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedKneeComfort = kc },
                            shape = RoundedCornerShape(10.dp),
                            color = if (active) c.copy(alpha = 0.18f) else TacticalColors.AmoledBlack,
                            border = BorderStroke(1.dp, if (active) c else TacticalColors.BorderHairline)
                        ) {
                            Text(
                                text = kc.label,
                                color = if (active) c else TacticalColors.TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                    Text(
                        text = "Logging this automatically credits today's Physical Win!",
                        fontSize = 11.sp,
                        color = TacticalColors.MentalCyan
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.completeWorkout(
                            program = program,
                            durationMinutes = elapsedMin,
                            setsCompleted = totalSetsLogged.coerceAtLeast(1),
                            kneeComfort = selectedKneeComfort,
                            notes = ""
                        )
                        showFinishDialog = false
                        onClose()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TacticalColors.FinancialEmerald)
                ) {
                    Text("SAVE & WIN", color = TacticalColors.AmoledBlack, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Resume", color = TacticalColors.TextSecondary)
                }
            }
        )
    }
}
