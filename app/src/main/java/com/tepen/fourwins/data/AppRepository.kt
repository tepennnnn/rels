package com.tepen.fourwins.data

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import com.tepen.fourwins.domain.FitnessCalculations
import com.tepen.fourwins.model.Challenge30Day
import com.tepen.fourwins.model.DailyWinEntry
import com.tepen.fourwins.model.KneeComfort
import com.tepen.fourwins.model.PdfLibraryItem
import com.tepen.fourwins.model.SavedDarebeeWorkout
import com.tepen.fourwins.model.UserProfile
import com.tepen.fourwins.model.WeightEntry
import com.tepen.fourwins.model.WinHabit
import com.tepen.fourwins.model.WinPillar
import com.tepen.fourwins.model.WinStatsSummary
import com.tepen.fourwins.model.WorkoutSessionLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.util.UUID

class AppRepository(private val context: Context) {

    private val db = FourWinsDbHelper(context)

    private val _profile = MutableStateFlow(db.getProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private val _habits = MutableStateFlow(db.getHabits())
    val habits: StateFlow<List<WinHabit>> = _habits.asStateFlow()

    private val _dailyWins = MutableStateFlow(db.getAllDailyWins())
    val dailyWins: StateFlow<Map<String, DailyWinEntry>> = _dailyWins.asStateFlow()

    private val _stats = MutableStateFlow(computeStats())
    val stats: StateFlow<WinStatsSummary> = _stats.asStateFlow()

    private val _workoutLogs = MutableStateFlow(db.getWorkoutLogs())
    val workoutLogs: StateFlow<List<WorkoutSessionLog>> = _workoutLogs.asStateFlow()

    private val _challenges = MutableStateFlow(loadChallenges())
    val challenges: StateFlow<List<Challenge30Day>> = _challenges.asStateFlow()

    private val _pdfLibrary = MutableStateFlow(db.getPdfLibrary())
    val pdfLibrary: StateFlow<List<PdfLibraryItem>> = _pdfLibrary.asStateFlow()

    private val _savedDarebee = MutableStateFlow(db.getSavedDarebee())
    val savedDarebee: StateFlow<List<SavedDarebeeWorkout>> = _savedDarebee.asStateFlow()

    private val _weightLogs = MutableStateFlow(db.getWeightLogs())
    val weightLogs: StateFlow<List<WeightEntry>> = _weightLogs.asStateFlow()

    private fun computeStats(): WinStatsSummary {
        return FitnessCalculations.computeWinStats(
            today = LocalDate.now(),
            habits = _habits.value,
            entriesByDate = _dailyWins.value,
            rule = _profile.value.pillarWinRule
        )
    }

    private fun refreshStats() {
        _stats.value = computeStats()
    }

    private fun loadChallenges(): List<Challenge30Day> {
        val progress = db.getChallengeProgress()
        return PreloadedCatalog.challenges30Day.map { c ->
            c.copy(completedDays = progress[c.id] ?: emptySet())
        }
    }

    fun toggleHabitWin(habitId: String, dateIso: String = LocalDate.now().toString()) {
        val current = _dailyWins.value[dateIso] ?: DailyWinEntry(dateIso, emptySet())
        val updatedSet = current.completedHabitIds.toMutableSet()
        if (habitId in updatedSet) updatedSet.remove(habitId) else updatedSet.add(habitId)
        val next = current.copy(completedHabitIds = updatedSet)
        db.upsertDailyWin(next)
        _dailyWins.value = _dailyWins.value.toMutableMap().apply { put(dateIso, next) }
        refreshStats()
    }

    fun markHabitWinDone(habitId: String, dateIso: String = LocalDate.now().toString()) {
        val current = _dailyWins.value[dateIso] ?: DailyWinEntry(dateIso, emptySet())
        if (habitId in current.completedHabitIds) return
        val next = current.copy(completedHabitIds = current.completedHabitIds + habitId)
        db.upsertDailyWin(next)
        _dailyWins.value = _dailyWins.value.toMutableMap().apply { put(dateIso, next) }
        refreshStats()
    }

    fun saveVictoryNote(note: String, dateIso: String = LocalDate.now().toString()) {
        val current = _dailyWins.value[dateIso] ?: DailyWinEntry(dateIso, emptySet())
        val next = current.copy(victoryNote = note)
        db.upsertDailyWin(next)
        _dailyWins.value = _dailyWins.value.toMutableMap().apply { put(dateIso, next) }
    }

    fun addOrUpdateHabit(pillar: WinPillar, title: String, existingId: String? = null) {
        val clean = title.trim()
        if (clean.isEmpty()) return
        val id = existingId ?: "${pillar.tag.lowercase()}_${UUID.randomUUID().toString().take(8)}"
        val order = _habits.value.filter { it.pillar == pillar }.maxOfOrNull { it.sortOrder }?.plus(1) ?: 1
        val existing = _habits.value.find { it.id == id }
        val habit = existing?.copy(title = clean, isActive = true)
            ?: WinHabit(id = id, pillar = pillar, title = clean, sortOrder = order, isActive = true)
        db.upsertHabit(habit)
        _habits.value = db.getHabits()
        refreshStats()
    }

    fun deleteHabit(habitId: String) {
        db.deleteHabit(habitId)
        _habits.value = db.getHabits()
        refreshStats()
    }

    fun updateProfile(profile: UserProfile) {
        db.saveProfile(profile)
        _profile.value = profile
        refreshStats()
    }

    fun logWorkoutCompletion(log: WorkoutSessionLog) {
        db.insertWorkoutLog(log)
        _workoutLogs.value = db.getWorkoutLogs()
        // Auto-mark Physical Gym Win on today's 4 Daily Wins board
        val gymHabit = _habits.value.firstOrNull { it.pillar == WinPillar.PHYSICAL && it.id == "phy_gym" }
            ?: _habits.value.firstOrNull { it.pillar == WinPillar.PHYSICAL }
        if (gymHabit != null) {
            markHabitWinDone(gymHabit.id, log.dateIso)
        }
        // Also store today's knee comfort check-in
        val current = _dailyWins.value[log.dateIso] ?: DailyWinEntry(log.dateIso, emptySet())
        val next = current.copy(kneeComfort = log.kneeComfort)
        db.upsertDailyWin(next)
        _dailyWins.value = _dailyWins.value.toMutableMap().apply { put(log.dateIso, next) }
    }

    fun toggleChallengeDay(challengeId: String, dayNumber: Int) {
        val currentList = _challenges.value
        val target = currentList.find { it.id == challengeId } ?: return
        val updatedDays = target.completedDays.toMutableSet()
        if (dayNumber in updatedDays) updatedDays.remove(dayNumber) else updatedDays.add(dayNumber)
        db.saveChallengeProgress(challengeId, updatedDays)
        _challenges.value = loadChallenges()

        // If completing a day, also credit the linked pillar's habit if applicable
        if (dayNumber in updatedDays) {
            when (target.id) {
                "ch_meditation_1min" -> markHabitWinDone("spr_meditate")
                "ch_walk_streak" -> markHabitWinDone("phy_walk")
                "ch_gratitude_win" -> markHabitWinDone("spr_gratitude")
            }
        }
    }

    suspend fun importPdfFromUri(uri: Uri): Result<PdfLibraryItem> = withContext(Dispatchers.IO) {
        runCatching {
            val pdfDir = File(context.filesDir, "pdfs").apply { mkdirs() }
            val displayName = resolveDisplayName(uri) ?: "Darebee_Workout_${System.currentTimeMillis()}.pdf"
            val cleanTitle = displayName.removeSuffix(".pdf").removeSuffix(".PDF")
                .replace("-", " ")
                .replace("_", " ")
            val targetFile = File(pdfDir, "${UUID.randomUUID().toString().take(8)}_${displayName.replace(Regex("[^a-zA-Z0-9._-]"), "_")}")

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            } ?: error("Could not read selected PDF file.")

            val pages = countPdfPages(targetFile)
            require(pages > 0) { "Selected file is not a valid readable PDF." }

            val item = PdfLibraryItem(
                id = UUID.randomUUID().toString().take(12),
                title = cleanTitle,
                filePath = targetFile.absolutePath,
                pageCount = pages
            )
            db.upsertPdfItem(item)
            _pdfLibrary.value = db.getPdfLibrary()
            item
        }
    }

