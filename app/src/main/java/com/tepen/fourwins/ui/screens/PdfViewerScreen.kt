package com.tepen.fourwins.ui.screens

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tepen.fourwins.MainViewModel
import com.tepen.fourwins.model.PdfLibraryItem
import com.tepen.fourwins.ui.theme.TacticalColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun PdfViewerScreen(
    vm: MainViewModel,
    pdfItem: PdfLibraryItem,
    onClose: () -> Unit
) {
    val profile by vm.profile.collectAsState()
    var currentPage by remember { mutableIntStateOf(pdfItem.lastPage.coerceIn(0, (pdfItem.pageCount - 1).coerceAtLeast(0))) }
    var bookmarks by remember { mutableStateOf(pdfItem.bookmarkedPages) }
    var pageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showJumpDialog by remember { mutableStateOf(false) }
    var jumpInput by remember { mutableStateOf("${currentPage + 1}") }

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    // Docked Mini Workout Timer right inside the PDF viewer!
    var presetSec by remember { mutableIntStateOf(45) }
    var timerSec by remember { mutableIntStateOf(45) }
    var timerRunning by remember { mutableStateOf(false) }
    var setCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(timerRunning, timerSec) {
        if (timerRunning && timerSec > 0) {
            delay(1000L)
            timerSec--
            if (timerSec in 1..3) {
                vm.coach.beepCountdown(profile.beepEnabled)
                vm.coach.vibrateTick(profile.hapticsEnabled)
            }
            if (timerSec == 0) {
                timerRunning = false
                setCount++
                vm.coach.beepVictory(profile.beepEnabled)
                vm.coach.vibrateVictory(profile.hapticsEnabled)
                vm.coach.speak("Timer done. Set $setCount logged.", profile.voiceCoachEnabled)
            }
        }
    }

    // Render current page cleanly off the UI thread
    LaunchedEffect(currentPage, pdfItem.filePath) {
        scale = 1f
        offset = Offset.Zero
        pageBitmap = withContext(Dispatchers.IO) {
            runCatching {
                val file = File(pdfItem.filePath)
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
                    PdfRenderer(pfd).use { renderer ->
                        if (currentPage !in 0 until renderer.pageCount) return@runCatching null
                        renderer.openPage(currentPage).use { page ->
                            val targetW = (page.width * 2).coerceIn(900, 1800)
                            val targetH = (page.height * (targetW.toFloat() / page.width)).toInt()
                            val bmp = Bitmap.createBitmap(targetW, targetH, Bitmap.Config.ARGB_8888)
                            bmp.eraseColor(AndroidColor.WHITE)
                            page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                            bmp
                        }
                    }
                }
            }.getOrNull()
        }
        vm.updatePdfProgress(pdfItem.copy(lastPage = currentPage, bookmarkedPages = bookmarks))
    }

    DisposableEffect(Unit) {
        onDispose {
            vm.updatePdfProgress(pdfItem.copy(lastPage = currentPage, bookmarkedPages = bookmarks))
        }
    }

    val isBookmarked = currentPage in bookmarks

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TacticalColors.AmoledBlack)
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TacticalColors.SurfaceDark)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = TacticalColors.TextPrimary)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        jumpInput = "${currentPage + 1}"
                        showJumpDialog = true
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = pdfItem.title,
                    color = TacticalColors.TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "PAGE ${currentPage + 1} / ${pdfItem.pageCount} (TAP TO JUMP)",
                    color = TacticalColors.MentalCyan,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            IconButton(
                onClick = {
                    val next = bookmarks.toMutableSet()
                    if (isBookmarked) next.remove(currentPage) else next.add(currentPage)
                    bookmarks = next
                    vm.updatePdfProgress(pdfItem.copy(lastPage = currentPage, bookmarkedPages = next))
                }
            ) {
                Icon(
                    if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                    "Bookmark",
                    tint = if (isBookmarked) TacticalColors.ArcadeGold else TacticalColors.TextSecondary
                )
            }
        }

        // PDF Page Viewport (Pinch-to-Zoom + Pan)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF101318))
                .pointerInput(currentPage) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(1f, 4f)
                        offset = if (scale > 1f) offset + pan else Offset.Zero
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            val bmp = pageBitmap
            if (bmp != null) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "PDF Page ${currentPage + 1}",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                )
            } else {
                Text("Rendering page...", color = TacticalColors.TextSecondary, fontSize = 13.sp)
            }

            // Left / Right Page Overlay Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(TacticalColors.AmoledBlack.copy(alpha = 0.8f))
                        .border(1.dp, TacticalColors.BorderHairline, RoundedCornerShape(10.dp))
                        .clickable(enabled = currentPage > 0) { currentPage-- }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.ChevronLeft, null, tint = if (currentPage > 0) Color.White else TacticalColors.TextMuted)
                        Text("PREV", color = if (currentPage > 0) Color.White else TacticalColors.TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(TacticalColors.AmoledBlack.copy(alpha = 0.8f))
                        .border(1.dp, TacticalColors.BorderHairline, RoundedCornerShape(10.dp))
                        .clickable(enabled = currentPage < pdfItem.pageCount - 1) { currentPage++ }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("NEXT", color = if (currentPage < pdfItem.pageCount - 1) Color.White else TacticalColors.TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Icon(Icons.Rounded.ChevronRight, null, tint = if (currentPage < pdfItem.pageCount - 1) Color.White else TacticalColors.TextMuted)
                    }
                }
            }
        }

        // Docked Tactical Mini Workout Timer + Set Counter
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(TacticalColors.CardDark)
                .border(1.dp, TacticalColors.BorderHairline)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(30, 45, 60, 90).forEach { sec ->
                        val active = presetSec == sec
                        Text(
                            text = "${sec}s",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (active) TacticalColors.AmoledBlack else TacticalColors.TextSecondary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (active) TacticalColors.MentalCyan else TacticalColors.AmoledBlack)
                                .clickable {
                                    presetSec = sec
                                    timerSec = sec
                                    timerRunning = false
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = "SETS: $setCount",
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = TacticalColors.ArcadeGold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(TacticalColors.AmoledBlack)
                        .clickable { setCount++ }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format("%02d:%02d", timerSec / 60, timerSec % 60),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (timerRunning) TacticalColors.PhysicalCrimson else TacticalColors.TextPrimary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = {
                            timerRunning = false
                            timerSec = presetSec
                        }
                    ) {
                        Icon(Icons.Rounded.Refresh, "Reset", tint = TacticalColors.TextSecondary)
                    }
                    Button(
                        onClick = { setCount++ },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TacticalColors.CardElevated)
                    ) {
                        Icon(Icons.Rounded.Add, null, modifier = Modifier.size(16.dp), tint = TacticalColors.ArcadeGold)
                        Text("SET", color = TacticalColors.ArcadeGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            if (timerSec == 0) timerSec = presetSec
                            timerRunning = !timerRunning
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (timerRunning) TacticalColors.PhysicalCrimson else TacticalColors.MentalCyan
                        )
                    ) {
                        Icon(
                            if (timerRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            null,
                            tint = if (timerRunning) Color.White else TacticalColors.AmoledBlack,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            if (timerRunning) "PAUSE" else "TIMER",
                            color = if (timerRunning) Color.White else TacticalColors.AmoledBlack,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }

    if (showJumpDialog) {
        AlertDialog(
            onDismissRequest = { showJumpDialog = false },
            containerColor = TacticalColors.CardDark,
            title = { Text("Jump to Page (1–${pdfItem.pageCount})", color = TacticalColors.TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = jumpInput,
                        onValueChange = { jumpInput = it.filter { ch -> ch.isDigit() } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (bookmarks.isNotEmpty()) {
                        Text("Bookmarked Pages:", fontSize = 11.sp, color = TacticalColors.ArcadeGold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            bookmarks.sorted().take(6).forEach { bPage ->
                                Text(
                                    text = "p.${bPage + 1}",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TacticalColors.AmoledBlack,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(TacticalColors.ArcadeGold)
                                        .clickable {
                                            currentPage = bPage
                                            showJumpDialog = false
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = (jumpInput.toIntOrNull() ?: 1) - 1
                        currentPage = p.coerceIn(0, (pdfItem.pageCount - 1).coerceAtLeast(0))
                        showJumpDialog = false
                    }
                ) {
                    Text("GO")
                }
            },
            dismissButton = {
                TextButton(onClick = { showJumpDialog = false }) { Text("Cancel") }
            }
        )
    }
}
