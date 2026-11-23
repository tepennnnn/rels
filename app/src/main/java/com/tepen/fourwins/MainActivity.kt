package com.tepen.fourwins

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tepen.fourwins.model.PdfLibraryItem
import com.tepen.fourwins.model.WorkoutProgram
import com.tepen.fourwins.ui.screens.LibraryScreen
import com.tepen.fourwins.ui.screens.PdfViewerScreen
import com.tepen.fourwins.ui.screens.ProfileScreen
import com.tepen.fourwins.ui.screens.StatsScreen
import com.tepen.fourwins.ui.screens.TrainScreen
import com.tepen.fourwins.ui.screens.WinsScreen
import com.tepen.fourwins.ui.screens.WorkoutPlayerScreen
import com.tepen.fourwins.ui.theme.FourWinsTheme
import com.tepen.fourwins.ui.theme.TacticalColors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FourWinsTheme {
                FourWinsRoot()
            }
        }
    }
}

private data class NavTab(val label: String, val icon: ImageVector)

@Composable
private fun FourWinsRoot(vm: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val statusBanner by vm.statusBanner.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var activeTab by remember { mutableIntStateOf(0) }
    var activeWorkout by remember { mutableStateOf<WorkoutProgram?>(null) }
    var activePdf by remember { mutableStateOf<PdfLibraryItem?>(null) }

    val notifPermLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Permission result handled silently */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    LaunchedEffect(statusBanner) {
        val msg = statusBanner
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            vm.clearBanner()
        }
    }

    when {
        activeWorkout != null -> {
            BackHandler { activeWorkout = null }
            WorkoutPlayerScreen(
                vm = vm,
                program = activeWorkout!!,
                onClose = { activeWorkout = null }
            )
        }
        activePdf != null -> {
            BackHandler { activePdf = null }
            PdfViewerScreen(
                vm = vm,
                pdfItem = activePdf!!,
                onClose = { activePdf = null }
            )
        }
        else -> {
            val tabs = listOf(
                NavTab("Wins", Icons.Rounded.EmojiEvents),
                NavTab("Train", Icons.Rounded.FitnessCenter),
                NavTab("Darebee", Icons.AutoMirrored.Rounded.MenuBook),
                NavTab("Stats", Icons.Rounded.BarChart),
                NavTab("Profile", Icons.Rounded.Person)
            )
            Scaffold(
                containerColor = TacticalColors.AmoledBlack,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                bottomBar = {
                    NavigationBar(
                        containerColor = TacticalColors.SurfaceDark,
                        contentColor = TacticalColors.TextPrimary
                    ) {
                        tabs.forEachIndexed { idx, tab ->
                            val selected = activeTab == idx
                            NavigationBarItem(
                                selected = selected,
                                onClick = { activeTab = idx },
                                icon = { Icon(tab.icon, contentDescription = tab.label) },
                                label = {
                                    Text(
                                        text = tab.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = TacticalColors.PhysicalCrimson,
                                    selectedTextColor = TacticalColors.PhysicalCrimson,
                                    indicatorColor = TacticalColors.PhysicalCrimson.copy(alpha = 0.16f),
                                    unselectedIconColor = TacticalColors.TextMuted,
                                    unselectedTextColor = TacticalColors.TextMuted
                                )
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (activeTab) {
                        0 -> WinsScreen(vm = vm, onStartWorkout = { activeWorkout = it })
                        1 -> TrainScreen(vm = vm, onStartWorkout = { activeWorkout = it })
                        2 -> LibraryScreen(vm = vm, onOpenPdf = { activePdf = it })
                        3 -> StatsScreen(vm = vm)
                        4 -> ProfileScreen(vm = vm)
                    }
                }
            }
        }
    }
}
