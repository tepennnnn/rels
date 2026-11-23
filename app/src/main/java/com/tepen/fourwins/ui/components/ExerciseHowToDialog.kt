package com.tepen.fourwins.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.WarningAmber
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.disk.DiskCache
import coil.request.ImageRequest
import com.tepen.fourwins.model.DarebeeExerciseDetail
import com.tepen.fourwins.model.WorkoutExercise
import com.tepen.fourwins.ui.theme.TacticalColors

@Composable
fun rememberGifImageLoader(): ImageLoader {
    val context = LocalContext.current
    return remember(context) {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("darebee_gif_cache"))
                    .maxSizeBytes(80L * 1024L * 1024L)
                    .build()
            }
            .build()
    }
}

@Composable
fun DarebeeGifBanner(
    darebeeSlug: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val gifLoader = rememberGifImageLoader()
    val gifUrl = "https://darebee.com/images/gifs/$darebeeSlug.gif"
    val fallbackThumbUrl = "https://darebee.com/images/library/$darebeeSlug.jpg"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(gifUrl)
                .crossfade(true)
                .build(),
            imageLoader = gifLoader,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            error = {
                SubcomposeAsyncImage(
                    model = fallbackThumbUrl,
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().padding(8.dp)
                )
            }
        )
    }
}

