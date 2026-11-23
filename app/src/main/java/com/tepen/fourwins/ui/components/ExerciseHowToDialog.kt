package com.tepen.fourwins.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.BookmarkAdded
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.SlowMotionVideo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tepen.fourwins.model.DarebeeExerciseDetail
import com.tepen.fourwins.model.ExerciseMotionType
import com.tepen.fourwins.model.WorkoutExercise
import com.tepen.fourwins.ui.theme.TacticalColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ExerciseHowToDialog(
    exercise: WorkoutExercise,
    onSpeakSteps: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var modeTab by remember { mutableIntStateOf(0) } // 0 = Offline Animated Demo, 1 = Online Video

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            shape = RoundedCornerShape(18.dp),
            color = TacticalColors.SurfaceDark,
            border = BorderStroke(1.dp, TacticalColors.BorderHairline)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TacticalColors.CardDark)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "HOW-TO & PROPER EXECUTION",
                            color = TacticalColors.MentalCyan,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = exercise.name,
                            color = TacticalColors.TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = {
                        val spoken = buildString {
                            append(exercise.name)
                            append(". ")
                            exercise.steps.forEachIndexed { i, s ->
                                append("Step ${i + 1}: $s ")
                            }
                        }
                        onSpeakSteps(spoken)
                    }) {
                        Icon(Icons.AutoMirrored.Rounded.VolumeUp, "Read Steps Aloud", tint = TacticalColors.MentalCyan)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, "Close", tint = TacticalColors.TextPrimary)
                    }
                }

                // Offline Demo vs Online Video Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(TacticalColors.AmoledBlack)
                        .border(1.dp, TacticalColors.BorderHairline, RoundedCornerShape(10.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("ANIMATED DEMO (OFFLINE)", "ONLINE VIDEO HOW-TO").forEachIndexed { idx, label ->
                        val active = modeTab == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(7.dp))
                                .background(if (active) TacticalColors.MentalCyan else Color.Transparent)
                                .clickable { modeTab = idx }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = if (active) TacticalColors.AmoledBlack else TacticalColors.TextSecondary
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (modeTab == 0) {
                        ExerciseMotionDemoPlayer(
                            motionType = exercise.motionType,
                            kneeNote = exercise.kneeSafetyNote
                        )
                    } else {
                        val ytSearchUrl = "https://m.youtube.com/results?search_query=${Uri.encode(exercise.videoQuery)}"
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(230.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black)
                                    .border(1.dp, TacticalColors.BorderHairline, RoundedCornerShape(12.dp))
                            ) {
                                EmbeddedVideoWebView(url = ytSearchUrl)
                            }
                            OutlinedButton(
                                onClick = {
                                    runCatching {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(ytSearchUrl)))
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Rounded.OpenInNew, null, tint = TacticalColors.PhysicalCrimson, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("OPEN VIDEO IN YOUTUBE APP", color = TacticalColors.PhysicalCrimson, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Step-by-Step Guide
                    StepByStepExecutionCard(
                        steps = exercise.steps.ifEmpty { listOf(exercise.formCue) },
                        breathing = exercise.breathingCue,
                        commonMistakes = exercise.commonMistakes,
                        kneeNote = exercise.kneeSafetyNote
                    )

                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun DarebeeVideoDetailDialog(
    detail: DarebeeExerciseDetail,
    isSavedOffline: Boolean,
    onSaveOffline: () -> Unit,
    onSpeakSteps: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            shape = RoundedCornerShape(18.dp),
            color = TacticalColors.SurfaceDark,
            border = BorderStroke(1.dp, TacticalColors.BorderHairline)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TacticalColors.CardDark)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DAREBEE VIDEO EXERCISE GUIDE",
                            color = TacticalColors.PhysicalCrimson,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = detail.title,
                            color = TacticalColors.TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = {
                        val txt = "${detail.title}. " + detail.steps.mapIndexed { i, s -> "Step ${i + 1}: $s" }.joinToString(" ")
                        onSpeakSteps(txt)
                    }) {
                        Icon(Icons.AutoMirrored.Rounded.VolumeUp, "Speak", tint = TacticalColors.MentalCyan)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, "Close", tint = TacticalColors.TextPrimary)
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KneeSafetyPill(isSafe = detail.kneeSafe, label = if (detail.kneeSafe) "KNEE-SAFE EXERCISE" else "CAUTION: CHECK KNEE")

                    if (detail.youtubeEmbedUrl.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black)
                                .border(1.dp, TacticalColors.BorderHairline, RoundedCornerShape(12.dp))
                        ) {
                            EmbeddedVideoWebView(url = detail.youtubeEmbedUrl)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onSaveOffline,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSavedOffline) TacticalColors.FinancialEmerald else TacticalColors.MentalCyan,
                                contentColor = TacticalColors.AmoledBlack
                            )
                        ) {
                            Icon(
                                if (isSavedOffline) Icons.Rounded.BookmarkAdded else Icons.Rounded.BookmarkAdd,
                                null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (isSavedOffline) "SAVED OFFLINE" else "SAVE GUIDE OFFLINE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                runCatching {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(detail.youtubeWatchUrl)))
                                }
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Rounded.PlayCircle, null, tint = TacticalColors.PhysicalCrimson, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("YOUTUBE", color = TacticalColors.PhysicalCrimson, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (detail.worksMuscles.isNotBlank()) {
                        TacticalCard {
                            Text("MUSCLES WORKED", color = TacticalColors.ArcadeGold, style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(detail.worksMuscles, color = TacticalColors.TextPrimary, fontSize = 13.sp)
                        }
                    }

                    StepByStepExecutionCard(
                        steps = detail.steps,
                        breathing = detail.breathing.ifBlank { "Exhale on effort, inhale on return." },
                        commonMistakes = detail.commonMistakes.ifBlank { "Moving too fast or losing core tension." },
                        kneeNote = detail.kneeWarning
                    )

                    if (detail.makeEasier.isNotBlank() || detail.makeHarder.isNotBlank()) {
                        TacticalCard {
                            if (detail.makeEasier.isNotBlank()) {
                                Text("MAKE IT EASIER", color = TacticalColors.FinancialEmerald, style = MaterialTheme.typography.labelMedium)
                                Text(detail.makeEasier, color = TacticalColors.TextPrimary, fontSize = 12.sp)
                                Spacer(Modifier.height(8.dp))
                            }
                            if (detail.makeHarder.isNotBlank()) {
                                Text("MAKE IT HARDER", color = TacticalColors.PhysicalCrimson, style = MaterialTheme.typography.labelMedium)
                                Text(detail.makeHarder, color = TacticalColors.TextPrimary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepByStepExecutionCard(
    steps: List<String>,
    breathing: String,
    commonMistakes: String,
    kneeNote: String
) {
    TacticalCard {
        Text("STEP-BY-STEP EXECUTION", color = TacticalColors.MentalCyan, style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(8.dp))
        steps.forEachIndexed { idx, step ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(TacticalColors.MentalCyan.copy(alpha = 0.2f))
                        .border(1.dp, TacticalColors.MentalCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${idx + 1}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = TacticalColors.MentalCyan
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = step,
                    fontSize = 13.sp,
                    color = TacticalColors.TextPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        Text("BREATHING CADENCE", color = TacticalColors.FinancialEmerald, style = MaterialTheme.typography.labelMedium)
        Text(breathing, fontSize = 12.sp, color = TacticalColors.TextPrimary)

        Spacer(Modifier.height(8.dp))
        Text("COMMON MISTAKES TO AVOID", color = TacticalColors.CautionAmber, style = MaterialTheme.typography.labelMedium)
        Text(commonMistakes, fontSize = 12.sp, color = TacticalColors.TextPrimary)

        Spacer(Modifier.height(8.dp))
        Text("KNEE PROTECTION CHECK", color = TacticalColors.KneeSafeGreen, style = MaterialTheme.typography.labelMedium)
        Text(kneeNote, fontSize = 12.sp, color = TacticalColors.KneeSafeGreen)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun EmbeddedVideoWebView(url: String) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()
                loadUrl(url)
            }
        },
        update = { webView ->
            if (webView.url != url) {
                webView.loadUrl(url)
            }
        }
    )
}

/**
 * 60fps Offline Vector Motion-Demo Player showing proper joint mechanics, bench/machine context,
 * slow-motion toggle, and phase label (SETUP -> ECCENTRIC -> CONCENTRIC).
 */
@Composable
fun ExerciseMotionDemoPlayer(
    motionType: ExerciseMotionType,
    kneeNote: String
) {
    var playing by remember { mutableStateOf(true) }
    var slowMo by remember { mutableStateOf(false) }

    val durationMs = if (slowMo) 4400 else 2400
    val infinite = rememberInfiniteTransition(label = "exerciseDemo")
    val animPhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )
    val t = if (playing) animPhase else 0.5f
    // Smooth 0..1..0 wave
    val wave = ((1f - cos(t * 2f * PI.toFloat())) / 2f).coerceIn(0f, 1f)

    val phaseLabel = when {
        motionType == ExerciseMotionType.PLANK || motionType == ExerciseMotionType.MOBILITY_BREATH ->
            if (t < 0.5f) "PHASE: BRACE & HOLD" else "PHASE: STEADY BREATH"
        t < 0.2f -> "1. SETUP POSITION"
        t < 0.55f -> "2. CONTROLLED MOVEMENT"
        else -> "3. DRIVE & RESET"
    }

    TacticalCard(borderColor = TacticalColors.MentalCyan.copy(alpha = 0.45f)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = phaseLabel,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = TacticalColors.ArcadeGold
            )
            Text(
                text = if (slowMo) "0.5x FORM SPEED" else "1.0x TEMPO",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = TacticalColors.MentalCyan
            )
        }

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(176.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(TacticalColors.AmoledBlack)
                .border(1.dp, TacticalColors.BorderHairline, RoundedCornerShape(12.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                drawExerciseFigure(motionType, wave, t)
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { playing = !playing },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    null,
                    tint = TacticalColors.MentalCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(if (playing) "PAUSE DEMO" else "PLAY DEMO", color = TacticalColors.MentalCyan, fontSize = 11.sp)
            }
            OutlinedButton(
                onClick = { slowMo = !slowMo },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    Icons.Rounded.SlowMotionVideo,
                    null,
                    tint = if (slowMo) TacticalColors.ArcadeGold else TacticalColors.TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    if (slowMo) "NORMAL 1x" else "SLOW-MO 0.5x",
                    color = if (slowMo) TacticalColors.ArcadeGold else TacticalColors.TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

private fun DrawScope.drawExerciseFigure(
    type: ExerciseMotionType,
    wave: Float,
    t: Float
) {
    val w = size.width
    val h = size.height
    val floorY = h * 0.88f
    val bodyColor = Color(0xFFF3F6FC)
    val jointColor = Color(0xFF00E5FF)
    val equipColor = Color(0xFF475569)
    val activeAccent = Color(0xFFFF3B4E)
    val safeGreen = Color(0xFF22C55E)

    // Floor reference line
    drawLine(Color(0xFF1E293B), Offset(0f, floorY), Offset(w, floorY), strokeWidth = 2.dp.toPx())

    fun limb(a: Offset, b: Offset, c: Color = bodyColor, widthDp: Float = 5f) {
        drawLine(c, a, b, strokeWidth = widthDp.dp.toPx(), cap = StrokeCap.Round)
    }

    fun joint(p: Offset, c: Color = jointColor, rDp: Float = 4.5f) {
        drawCircle(c, radius = rDp.dp.toPx(), center = p)
    }

    fun head(c: Offset, rDp: Float = 11f) {
        drawCircle(bodyColor, radius = rDp.dp.toPx(), center = c)
    }

    when (type) {
        ExerciseMotionType.WALK_INCLINE -> {
            // Sloped treadmill deck
            val deckL = Offset(w * 0.2f, floorY)
            val deckR = Offset(w * 0.8f, floorY - h * 0.14f)
            limb(deckL, deckR, equipColor, 6f)
            limb(deckR, Offset(w * 0.78f, floorY - h * 0.62f), equipColor, 4f)
            val swing = sin(t * 2f * PI.toFloat())
            val hip = Offset(w * 0.50f, floorY - h * 0.42f)
            val shoulder = Offset(w * 0.53f, floorY - h * 0.68f)
            head(Offset(w * 0.55f, floorY - h * 0.79f))
            limb(hip, shoulder)
            // Legs alternating soft stride
            val foot1 = Offset(w * (0.48f + swing * 0.10f), floorY - h * 0.08f)
            val knee1 = Offset((hip.x + foot1.x) / 2 + 6f, (hip.y + foot1.y) / 2)
            val foot2 = Offset(w * (0.48f - swing * 0.10f), floorY - h * 0.08f)
            val knee2 = Offset((hip.x + foot2.x) / 2 + 6f, (hip.y + foot2.y) / 2)
            limb(hip, knee1); limb(knee1, foot1)
            limb(hip, knee2, bodyColor.copy(alpha = 0.6f)); limb(knee2, foot2, bodyColor.copy(alpha = 0.6f))
            joint(knee1, safeGreen)
            // Arm swing
            limb(shoulder, Offset(shoulder.x + swing * 22f, shoulder.y + 26f), activeAccent, 4f)
        }

        ExerciseMotionType.ROWING -> {
            // Rail
            limb(Offset(w * 0.12f, floorY - 8f), Offset(w * 0.88f, floorY - 8f), equipColor, 5f)
            val flywheel = Offset(w * 0.80f, floorY - h * 0.34f)
            drawCircle(equipColor, radius = 16.dp.toPx(), center = flywheel)
            val slideX = w * (0.52f - wave * 0.24f)
            val hip = Offset(slideX, floorY - h * 0.20f)
            val lean = -0.15f + wave * 0.38f
            val shoulder = Offset(hip.x - sin(lean) * 42f, hip.y - cos(lean) * 42f)
            head(Offset(shoulder.x, shoulder.y - 20f), 10f)
            limb(hip, shoulder)
            val foot = Offset(w * 0.68f, floorY - 12f)
            val knee = Offset(slideX + 26f + (1f - wave) * 14f, floorY - h * (0.34f - wave * 0.14f))
            limb(hip, knee); limb(knee, foot)
            joint(knee, safeGreen)
            val handle = Offset(w * (0.64f - wave * 0.30f), shoulder.y + 12f)
            limb(flywheel, handle, activeAccent, 2.5f)
            limb(shoulder, handle, activeAccent, 4f)
        }

        ExerciseMotionType.BENCH_PRESS, ExerciseMotionType.CHEST_FLY -> {
            val benchY = floorY - h * 0.25f
            limb(Offset(w * 0.22f, benchY), Offset(w * 0.72f, benchY), equipColor, 7f)
            limb(Offset(w * 0.30f, benchY), Offset(w * 0.30f, floorY), equipColor, 5f)
            limb(Offset(w * 0.64f, benchY), Offset(w * 0.64f, floorY), equipColor, 5f)
            val shoulder = Offset(w * 0.36f, benchY - 10f)
            val hip = Offset(w * 0.60f, benchY - 10f)
            val knee = Offset(w * 0.74f, benchY - 4f)
            head(Offset(w * 0.24f, benchY - 12f), 10f)
            limb(shoulder, hip); limb(hip, knee); limb(knee, Offset(w * 0.76f, floorY))
            val pressHeight = 20f + wave * 56f
            val elbow = Offset(shoulder.x + (if (type == ExerciseMotionType.CHEST_FLY) 10f else 18f), shoulder.y - pressHeight * 0.55f)
            val hand = Offset(shoulder.x, shoulder.y - pressHeight)
            limb(shoulder, elbow, activeAccent); limb(elbow, hand, activeAccent)
            drawCircle(activeAccent, radius = 6.dp.toPx(), center = hand)
            joint(shoulder)
        }

        ExerciseMotionType.SHOULDER_PRESS, ExerciseMotionType.TRICEP_EXT -> {
            // Upright bench
            val seatY = floorY - h * 0.28f
            limb(Offset(w * 0.38f, seatY), Offset(w * 0.58f, seatY), equipColor, 6f)
            limb(Offset(w * 0.40f, seatY), Offset(w * 0.37f, floorY - h * 0.66f), equipColor, 5f)
            val hip = Offset(w * 0.46f, seatY - 8f)
            val shoulder = Offset(w * 0.43f, seatY - h * 0.30f)
            head(Offset(w * 0.43f, shoulder.y - 20f), 10f)
            limb(hip, shoulder)
            limb(hip, Offset(w * 0.62f, seatY - 6f)); limb(Offset(w * 0.62f, seatY - 6f), Offset(w * 0.64f, floorY))
            val armEndY = if (type == ExerciseMotionType.SHOULDER_PRESS) {
                shoulder.y - 10f - wave * 52f
            } else {
                shoulder.y - 54f + (1f - wave) * 28f
            }
            val elbow = if (type == ExerciseMotionType.TRICEP_EXT) Offset(shoulder.x + 4f, shoulder.y - 36f) else Offset(shoulder.x + 14f, (shoulder.y + armEndY) / 2)
            val hand = Offset(shoulder.x + (if (type == ExerciseMotionType.TRICEP_EXT && wave < 0.5f) -10f else 4f), armEndY)
            limb(shoulder, elbow, activeAccent); limb(elbow, hand, activeAccent)
            drawCircle(activeAccent, radius = 5.dp.toPx(), center = hand)
            joint(elbow)
        }

        ExerciseMotionType.LAT_PULLDOWN -> {
            val seatY = floorY - h * 0.25f
            limb(Offset(w * 0.38f, seatY), Offset(w * 0.60f, seatY), equipColor, 6f)
            limb(Offset(w * 0.49f, 8f), Offset(w * 0.49f, seatY - 22f + (1f - wave) * 0f), equipColor, 2f)
            val hip = Offset(w * 0.49f, seatY - 8f)
            val shoulder = Offset(w * 0.48f, seatY - h * 0.32f)
            head(Offset(w * 0.48f, shoulder.y - 20f), 10f)
            limb(hip, shoulder)
            limb(hip, Offset(w * 0.62f, seatY - 4f)); limb(Offset(w * 0.62f, seatY - 4f), Offset(w * 0.62f, floorY))
            val barY = 22f + wave * (shoulder.y - 32f)
            limb(Offset(w * 0.34f, barY), Offset(w * 0.64f, barY), activeAccent, 4f)
            limb(shoulder, Offset(w * 0.36f, barY), activeAccent, 4f)
            limb(shoulder, Offset(w * 0.62f, barY), activeAccent, 4f)
            joint(shoulder)
        }

        ExerciseMotionType.BENT_ROW -> {
            // Incline bench support
            limb(Offset(w * 0.32f, floorY - h * 0.18f), Offset(w * 0.62f, floorY - h * 0.46f), equipColor, 7f)
            val hip = Offset(w * 0.36f, floorY - h * 0.28f)
            val shoulder = Offset(w * 0.58f, floorY - h * 0.50f)
            head(Offset(w * 0.65f, shoulder.y - 12f), 10f)
            limb(hip, shoulder)
            limb(hip, Offset(w * 0.24f, floorY))
            val handY = shoulder.y + 52f - wave * 34f
            val elbow = Offset(shoulder.x - 10f - wave * 12f, (shoulder.y + handY) / 2 - 8f)
            val hand = Offset(shoulder.x - 4f, handY)
            limb(shoulder, elbow, activeAccent); limb(elbow, hand, activeAccent)
            drawCircle(activeAccent, radius = 5.dp.toPx(), center = hand)
            joint(shoulder)
        }

        ExerciseMotionType.BICEP_CURL -> {
            val foot = Offset(w * 0.48f, floorY)
            val knee = Offset(w * 0.49f, floorY - h * 0.24f)
            val hip = Offset(w * 0.48f, floorY - h * 0.48f)
            val shoulder = Offset(w * 0.48f, floorY - h * 0.74f)
            head(Offset(w * 0.48f, shoulder.y - 20f), 10f)
            limb(foot, knee); limb(knee, hip); limb(hip, shoulder)
            val elbow = Offset(shoulder.x + 2f, shoulder.y + 34f)
            val angle = -1.2f + wave * 2.3f
            val hand = Offset(elbow.x + cos(angle) * 32f, elbow.y - sin(angle) * 32f)
            limb(shoulder, elbow); limb(elbow, hand, activeAccent)
            drawCircle(activeAccent, radius = 5.dp.toPx(), center = hand)
            joint(elbow)
        }

        ExerciseMotionType.DEAD_BUG -> {
            val backY = floorY - 14f
            val shoulder = Offset(w * 0.34f, backY)
            val hip = Offset(w * 0.58f, backY)
            head(Offset(w * 0.24f, backY - 4f), 10f)
            limb(shoulder, hip)
            // Opposite arm + leg extending
            val armEnd = Offset(shoulder.x - wave * 42f, backY - 44f + wave * 34f)
            val knee = Offset(hip.x + 12f + wave * 28f, backY - 38f + wave * 22f)
            val foot = Offset(knee.x + 28f, knee.y - 6f + wave * 10f)
            limb(shoulder, armEnd, activeAccent)
            limb(hip, knee, activeAccent); limb(knee, foot, activeAccent)
            joint(hip, safeGreen)
        }

        ExerciseMotionType.PLANK -> {
            val elbow = Offset(w * 0.28f, floorY - 8f)
            val shoulder = Offset(w * 0.30f, floorY - h * 0.32f)
            val hip = Offset(w * 0.54f, floorY - h * 0.29f)
            val ankle = Offset(w * 0.78f, floorY - 6f)
            // Straight alignment guide
            limb(Offset(w * 0.24f, floorY - h * 0.34f), Offset(w * 0.80f, floorY - 6f), safeGreen.copy(alpha = 0.4f), 2f)
            head(Offset(w * 0.22f, shoulder.y - 6f), 10f)
            limb(elbow, Offset(w * 0.36f, floorY - 8f), bodyColor, 4f)
            limb(elbow, shoulder); limb(shoulder, hip); limb(hip, ankle)
            joint(hip, safeGreen); joint(shoulder)
        }

        ExerciseMotionType.CRUNCH_CORE -> {
            val hip = Offset(w * 0.52f, floorY - 12f)
            val knee = Offset(w * 0.66f, floorY - h * 0.30f)
            val foot = Offset(w * 0.78f, floorY - 8f)
            limb(hip, knee); limb(knee, foot)
            val curl = wave * 0.55f
            val shoulder = Offset(hip.x - cos(curl) * 52f, hip.y - sin(curl) * 52f - 4f)
            head(Offset(shoulder.x - 16f, shoulder.y - 10f), 10f)
            limb(hip, shoulder, activeAccent)
            joint(hip)
        }

        ExerciseMotionType.GLUTE_BRIDGE -> {
            val shoulder = Offset(w * 0.28f, floorY - 12f)
            val foot = Offset(w * 0.74f, floorY - 10f)
            val knee = Offset(w * 0.68f, floorY - h * 0.34f)
            val hip = Offset(w * 0.48f, floorY - 12f - wave * h * 0.24f)
            head(Offset(w * 0.20f, floorY - 14f), 10f)
            limb(shoulder, hip, activeAccent); limb(hip, knee, activeAccent); limb(knee, foot)
            joint(hip, activeAccent); joint(knee, safeGreen)
        }

        ExerciseMotionType.ROMANIAN_DEADLIFT -> {
            val foot = Offset(w * 0.48f, floorY)
            val knee = Offset(w * 0.50f, floorY - h * 0.24f) // soft 15° bend kept fixed!
            val hip = Offset(w * (0.48f - wave * 0.14f), floorY - h * (0.48f - wave * 0.04f))
            val hingeAngle = wave * 1.15f
            val shoulder = Offset(hip.x + sin(hingeAngle) * 52f, hip.y - cos(hingeAngle) * 52f)
            head(Offset(shoulder.x + 12f, shoulder.y - 12f), 10f)
            limb(foot, knee); limb(knee, hip); limb(hip, shoulder)
            val hand = Offset(shoulder.x + 6f, shoulder.y + 42f)
            limb(shoulder, hand, activeAccent)
            drawCircle(activeAccent, radius = 5.dp.toPx(), center = hand)
            joint(knee, safeGreen); joint(hip, activeAccent)
        }

        ExerciseMotionType.LEG_PRESS_SHALLOW -> {
            // Angled sled + shallow 45-60 deg range
            val seat = Offset(w * 0.28f, floorY - h * 0.18f)
            val shoulder = Offset(w * 0.20f, floorY - h * 0.44f)
            head(Offset(shoulder.x - 6f, shoulder.y - 18f), 10f)
            limb(seat, shoulder)
            val sledX = w * (0.76f - wave * 0.16f) // shallow travel only!
            val foot = Offset(sledX, floorY - h * 0.48f)
            limb(Offset(sledX + 8f, floorY - h * 0.64f), Offset(sledX + 8f, floorY - h * 0.34f), equipColor, 7f)
            val knee = Offset((seat.x + foot.x) / 2 - 4f, (seat.y + foot.y) / 2 - 14f - wave * 14f)
            limb(seat, knee, activeAccent); limb(knee, foot, activeAccent)
            joint(knee, safeGreen)
        }

        ExerciseMotionType.CALF_RAISE -> {
            val hip = Offset(w * 0.42f, floorY - h * 0.34f)
            val shoulder = Offset(w * 0.40f, floorY - h * 0.64f)
            head(Offset(w * 0.40f, shoulder.y - 18f), 10f)
            limb(hip, shoulder)
            val knee = Offset(w * 0.60f, floorY - h * 0.34f)
            val heelY = floorY - wave * 14f
            limb(hip, knee); limb(knee, Offset(w * 0.60f, heelY))
            limb(Offset(w * 0.60f, heelY), Offset(w * 0.68f, floorY), activeAccent, 5f)
            drawCircle(activeAccent, radius = 6.dp.toPx(), center = Offset(w * 0.58f, knee.y - 10f))
        }

        ExerciseMotionType.STANDING_CORE, ExerciseMotionType.SHADOW_BOXING, ExerciseMotionType.MOBILITY_BREATH -> {
            val foot = Offset(w * 0.48f, floorY)
            val knee = Offset(w * 0.49f, floorY - h * 0.24f)
            val hip = Offset(w * 0.48f, floorY - h * 0.48f)
            val sway = if (type == ExerciseMotionType.STANDING_CORE) (wave - 0.5f) * 26f else 0f
            val shoulder = Offset(w * 0.48f + sway, floorY - h * 0.74f)
            head(Offset(shoulder.x, shoulder.y - 20f), 10f)
            limb(foot, knee); limb(knee, hip); limb(hip, shoulder)
            if (type == ExerciseMotionType.SHADOW_BOXING) {
                val punchX = shoulder.x + 16f + wave * 42f
                limb(shoulder, Offset(punchX, shoulder.y + 4f), activeAccent, 4.5f)
                drawCircle(activeAccent, radius = 5.dp.toPx(), center = Offset(punchX, shoulder.y + 4f))
            } else {
                val reach = Offset(shoulder.x + 24f, shoulder.y - 10f - wave * 28f)
                limb(shoulder, reach, activeAccent, 4f)
            }
            joint(knee, safeGreen)
        }
    }
}