    fun updatePdfProgress(item: PdfLibraryItem) {
        db.upsertPdfItem(item)
        _pdfLibrary.value = db.getPdfLibrary()
    }

    fun deletePdf(item: PdfLibraryItem) {
        runCatching { File(item.filePath).delete() }
        db.deletePdfItem(item.id)
        _pdfLibrary.value = db.getPdfLibrary()
    }

    fun saveOfflineDarebeeWorkout(item: SavedDarebeeWorkout) {
        db.upsertSavedDarebee(item)
        _savedDarebee.value = db.getSavedDarebee()
    }

    fun deleteSavedDarebee(item: SavedDarebeeWorkout) {
        runCatching { File(item.localImagePath).delete() }
        db.deleteSavedDarebee(item.id)
        _savedDarebee.value = db.getSavedDarebee()
    }

    fun logWeight(weightKg: Float, waistCm: Float? = null, dateIso: String = LocalDate.now().toString()) {
        val entry = WeightEntry(dateIso, weightKg, waistCm)
        db.upsertWeightLog(entry)
        _weightLogs.value = db.getWeightLogs()
        // Update profile current weight too
        updateProfile(_profile.value.copy(weightKg = weightKg))
    }

    fun exportBackupJson(): String {
        val root = JSONObject()
        root.put("exportedAt", LocalDate.now().toString())
        val p = _profile.value
        root.put("profile", JSONObject().apply {
            put("name", p.name)
            put("age", p.age)
            put("heightCm", p.heightCm.toDouble())
            put("weightKg", p.weightKg.toDouble())
            put("targetWeightKg", p.targetWeightKg.toDouble())
            put("kneeSafeMode", p.kneeSafeMode)
        })
        val winsArr = JSONArray()
        _dailyWins.value.values.forEach { entry ->
            winsArr.put(JSONObject().apply {
                put("dateIso", entry.dateIso)
                put("completedIds", JSONArray(entry.completedHabitIds.toList()))
                put("victoryNote", entry.victoryNote)
                put("kneeComfort", entry.kneeComfort?.name ?: "")
            })
        }
        root.put("dailyWins", winsArr)
        val weightArr = JSONArray()
        _weightLogs.value.forEach { w ->
            weightArr.put(JSONObject().apply {
                put("dateIso", w.dateIso)
                put("weightKg", w.weightKg.toDouble())
                if (w.waistCm != null) put("waistCm", w.waistCm.toDouble())
            })
        }
        root.put("weightLogs", weightArr)
        return root.toString(2)
    }