@Composable
fun ExerciseHowToDialog(
    exercise: WorkoutExercise,
    onSpeakSteps: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val hasDarebeeGif = !exercise.darebeeSlug.isNullOrBlank()
    var showVideoPlayer by remember { mutableStateOf(!hasDarebeeGif) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = TacticalColors.AmoledBlack
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Clean Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TacticalColors.SurfaceDark)
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, "Close", tint = TacticalColors.TextPrimary)
                    }
                    Column(modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                        Text(
                            text = exercise.equipment.label,
                            color = TacticalColors.MentalCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = exercise.name,
                            color = TacticalColors.TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2
                        )
                    }
                    IconButton(onClick = {
                        val spoken = buildString {
                            append(exercise.name)
                            append(". ")
                            exercise.steps.forEachIndexed { i, s -> append("Step ${i + 1}: $s ") }
                        }
                        onSpeakSteps(spoken)
                    }) {
                        Icon(Icons.AutoMirrored.Rounded.VolumeUp, "Listen to steps", tint = TacticalColors.MentalCyan)
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    val ytSearchUrl = "https://m.youtube.com/results?search_query=${Uri.encode(exercise.videoQuery)}"

                    // Media Area: Real Darebee GIF + Video Tutorial Switcher
                    if (hasDarebeeGif) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(TacticalColors.CardDark)
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("Darebee Motion Loop", "Video Tutorial").forEachIndexed { idx, label ->
                                val active = (idx == 1) == showVideoPlayer
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(9.dp))
                                        .background(if (active) TacticalColors.MentalCyan else Color.Transparent)
                                        .clickable { showVideoPlayer = (idx == 1) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) TacticalColors.AmoledBlack else TacticalColors.TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    if (hasDarebeeGif && !showVideoPlayer) {
                        DarebeeGifBanner(
                            darebeeSlug = exercise.darebeeSlug!!,
                            contentDescription = exercise.name
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(230.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.Black)
                            ) {
                                EmbeddedVideoWebView(url = ytSearchUrl)
                            }
                            OutlinedButton(
                                onClick = {
                                    runCatching {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(ytSearchUrl)))
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(46.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Rounded.OpenInNew, null, tint = TacticalColors.PhysicalCrimson, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Open Full Video in YouTube", color = TacticalColors.TextPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Key Specs Pill Row (Tempo, Muscles, Knee-Safe)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = TacticalColors.CardDark
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Rounded.Speed, null, tint = TacticalColors.ArcadeGold, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Tempo", fontSize = 12.sp, color = TacticalColors.TextSecondary)
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(exercise.tempo, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TacticalColors.TextPrimary)
                                }
                            }
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = TacticalColors.CardDark
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Sets & Target", fontSize = 12.sp, color = TacticalColors.TextSecondary)
                                    Spacer(Modifier.height(4.dp))
                                    Text("${exercise.sets} × ${exercise.repsOrSeconds}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TacticalColors.MentalCyan)
                                }
                            }
                        }
                        if (exercise.targetMuscles.isNotBlank()) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                color = TacticalColors.CardDark
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Target Muscles", fontSize = 12.sp, color = TacticalColors.TextSecondary)
                                    Spacer(Modifier.height(2.dp))
                                    Text(exercise.targetMuscles, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TacticalColors.TextPrimary)
                                }
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
            modifier = Modifier.fillMaxSize(),
            color = TacticalColors.AmoledBlack
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TacticalColors.SurfaceDark)
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, "Close", tint = TacticalColors.TextPrimary)
                    }
                    Column(modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                        Text(
                            text = "Darebee Video Guide",
                            color = TacticalColors.PhysicalCrimson,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = detail.title,
                            color = TacticalColors.TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = {
                        val txt = "${detail.title}. " + detail.steps.mapIndexed { i, s -> "Step ${i + 1}: $s" }.joinToString(" ")
                        onSpeakSteps(txt)
                    }) {
                        Icon(Icons.AutoMirrored.Rounded.VolumeUp, "Speak", tint = TacticalColors.MentalCyan)
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    KneeSafetyPill(isSafe = detail.kneeSafe, label = if (detail.kneeSafe) "Knee-Safe Exercise" else "Knee Caution")

                    if (detail.youtubeEmbedUrl.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black)
                        ) {
                            EmbeddedVideoWebView(url = detail.youtubeEmbedUrl)
                        }
                    } else {
                        DarebeeGifBanner(darebeeSlug = detail.slug, contentDescription = detail.title)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onSaveOffline,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSavedOffline) TacticalColors.FinancialEmerald else TacticalColors.MentalCyan,
                                contentColor = TacticalColors.AmoledBlack
                            )
                        ) {
                            Icon(
                                if (isSavedOffline) Icons.Rounded.BookmarkAdded else Icons.Rounded.BookmarkAdd,
                                null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (isSavedOffline) "Saved Offline" else "Save Guide Offline",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                runCatching {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(detail.youtubeWatchUrl)))
                                }
                            },
                            modifier = Modifier.height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Rounded.PlayCircle, null, tint = TacticalColors.PhysicalCrimson, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("YouTube", color = TacticalColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    if (detail.worksMuscles.isNotBlank()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = TacticalColors.CardDark
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Muscles Worked", color = TacticalColors.ArcadeGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(4.dp))
                                Text(detail.worksMuscles, color = TacticalColors.TextPrimary, fontSize = 14.sp)
                            }
                        }
                    }

                    StepByStepExecutionCard(
                        steps = detail.steps,
                        breathing = detail.breathing.ifBlank { "Exhale on effort, inhale on return." },
                        commonMistakes = detail.commonMistakes.ifBlank { "Moving too fast or losing core tension." },
                        kneeNote = detail.kneeWarning
                    )

                    if (detail.makeEasier.isNotBlank() || detail.makeHarder.isNotBlank()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = TacticalColors.CardDark
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (detail.makeEasier.isNotBlank()) {
                                    Text("Make It Easier", color = TacticalColors.FinancialEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(detail.makeEasier, color = TacticalColors.TextPrimary, fontSize = 14.sp)
                                }
                                if (detail.makeHarder.isNotBlank()) {
                                    Text("Make It Harder", color = TacticalColors.PhysicalCrimson, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(detail.makeHarder, color = TacticalColors.TextPrimary, fontSize = 14.sp)
                                }
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = TacticalColors.CardDark
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Step-by-Step Execution",
                color = TacticalColors.TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            steps.forEachIndexed { idx, step ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(TacticalColors.MentalCyan.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${idx + 1}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TacticalColors.MentalCyan
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = step,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = TacticalColors.TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(TacticalColors.FinancialEmerald.copy(alpha = 0.1f))
                    .padding(12.dp)
            ) {
                Column {
                    Text("Breathing", color = TacticalColors.FinancialEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(2.dp))
                    Text(breathing, fontSize = 13.sp, color = TacticalColors.TextPrimary)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(TacticalColors.CautionAmber.copy(alpha = 0.1f))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Rounded.WarningAmber, null, tint = TacticalColors.CautionAmber, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Avoid Common Mistakes", color = TacticalColors.CautionAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(2.dp))
                        Text(commonMistakes, fontSize = 13.sp, color = TacticalColors.TextPrimary)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(TacticalColors.KneeSafeGreen.copy(alpha = 0.1f))
                    .padding(12.dp)
            ) {
                Column {
                    Text("Knee Protection Note", color = TacticalColors.KneeSafeGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(2.dp))
                    Text(kneeNote, fontSize = 13.sp, color = TacticalColors.TextPrimary)
                }
            }
        }
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
