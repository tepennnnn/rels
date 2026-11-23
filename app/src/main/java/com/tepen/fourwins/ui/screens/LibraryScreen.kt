package com.tepen.fourwins.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.OpenInBrowser
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import com.tepen.fourwins.ui.components.DarebeeVideoDetailDialog
import com.tepen.fourwins.ui.components.KneeSafetyPill
import com.tepen.fourwins.ui.theme.TacticalColors
import java.io.File

@Composable
fun LibraryScreen(
    vm: MainViewModel,
    onOpenPdf: (PdfLibraryItem) -> Unit
) {
    val context = LocalContext.current
    var mainMode by remember { mutableIntStateOf(0) } // 0 = Offline Vault, 1 = Discover Darebee
    var discoverSection by remember { mutableIntStateOf(0) } // 0 = Workouts & Challenges, 1 = Video Exercises

    val pdfs by vm.pdfLibrary.collectAsState()
    val savedPosters by vm.savedDarebee.collectAsState()
    val remoteItems by vm.remoteItems.collectAsState()
    val remoteLoading by vm.remoteLoading.collectAsState()
    val remoteError by vm.remoteError.collectAsState()
    val selectedFeed by vm.selectedFeed.collectAsState()

    val exerciseLibrary by vm.exerciseLibrary.collectAsState()
    val exerciseLibLoading by vm.exerciseLibraryLoading.collectAsState()
    val exerciseLibError by vm.exerciseLibraryError.collectAsState()
    val savedGuides by vm.savedExerciseGuides.collectAsState()
    val activeExerciseDetail by vm.activeExerciseDetail.collectAsState()
    val exerciseDetailLoading by vm.exerciseDetailLoading.collectAsState()

    var kneeSafeOnlyFilter by remember { mutableStateOf(true) }
    var onlinePage by remember { mutableIntStateOf(0) }
    var previewPoster by remember { mutableStateOf<SavedDarebeeWorkout?>(null) }
    var exerciseSearch by remember { mutableStateOf("") }
    var selectedFocusFilter by remember { mutableStateOf("all") }

    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) vm.importPdf(uri)
    }

    LaunchedEffect(mainMode, discoverSection) {
        if (mainMode == 1 && discoverSection == 0 && remoteItems.isEmpty() && !remoteLoading) {
            vm.loadDarebeeFeed(selectedFeed, 0)
        } else if (mainMode == 1 && discoverSection == 1 && exerciseLibrary.isEmpty() && !exerciseLibLoading) {
            vm.loadExerciseLibrary()
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
                Text("Darebee Hub", style = MaterialTheme.typography.headlineMedium, color = TacticalColors.TextPrimary)
                Text("Offline PDFs, saved video guides & live Darebee library", fontSize = 13.sp, color = TacticalColors.TextSecondary)
            }
        }

        // Spacious 2-Tab Mode Switcher
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(TacticalColors.CardDark)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Offline Vault", "Discover Darebee").forEachIndexed { idx, label ->
                    val active = mainMode == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (active) TacticalColors.MentalCyan else Color.Transparent)
                            .clickable { mainMode = idx }
                            .padding(vertical = 11.dp),
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

        if (mainMode == 0) {
            // Import PDF Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = TacticalColors.CardElevated
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Import Your Darebee PDFs", color = TacticalColors.MentalCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Add your downloaded PDFs (100 Ab Workouts, 100 Dumbbell Workouts, 100 Fitness Challenges, Fitness Over 60) from Downloads or Google Drive for offline viewing + timer.",
                            fontSize = 13.sp,
                            color = TacticalColors.TextSecondary
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { pdfPicker.launch(arrayOf("application/pdf")) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TacticalColors.MentalCyan,
                                contentColor = TacticalColors.AmoledBlack
                            )
                        ) {
                            Icon(Icons.Rounded.UploadFile, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Import PDF from Device", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (pdfs.isNotEmpty()) {
                item {
                    Text("Imported PDFs (${pdfs.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TacticalColors.TextPrimary)
                }
                items(pdfs, key = { it.id }) { pdf ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { onOpenPdf(pdf) },
                        shape = RoundedCornerShape(16.dp),
                        color = TacticalColors.CardDark
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(TacticalColors.PhysicalCrimson.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.PictureAsPdf, null, tint = TacticalColors.PhysicalCrimson)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(pdf.title, style = MaterialTheme.typography.titleMedium, color = TacticalColors.TextPrimary)
                                Text(
                                    text = "${pdf.pageCount} pages • Page ${pdf.lastPage + 1} • ${pdf.bookmarkedPages.size} bookmarks",
                                    fontSize = 12.sp,
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

            if (savedGuides.isNotEmpty()) {
                item {
                    Text("Saved Exercise Video Guides (${savedGuides.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TacticalColors.TextPrimary)
                }
                items(savedGuides, key = { "vault_${it.slug}" }) { guide ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { vm.openSavedExerciseGuide(guide) },
                        shape = RoundedCornerShape(16.dp),
                        color = TacticalColors.CardDark
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.PlayCircle, null, tint = TacticalColors.FinancialEmerald, modifier = Modifier.size(32.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(guide.title, style = MaterialTheme.typography.titleMedium, color = TacticalColors.TextPrimary)
                                Text("${guide.steps.size} execution steps • Offline ready", fontSize = 12.sp, color = TacticalColors.TextSecondary)
                            }
                            IconButton(onClick = { vm.deleteExerciseGuide(guide.slug) }) {
                                Icon(Icons.Rounded.DeleteOutline, "Remove", tint = TacticalColors.TextMuted)
                            }
                        }
                    }
                }
            }

            if (savedPosters.isNotEmpty()) {
                item {
                    Text("Saved Workout Posters (${savedPosters.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TacticalColors.TextPrimary)
                }
                items(savedPosters, key = { it.id }) { poster ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { previewPoster = poster },
                        shape = RoundedCornerShape(16.dp),
                        color = TacticalColors.CardDark
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = rememberAsyncImagePainter(File(poster.localImagePath)),
                                contentDescription = poster.title,
                                modifier = Modifier.size(56.dp, 76.dp).clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                KneeSafetyPill(isSafe = poster.kneeSafe)
                                Spacer(Modifier.height(4.dp))
                                Text(poster.title, style = MaterialTheme.typography.titleMedium, color = TacticalColors.TextPrimary)
                            }
                            IconButton(onClick = { vm.deleteSavedDarebee(poster) }) {
                                Icon(Icons.Rounded.DeleteOutline, "Remove", tint = TacticalColors.TextMuted)
                            }
                        }
                    }
                }
            }

            item {
                Text("Official Free Darebee Books", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TacticalColors.TextPrimary)
            }
            items(PreloadedCatalog.officialBooksDirectory) { book ->
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable {
                        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(book.pageUrl))) }
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = TacticalColors.CardDark
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(book.title, style = MaterialTheme.typography.titleMedium, color = TacticalColors.TextPrimary)
                            Text(book.subtitle, fontSize = 12.sp, color = TacticalColors.TextSecondary)
                            Spacer(Modifier.height(4.dp))
                            Text(book.kneeFriendlyTag, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TacticalColors.FinancialEmerald)
                        }
                        Icon(Icons.Rounded.OpenInBrowser, "Open", tint = TacticalColors.MentalCyan)
                    }
                }
            }
        } else {
            // Discover Darebee: Clean sub-selector (Workouts vs Video Exercises)
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = discoverSection == 0,
                        onClick = { discoverSection = 0 },
                        label = { Text("Workouts & Challenges", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TacticalColors.PhysicalCrimson,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = discoverSection == 1,
                        onClick = { discoverSection = 1 },
                        label = { Text("Video Exercise Library", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TacticalColors.PhysicalCrimson,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            if (discoverSection == 0) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DarebeeScraper.FeedType.entries.forEach { feed ->
                            FilterChip(
                                selected = selectedFeed == feed,
                                onClick = {
                                    onlinePage = 0
                                    vm.loadDarebeeFeed(feed, 0)
                                },
                                label = { Text(feed.label, fontSize = 12.sp) }
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
                            label = { Text("Knee-Safe Only", fontSize = 12.sp) },
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
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = TacticalColors.MentalCyan)
                        }
                    }
                }

                if (remoteError != null) {
                    item {
                        Surface(shape = RoundedCornerShape(14.dp), color = TacticalColors.CautionAmber.copy(alpha = 0.14f)) {
                            Text(remoteError!!, color = TacticalColors.CautionAmber, fontSize = 13.sp, modifier = Modifier.padding(14.dp))
                        }
                    }
                }

                val visibleRemote = if (kneeSafeOnlyFilter) remoteItems.filter { it.kneeSafeFlag } else remoteItems
                items(visibleRemote, key = { it.url }) { item ->
                    Surface(shape = RoundedCornerShape(16.dp), color = TacticalColors.CardDark) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (item.thumbUrl.isNotBlank()) {
                                Image(
                                    painter = rememberAsyncImagePainter(item.thumbUrl),
                                    contentDescription = item.title,
                                    modifier = Modifier.size(64.dp, 88.dp).clip(RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(Modifier.width(12.dp))
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                KneeSafetyPill(isSafe = item.kneeSafeFlag)
                                Spacer(Modifier.height(4.dp))
                                Text(item.title, style = MaterialTheme.typography.titleMedium, color = TacticalColors.TextPrimary)
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { vm.saveRemoteWorkoutOffline(item) },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = TacticalColors.MentalCyan,
                                            contentColor = TacticalColors.AmoledBlack
                                        )
                                    ) {
                                        Icon(Icons.Rounded.CloudDownload, null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Save Offline", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.url))) }
                                        },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Rounded.OpenInBrowser, null, modifier = Modifier.size(16.dp), tint = TacticalColors.TextSecondary)
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
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Load More from Darebee.com", color = TacticalColors.MentalCyan, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Video Exercise Library
                item {
                    OutlinedTextField(
                        value = exerciseSearch,
                        onValueChange = { exerciseSearch = it },
                        placeholder = { Text("Search exercise (e.g., plank, bridge, bird dog)") },
                        leadingIcon = { Icon(Icons.Rounded.Search, null, tint = TacticalColors.MentalCyan) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("all", "abs", "upper-body", "lower-body", "full-body", "stretching").forEach { focus ->
                            FilterChip(
                                selected = selectedFocusFilter == focus,
                                onClick = { selectedFocusFilter = focus },
                                label = { Text(focus.replaceFirstChar { it.uppercase() }, fontSize = 12.sp) }
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
                            label = { Text("Knee-Safe Only", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TacticalColors.KneeSafeGreen.copy(alpha = 0.22f),
                                selectedLabelColor = TacticalColors.KneeSafeGreen
                            )
                        )
                        IconButton(onClick = { vm.loadExerciseLibrary(forceRefresh = true) }) {
                            Icon(Icons.Rounded.Refresh, "Reload", tint = TacticalColors.MentalCyan)
                        }
                    }
                }

                if (exerciseLibLoading || exerciseDetailLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(28.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = TacticalColors.MentalCyan)
                        }
                    }
                }

                if (exerciseLibError != null) {
                    item {
                        Surface(shape = RoundedCornerShape(14.dp), color = TacticalColors.CautionAmber.copy(alpha = 0.14f)) {
                            Text(exerciseLibError!!, color = TacticalColors.CautionAmber, fontSize = 13.sp, modifier = Modifier.padding(14.dp))
                        }
                    }
                }

                val filteredExercises = exerciseLibrary.filter { ex ->
                    val matchesSearch = exerciseSearch.isBlank() || ex.title.contains(exerciseSearch, ignoreCase = true)
                    val matchesFocus = selectedFocusFilter == "all" || ex.focus.equals(selectedFocusFilter, ignoreCase = true)
                    val matchesKnee = !kneeSafeOnlyFilter || ex.kneeSafe
                    matchesSearch && matchesFocus && matchesKnee
                }.take(60)

                items(filteredExercises, key = { it.slug }) { ex ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { vm.openDarebeeExercise(ex) },
                        shape = RoundedCornerShape(16.dp),
                        color = TacticalColors.CardDark
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = rememberAsyncImagePainter(ex.thumbUrl),
                                contentDescription = ex.title,
                                modifier = Modifier.size(58.dp).clip(RoundedCornerShape(10.dp)).background(Color.White),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ex.title, style = MaterialTheme.typography.titleMedium, color = TacticalColors.TextPrimary)
                                Text(
                                    text = "${ex.focus.replaceFirstChar { it.uppercase() }} • ${ex.difficulty.replaceFirstChar { it.uppercase() }}",
                                    fontSize = 12.sp,
                                    color = TacticalColors.TextSecondary
                                )
                            }
                            Icon(Icons.Rounded.PlayCircle, "Watch How-To", tint = TacticalColors.PhysicalCrimson, modifier = Modifier.size(30.dp))
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }

    if (activeExerciseDetail != null) {
        val detail = activeExerciseDetail!!
        val isSaved = savedGuides.any { it.slug == detail.slug }
        DarebeeVideoDetailDialog(
            detail = detail,
            isSavedOffline = isSaved,
            onSaveOffline = { vm.saveExerciseGuideOffline(detail) },
            onSpeakSteps = { txt -> vm.coach.speak(txt, true) },
            onDismiss = { vm.closeDarebeeExercise() }
        )
    }

    if (previewPoster != null) {
        val poster = previewPoster!!
        Dialog(
            onDismissRequest = { previewPoster = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            var scale by remember { mutableFloatStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }
            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
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
                        Text(poster.kneeWarning, color = TacticalColors.TextSecondary, fontSize = 12.sp)
                    }
                    IconButton(onClick = { previewPoster = null }) {
                        Icon(Icons.Rounded.Close, "Close", tint = Color.White)
                    }
                }
            }
        }
    }
}