    fun importBackupJson(jsonText: String): Result<Int> = runCatching {
        val root = JSONObject(jsonText)
        var restored = 0
        val winsArr = root.optJSONArray("dailyWins") ?: JSONArray()
        for (i in 0 until winsArr.length()) {
            val obj = winsArr.getJSONObject(i)
            val dateIso = obj.getString("dateIso")
            val arr = obj.optJSONArray("completedIds") ?: JSONArray()
            val set = (0 until arr.length()).map { arr.getString(it) }.toSet()
            val note = obj.optString("victoryNote", "")
            val knee = obj.optString("kneeComfort", "").takeIf { it.isNotEmpty() }
                ?.let { runCatching { KneeComfort.valueOf(it) }.getOrNull() }
            db.upsertDailyWin(DailyWinEntry(dateIso, set, note, knee))
            restored++
        }
        val weightArr = root.optJSONArray("weightLogs") ?: JSONArray()
        for (i in 0 until weightArr.length()) {
            val obj = weightArr.getJSONObject(i)
            val waist = if (obj.has("waistCm")) obj.getDouble("waistCm").toFloat() else null
            db.upsertWeightLog(
                WeightEntry(
                    dateIso = obj.getString("dateIso"),
                    weightKg = obj.getDouble("weightKg").toFloat(),
                    waistCm = waist
                )
            )
        }
        _dailyWins.value = db.getAllDailyWins()
        _weightLogs.value = db.getWeightLogs()
        refreshStats()
        restored
    }

    private fun resolveDisplayName(uri: Uri): String? {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
            if (c.moveToFirst()) return c.getString(0)
        }
        return uri.lastPathSegment
    }

    private fun countPdfPages(file: File): Int {
        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
            PdfRenderer(pfd).use { renderer ->
                renderer.pageCount
            }
        }
    }
}
