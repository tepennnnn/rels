package com.tepen.fourwins

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tepen.fourwins.audio.WorkoutCoach
import com.tepen.fourwins.data.PreloadedCatalog
import com.tepen.fourwins.domain.FitnessCalculations
import com.tepen.fourwins.model.DarebeeExerciseDetail
import com.tepen.fourwins.model.DarebeeExerciseLibraryItem
import com.tepen.fourwins.model.DarebeeRemoteItem
import com.tepen.fourwins.model.KneeComfort
import com.tepen.fourwins.model.PdfLibraryItem
import com.tepen.fourwins.model.SavedDarebeeWorkout
import com.tepen.fourwins.model.UserProfile
import com.tepen.fourwins.model.WinPillar
import com.tepen.fourwins.model.WorkoutProgram
import com.tepen.fourwins.model.WorkoutSessionLog
import com.tepen.fourwins.network.DarebeeScraper
import com.tepen.fourwins.notify.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as FourWinsApp
    val repo = app.repository
    val coach = WorkoutCoach(application)

    val profile = repo.profile
    val habits = repo.habits
    val dailyWins = repo.dailyWins
    val stats = repo.stats
    val workoutLogs = repo.workoutLogs
    val challenges = repo.challenges
    val pdfLibrary = repo.pdfLibrary
    val savedDarebee = repo.savedDarebee
    val weightLogs = repo.weightLogs
    val savedExerciseGuides = repo.savedExerciseGuides

    private val _exerciseLibrary = MutableStateFlow<List<DarebeeExerciseLibraryItem>>(emptyList())
    val exerciseLibrary: StateFlow<List<DarebeeExerciseLibraryItem>> = _exerciseLibrary.asStateFlow()

    private val _exerciseLibraryLoading = MutableStateFlow(false)
    val exerciseLibraryLoading: StateFlow<Boolean> = _exerciseLibraryLoading.asStateFlow()

    private val _exerciseLibraryError = MutableStateFlow<String?>(null)
    val exerciseLibraryError: StateFlow<String?> = _exerciseLibraryError.asStateFlow()

    private val _activeExerciseDetail = MutableStateFlow<DarebeeExerciseDetail?>(null)
    val activeExerciseDetail: StateFlow<DarebeeExerciseDetail?> = _activeExerciseDetail.asStateFlow()

    private val _exerciseDetailLoading = MutableStateFlow(false)
    val exerciseDetailLoading: StateFlow<Boolean> = _exerciseDetailLoading.asStateFlow()

    val recompMetrics = profile.map { FitnessCalculations.computeRecompMetrics(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, FitnessCalculations.computeRecompMetrics(profile.value))

    private val _selectedDate = MutableStateFlow(LocalDate.now().toString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Online Darebee feed state
    private val _selectedFeed = MutableStateFlow(DarebeeScraper.FeedType.WORKOUTS)
    val selectedFeed: StateFlow<DarebeeScraper.FeedType> = _selectedFeed.asStateFlow()

    private val _remoteItems = MutableStateFlow<List<DarebeeRemoteItem>>(emptyList())
    val remoteItems: StateFlow<List<DarebeeRemoteItem>> = _remoteItems.asStateFlow()

    private val _remoteLoading = MutableStateFlow(false)
    val remoteLoading: StateFlow<Boolean> = _remoteLoading.asStateFlow()

    private val _remoteError = MutableStateFlow<String?>(null)
    val remoteError: StateFlow<String?> = _remoteError.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    fun clearBanner() {
        _statusBanner.value = null
    }

    fun selectDate(dateIso: String) {
        _selectedDate.value = dateIso
    }

    fun toggleHabit(habitId: String) {
        val prevPillars = stats.value.todayPillarsWon
        repo.toggleHabitWin(habitId, _selectedDate.value)
        val p = profile.value
        coach.vibrateTick(p.hapticsEnabled)
        val newPillars = stats.value.todayPillarsWon
        if (_selectedDate.value == LocalDate.now().toString() && newPillars == 4 && prevPillars < 4) {
            coach.beepVictory(p.beepEnabled)
            coach.vibrateVictory(p.hapticsEnabled)
            coach.speak("4 out of 4 daily wins locked in!", p.voiceCoachEnabled)
        } else {
            coach.beepStartOrFinish(p.beepEnabled)
        }
    }

    fun saveVictoryNote(note: String) {
        repo.saveVictoryNote(note, _selectedDate.value)
        _statusBanner.value = "Daily victory note saved."
    }

    fun addOrUpdateHabit(pillar: WinPillar, title: String, existingId: String? = null) {
        repo.addOrUpdateHabit(pillar, title, existingId)
    }

    fun deleteHabit(habitId: String) {
        repo.deleteHabit(habitId)
    }

    fun updateProfile(updated: UserProfile) {
        repo.updateProfile(updated)
        ReminderScheduler.syncReminders(getApplication(), updated)
        _statusBanner.value = "Profile & reminders updated."
    }

    fun completeWorkout(
        program: WorkoutProgram,
        durationMinutes: Int,
        setsCompleted: Int,
        kneeComfort: KneeComfort,
        notes: String
    ) {
        repo.logWorkoutCompletion(
            WorkoutSessionLog(
                dateIso = LocalDate.now().toString(),
                programId = program.id,
                programTitle = program.title,
                durationMinutes = durationMinutes.coerceAtLeast(1),
                setsCompleted = setsCompleted,
                kneeComfort = kneeComfort,
                notes = notes
            )
        )
        val p = profile.value
        coach.beepVictory(p.beepEnabled)
        coach.vibrateVictory(p.hapticsEnabled)
        coach.speak("Workout complete! Physical win recorded.", p.voiceCoachEnabled)
        _statusBanner.value = "Session logged + Physical Win credited!"
    }

    fun toggleChallengeDay(challengeId: String, dayNumber: Int) {
        repo.toggleChallengeDay(challengeId, dayNumber)
        coach.vibrateTick(profile.value.hapticsEnabled)
    }

    fun importPdf(uri: Uri, onImported: (PdfLibraryItem) -> Unit = {}) {
        viewModelScope.launch {
            _statusBanner.value = "Importing PDF into offline library..."
            repo.importPdfFromUri(uri)
                .onSuccess { item ->
                    _statusBanner.value = "Imported '${item.title}' (${item.pageCount} pages)"
                    onImported(item)
                }
                .onFailure { err ->
                    _statusBanner.value = "Import failed: ${err.message}"
                }
        }
    }

    fun updatePdfProgress(item: PdfLibraryItem) = repo.updatePdfProgress(item)

    fun deletePdf(item: PdfLibraryItem) {
        repo.deletePdf(item)
        _statusBanner.value = "Removed PDF from offline library."
    }

    fun loadDarebeeFeed(feedType: DarebeeScraper.FeedType = _selectedFeed.value, page: Int = 0) {
        _selectedFeed.value = feedType
        viewModelScope.launch {
            _remoteLoading.value = true
            _remoteError.value = null
            DarebeeScraper.fetchFeed(feedType, page)
                .onSuccess { list ->
                    _remoteItems.value = if (page == 0) list else (_remoteItems.value + list).distinctBy { it.url }
                }
                .onFailure { err ->
                    _remoteError.value = "Offline or Darebee unreachable (${err.message}). Your offline library & workouts still work 100%."
                }
            _remoteLoading.value = false
        }
    }

    fun saveRemoteWorkoutOffline(item: DarebeeRemoteItem) {
        viewModelScope.launch {
            _statusBanner.value = "Downloading '${item.title}' for offline use..."
            val detail = DarebeeScraper.fetchWorkoutDetail(item).getOrDefault(item)
            DarebeeScraper.downloadWorkoutOffline(getApplication(), detail)
                .onSuccess { saved ->
                    repo.saveOfflineDarebeeWorkout(saved)
                    _statusBanner.value = "Saved '${saved.title}' to Offline Posters!"
                }
                .onFailure { err ->
                    _statusBanner.value = "Download failed: ${err.message}"
                }
        }
    }

    fun deleteSavedDarebee(item: SavedDarebeeWorkout) {
        repo.deleteSavedDarebee(item)
    }

    fun loadExerciseLibrary(forceRefresh: Boolean = false) {
        if (!forceRefresh && _exerciseLibrary.value.isNotEmpty()) return
        viewModelScope.launch {
            _exerciseLibraryLoading.value = true
            _exerciseLibraryError.value = null
            DarebeeScraper.fetchExerciseLibrary()
                .onSuccess { _exerciseLibrary.value = it }
                .onFailure { err ->
                    _exerciseLibraryError.value = "Offline or Darebee unreachable (${err.message}). Your saved form guides & animated demos work offline."
                }
            _exerciseLibraryLoading.value = false
        }
    }

    fun openDarebeeExercise(item: DarebeeExerciseLibraryItem) {
        // Check offline saved guide first so it loads instantaneously even offline!
        val offlineMatch = savedExerciseGuides.value.find { it.slug == item.slug }
        if (offlineMatch != null) {
            _activeExerciseDetail.value = offlineMatch
            return
        }
        viewModelScope.launch {
            _exerciseDetailLoading.value = true
            DarebeeScraper.fetchExerciseDetail(item)
                .onSuccess { _activeExerciseDetail.value = it }
                .onFailure { err ->
                    _statusBanner.value = "Could not load video guide (${err.message})."
                }
            _exerciseDetailLoading.value = false
        }
    }

    fun openSavedExerciseGuide(detail: DarebeeExerciseDetail) {
        _activeExerciseDetail.value = detail
    }

    fun closeDarebeeExercise() {
        _activeExerciseDetail.value = null
    }

    fun saveExerciseGuideOffline(detail: DarebeeExerciseDetail) {
        repo.saveExerciseGuide(detail)
        _statusBanner.value = "Saved '${detail.title}' step-by-step guide offline!"
    }

    fun deleteExerciseGuide(slug: String) {
        repo.deleteExerciseGuide(slug)
        _statusBanner.value = "Removed saved guide."
    }

    fun logWeight(weightKg: Float, waistCm: Float? = null) {
        repo.logWeight(weightKg, waistCm)
        _statusBanner.value = "Logged ${String.format("%.1f", weightKg)} kg."
    }

    fun recommendedTodayProgram(): WorkoutProgram {
        val lastLog = workoutLogs.value.firstOrNull()
        val lastDay = PreloadedCatalog.tailoredPrograms.find { it.id == lastLog?.programId }?.dayNumber ?: 0
        val nextDay = (lastDay % 6) + 1
        // If yesterday/last workout had SORE knee comfort, recommend Day 1 (Upper Push) or Day 6 (Active Recovery)
        if (lastLog?.kneeComfort == KneeComfort.SORE && nextDay == 4) {
            return PreloadedCatalog.tailoredPrograms.first { it.dayNumber == 1 }
        }
        return PreloadedCatalog.tailoredPrograms.find { it.dayNumber == nextDay }
            ?: PreloadedCatalog.tailoredPrograms.first()
    }

    override fun onCleared() {
        super.onCleared()
        coach.release()
    }
}
