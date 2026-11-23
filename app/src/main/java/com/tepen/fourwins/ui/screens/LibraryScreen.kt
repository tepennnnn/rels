package com.tepen.fourwins.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.OpenInBrowser
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.tepen.fourwins.MainViewModel
import com.tepen.fourwins.data.PreloadedCatalog
import com.tepen.fourwins.model.PdfLibraryItem
import com.tepen.fourwins.model.SavedDarebeeWorkout
import com.tepen.fourwins.network.DarebeeScraper
import com.tepen.fourwins.ui.components.KneeSafetyPill
import com.tepen.fourwins.ui.components.TacticalCard
import com.tepen.fourwins.ui.theme.TacticalColors
import java.io.File

@Composable
fun LibraryScreen(
    vm: MainViewModel,
    onOpenPdf: (PdfLibraryItem) -> Unit
) {
    val context = LocalContext.current
    var subTab by remember { mutableIntStateOf(0) } // 0 = Offline Library, 1 = Online Darebee

    val pdfs by vm.pdfLibrary.collectAsState()
    val savedPosters by vm.savedDarebee.collectAsState()
    val remoteItems by vm.remoteItems.collectAsState()
    val remoteLoading by vm.remoteLoading.collectAsState()
    val remoteError by vm.remoteError.collectAsState()
    val selectedFeed by vm.selectedFeed.collectAsState()

    var kneeSafeOnlyFilter by remember { mutableStateOf(true) }
    var onlinePage by remember { mutableIntStateOf(0) }
    var previewPoster by remember { mutableStateOf<SavedDarebeeWorkout?>(null) }

    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            vm.importPdf(uri)
        }
    }

    LaunchedEffect(subTab) {
        if (subTab == 1 && remoteItems.isEmpty() && !remoteLoading) {
            vm.loadDarebeeFeed(selectedFeed, 0)
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
                Text(
                    text = "DAREBEE HUB",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TacticalColors.TextPrimary
                )
                Text(
                    text = "Offline PDF books & posters + live Darebee browser",
                    fontSize = 12.sp,
                    color = TacticalColors.TextSecondary
                )
            }
        }

        // Sub-tab switcher
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(TacticalColors.CardDark)
                    .border(1.dp, TacticalColors.BorderHairline, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("MY OFFLINE PDFs", "BROWSE DAREBEE ONLINE").forEachIndexed { idx, label ->
                    val active = subTab == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (active) TacticalColors.MentalCyan else Color.Transparent)
                            .clickable { subTab = idx }
                            .padding(vertical = 9.dp),
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
        }

        if (subTab == 0) {
            // Import PDF CTA
            item {
                TacticalCard(borderColor = TacticalColors.MentalCyan.copy(alpha = 0.5f)) {
                    Text(
                        text = "IMPORT YOUR DAREBEE PDFs",
                        color = TacticalColors.MentalCyan,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Load your downloaded PDFs (100 Ab Workouts, 100 Dumbbell Workouts, 100 Fitness Challenges, Fitness Over 60) directly from Downloads or Google Drive for 100% offline viewing with built-in timer.",
                        fontSize = 12.sp,
                        color = TacticalColors.TextSecondary
                    )
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = { pdfPicker.launch(arrayOf("application/pdf")) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TacticalColors.MentalCyan,
                            contentColor = TacticalColors.AmoledBlack
                        )
                    ) {
                        Icon(Icons.Rounded.UploadFile, null)
                        Spacer(Modifier.width(6.dp))
                        Text("IMPORT PDF FROM DEVICE", fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (pdfs.isNotEmpty()) {
                item {
                    Text(
                        text = "IMPORTED PDF BOOKS (${pdfs.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = TacticalColors.TextSecondary
                    )
                }
                items(pdfs, key = { it.id }) { pdf ->
                    TacticalCard(onClick = { onOpenPdf(pdf) }) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(TacticalColors.PhysicalCrimson.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.PictureAsPdf, null, tint = TacticalColors.PhysicalCrimson)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pdf.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TacticalColors.TextPrimary
                                )
                                Text(
                                    text = "${pdf.pageCount} pages • Last: p.${pdf.lastPage + 1} • ${pdf.bookmarkedPages.size} bookmarks",
                                    fontSize = 11.sp,
                                    color = TacticalColors.TextSecondary
                                )
                            }
                            IconButton(onClick = { vm.deletePdf(pdf) }) {
                                Icon(Icons.Rounded.DeleteOutline, "Remove", tint = TacticalColors.TextMuted)
                            }
                        }
                    }
                }
            }

            if (savedPosters.isNotEmpty()) {
                item {
                    Text(
                        text = "SAVED OFFLINE DAREBEE POSTERS (${savedPosters.size})",
                        style = MaterialTheme.typography.labelMedium,
                        color = TacticalColors.TextSecondary
                    )
                }
                items(savedPosters, key = { it.id }) { poster ->
                    TacticalCard(onClick = { previewPoster = poster }) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(File(poster.localImagePath)),
                                contentDescription = poster.title,
                                modifier = Modifier
                                    .size(56.dp, 74.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(TacticalColors.AmoledBlack),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                KneeSafetyPill(isSafe = poster.kneeSafe)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = poster.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TacticalColors.TextPrimary
                                )
                                if (poster.focus.isNotBlank() || poster.difficulty.isNotBlank()) {
                                    Text(
                                        text = "${poster.focus} • ${poster.difficulty}".trim(' ', '•'),
                                        fontSize = 11.sp,
                                        color = TacticalColors.TextSecondary
                                    )
                                }
                            }
                            IconButton(onClick = { vm.deleteSavedDarebee(poster) }) {
                                Icon(Icons.Rounded.DeleteOutline, "Remove", tint = TacticalColors.TextMuted)
                            }
                        }
                    }
                }
            }

            // Official Darebee Books Directory
            item {
                Text(
                    text = "OFFICIAL DAREBEE PDF BOOKS (FREE DOWNLOAD)",
                    style = MaterialTheme.typography.labelMedium,
                    color = TacticalColors.TextSecondary
                )
            }
            items(PreloadedCatalog.officialBooksDirectory) { book ->
                TacticalCard(
                    onClick = {
                        runCatching {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(book.pageUrl)))
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(book.title, style = MaterialTheme.typography.titleMedium, color = TacticalColors.TextPrimary)
                            Text(book.subtitle, fontSize = 11.sp, color = TacticalColors.TextSecondary)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = book.kneeFriendlyTag.uppercase(),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TacticalColors.FinancialEmerald
                            )
                        }
                        Icon(Icons.Rounded.OpenInBrowser, "Open Darebee", tint = TacticalColors.MentalCyan)
                    }
                }
            }
        } else {
            // Online Darebee Browser
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DarebeeScraper.FeedType.entries.forEach { feed ->
                        FilterChip(
                            selected = selectedFeed == feed,
                            onClick = {
                                onlinePage = 0
                                vm.loadDarebeeFeed(feed, 0)
                            },
                            label = { Text(feed.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TacticalColors.PhysicalCrimson,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = kneeSafeOnlyFilter,
                        onClick = { kneeSafeOnlyFilter = !kneeSafeOnlyFilter },
                        label = { Text("Knee-Safe Filter", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TacticalColors.KneeSafeGreen.copy(alpha = 0.22f),
                            selectedLabelColor = TacticalColors.KneeSafeGreen
                        )
                    )
                    IconButton(onClick = { vm.loadDarebeeFeed(selectedFeed, 0) }) {
                        Icon(Icons.Rounded.Refresh, "Refresh", tint = TacticalColors.MentalCyan)
                    }
                }
            }

            if (remoteLoading && remoteItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = TacticalColors.MentalCyan)
                    }
                }
            }

            if (remoteError != null) {
                item {
                    TacticalCard(borderColor = TacticalColors.CautionAmber) {
                        Text(remoteError!!, color = TacticalColors.CautionAmber, fontSize = 12.sp)
                    }
                }
            }

            val visibleRemote = if (kneeSafeOnlyFilter) {
                remoteItems.filter { it.kneeSafeFlag }
            } else remoteItems

            items(visibleRemote, key = { it.url }) { item ->
                TacticalCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (item.thumbUrl.isNotBlank()) {
                            Image(
                                painter = rememberAsyncImagePainter(item.thumbUrl),
                                contentDescription = item.title,
                                modifier = Modifier
                                    .size(62.dp, 86.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(TacticalColors.AmoledBlack),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(12.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            KneeSafetyPill(isSafe = item.kneeSafeFlag)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = TacticalColors.TextPrimary
                            )
                            Text(
                                text = item.category.uppercase(),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TacticalColors.TextMuted
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { vm.saveRemoteWorkoutOffline(item) },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = TacticalColors.MentalCyan,
                                        contentColor = TacticalColors.AmoledBlack
                                    )
                                ) {
                                    Icon(Icons.Rounded.CloudDownload, null, modifier = Modifier.size(15.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("SAVE OFFLINE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = {
                                        runCatching {
                                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.url)))
                                        }
                                    },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Rounded.OpenInBrowser, null, modifier = Modifier.size(15.dp), tint = TacticalColors.TextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            if (remoteItems.isNotEmpty() && selectedFeed in listOf(DarebeeScraper.FeedType.WORKOUTS, DarebeeScraper.FeedType.CHALLENGES)) {
                item {
                    OutlinedButton(
                        onClick = {
                            onlinePage++
                            vm.loadDarebeeFeed(selectedFeed, onlinePage)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("LOAD MORE FROM DAREBEE.COM", color = TacticalColors.MentalCyan)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }

    // Full-screen Zoomable Saved Poster Viewer
    if (previewPoster != null) {
        val poster = previewPoster!!
        Dialog(
            onDismissRequest = { previewPoster = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            var scale by remember { mutableFloatStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(File(poster.localImagePath)),
                    contentDescription = poster.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(1f, 4f)
                                offset = if (scale > 1f) offset + pan else Offset.Zero
                            }
                        }
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.72f))
                        .padding(12.dp)
                        .align(Alignment.TopCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(poster.title, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(poster.kneeWarning, color = TacticalColors.TextSecondary, fontSize = 11.sp)
                    }
                    IconButton(onClick = { previewPoster = null }) {
                        Icon(Icons.Rounded.Close, "Close", tint = Color.White)
                    }
                }
            }
        }
    }
}
