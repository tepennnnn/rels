package com.tepen.fourwins.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.tepen.fourwins.model.DailyWinEntry
import com.tepen.fourwins.model.DarebeeExerciseDetail
import com.tepen.fourwins.model.Equipment
import com.tepen.fourwins.model.KneeComfort
import com.tepen.fourwins.model.PdfLibraryItem
import com.tepen.fourwins.model.PillarWinRule
import com.tepen.fourwins.model.SavedDarebeeWorkout
import com.tepen.fourwins.model.UserProfile
import com.tepen.fourwins.model.WeightEntry
import com.tepen.fourwins.model.WinHabit
import com.tepen.fourwins.model.WinPillar
import com.tepen.fourwins.model.WorkoutSessionLog
import java.time.LocalDate

class FourWinsDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE habits (
                id TEXT PRIMARY KEY,
                pillar TEXT NOT NULL,
                title TEXT NOT NULL,
                sort_order INTEGER NOT NULL,
                is_active INTEGER NOT NULL DEFAULT 1
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE daily_wins (
                date_iso TEXT PRIMARY KEY,
                completed_ids TEXT NOT NULL DEFAULT '',
                victory_note TEXT NOT NULL DEFAULT '',
                knee_comfort TEXT
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE user_profile (
                id INTEGER PRIMARY KEY CHECK (id = 1),
                name TEXT NOT NULL,
                age INTEGER NOT NULL,
                height_cm REAL NOT NULL,
                weight_kg REAL NOT NULL,
                target_weight_kg REAL NOT NULL,
                is_male INTEGER NOT NULL,
                knee_safe_mode INTEGER NOT NULL,
                pillar_win_rule TEXT NOT NULL,
                voice_enabled INTEGER NOT NULL,
                beep_enabled INTEGER NOT NULL,
                haptics_enabled INTEGER NOT NULL,
                default_rest_sec INTEGER NOT NULL,
                morning_enabled INTEGER NOT NULL,
                morning_hour INTEGER NOT NULL,
                morning_min INTEGER NOT NULL,
                evening_enabled INTEGER NOT NULL,
                evening_hour INTEGER NOT NULL,
                evening_min INTEGER NOT NULL,
                equipment_csv TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE workout_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date_iso TEXT NOT NULL,
                program_id TEXT NOT NULL,
                program_title TEXT NOT NULL,
                duration_min INTEGER NOT NULL,
                sets_completed INTEGER NOT NULL,
                knee_comfort TEXT NOT NULL,
                notes TEXT NOT NULL DEFAULT ''
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE challenge_progress (
                challenge_id TEXT PRIMARY KEY,
                completed_days_csv TEXT NOT NULL DEFAULT ''
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE pdf_library (
                id TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                file_path TEXT NOT NULL,
                page_count INTEGER NOT NULL,
                last_page INTEGER NOT NULL DEFAULT 0,
                bookmarks_csv TEXT NOT NULL DEFAULT '',
                added_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE saved_darebee (
                id TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                url TEXT NOT NULL,
                local_image_path TEXT NOT NULL,
                category TEXT NOT NULL,
                focus TEXT NOT NULL,
                difficulty TEXT NOT NULL,
                description TEXT NOT NULL,
                knee_safe INTEGER NOT NULL,
                knee_warning TEXT NOT NULL,
                saved_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE weight_logs (
                date_iso TEXT PRIMARY KEY,
                weight_kg REAL NOT NULL,
                waist_cm REAL
            )
            """.trimIndent()
        )
        createExerciseGuidesTable(db)

        seedDefaults(db)
    }

    private fun createExerciseGuidesTable(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS saved_exercise_guides (
                slug TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                url TEXT NOT NULL,
                thumb_url TEXT NOT NULL,
                youtube_embed TEXT NOT NULL,
                youtube_watch TEXT NOT NULL,
                instructions TEXT NOT NULL,
                steps_sep TEXT NOT NULL,
                works_muscles TEXT NOT NULL,
                breathing TEXT NOT NULL,
                common_mistakes TEXT NOT NULL,
                make_easier TEXT NOT NULL,
                make_harder TEXT NOT NULL,
                knee_safe INTEGER NOT NULL,
                knee_warning TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            createExerciseGuidesTable(db)
        }
    }

    private fun seedDefaults(db: SQLiteDatabase) {
        PreloadedCatalog.defaultHabits.forEach { habit ->
            val cv = ContentValues().apply {
                put("id", habit.id)
                put("pillar", habit.pillar.name)
                put("title", habit.title)
                put("sort_order", habit.sortOrder)
                put("is_active", if (habit.isActive) 1 else 0)
            }
            db.insert("habits", null, cv)
        }

        val p = UserProfile()
        db.insert("user_profile", null, profileToCv(p))

        // Seed starting weight entry (75 kg)
        val wcv = ContentValues().apply {
            put("date_iso", LocalDate.now().toString())
            put("weight_kg", 75.0f)
        }
        db.insert("weight_logs", null, wcv)
    }

    fun getHabits(): List<WinHabit> {
        val list = mutableListOf<WinHabit>()
        readableDatabase.rawQuery(
            "SELECT id, pillar, title, sort_order, is_active FROM habits ORDER BY pillar, sort_order ASC",
            null
        ).use { c ->
            while (c.moveToNext()) {
                val pillar = runCatching { WinPillar.valueOf(c.getString(1)) }.getOrNull() ?: continue
                list.add(
                    WinHabit(
                        id = c.getString(0),
                        pillar = pillar,
                        title = c.getString(2),
                        sortOrder = c.getInt(3),
                        isActive = c.getInt(4) == 1
                    )
                )
            }
        }
        return list
    }

    fun upsertHabit(habit: WinHabit) {
        val cv = ContentValues().apply {
            put("id", habit.id)
            put("pillar", habit.pillar.name)
            put("title", habit.title)
            put("sort_order", habit.sortOrder)
            put("is_active", if (habit.isActive) 1 else 0)
        }
        writableDatabase.insertWithOnConflict("habits", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun deleteHabit(id: String) {
        writableDatabase.delete("habits", "id = ?", arrayOf(id))
    }

    fun getAllDailyWins(): Map<String, DailyWinEntry> {
        val map = mutableMapOf<String, DailyWinEntry>()
        readableDatabase.rawQuery(
            "SELECT date_iso, completed_ids, victory_note, knee_comfort FROM daily_wins",
            null
        ).use { c ->
            while (c.moveToNext()) {
                val dateIso = c.getString(0)
                val ids = c.getString(1)
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .toSet()
                val note = c.getString(2) ?: ""
                val kneeStr = c.getString(3)
                val knee = kneeStr?.let { runCatching { KneeComfort.valueOf(it) }.getOrNull() }
                map[dateIso] = DailyWinEntry(dateIso, ids, note, knee)
            }
        }
        return map
    }

    fun upsertDailyWin(entry: DailyWinEntry) {
        val cv = ContentValues().apply {
            put("date_iso", entry.dateIso)
            put("completed_ids", entry.completedHabitIds.joinToString(","))
            put("victory_note", entry.victoryNote)
            if (entry.kneeComfort != null) put("knee_comfort", entry.kneeComfort.name)
            else putNull("knee_comfort")
        }
        writableDatabase.insertWithOnConflict("daily_wins", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getProfile(): UserProfile {
        readableDatabase.rawQuery("SELECT * FROM user_profile WHERE id = 1", null).use { c ->
            if (c.moveToFirst()) {
                val eqCsv = c.getString(c.getColumnIndexOrThrow("equipment_csv")) ?: ""
                val eqSet = eqCsv.split(",")
                    .mapNotNull { name -> runCatching { Equipment.valueOf(name.trim()) }.getOrNull() }
                    .toSet()
                    .ifEmpty { Equipment.entries.toSet() }
                val ruleName = c.getString(c.getColumnIndexOrThrow("pillar_win_rule"))
                val rule = runCatching { PillarWinRule.valueOf(ruleName) }.getOrDefault(PillarWinRule.AT_LEAST_ONE)
                return UserProfile(
                    name = c.getString(c.getColumnIndexOrThrow("name")),
                    age = c.getInt(c.getColumnIndexOrThrow("age")),
                    heightCm = c.getFloat(c.getColumnIndexOrThrow("height_cm")),
                    weightKg = c.getFloat(c.getColumnIndexOrThrow("weight_kg")),
                    targetWeightKg = c.getFloat(c.getColumnIndexOrThrow("target_weight_kg")),
                    isMale = c.getInt(c.getColumnIndexOrThrow("is_male")) == 1,
                    kneeSafeMode = c.getInt(c.getColumnIndexOrThrow("knee_safe_mode")) == 1,
                    pillarWinRule = rule,
                    voiceCoachEnabled = c.getInt(c.getColumnIndexOrThrow("voice_enabled")) == 1,
                    beepEnabled = c.getInt(c.getColumnIndexOrThrow("beep_enabled")) == 1,
                    hapticsEnabled = c.getInt(c.getColumnIndexOrThrow("haptics_enabled")) == 1,
                    defaultRestSeconds = c.getInt(c.getColumnIndexOrThrow("default_rest_sec")),
                    morningReminderEnabled = c.getInt(c.getColumnIndexOrThrow("morning_enabled")) == 1,
                    morningHour = c.getInt(c.getColumnIndexOrThrow("morning_hour")),
                    morningMinute = c.getInt(c.getColumnIndexOrThrow("morning_min")),
                    eveningReminderEnabled = c.getInt(c.getColumnIndexOrThrow("evening_enabled")) == 1,
                    eveningHour = c.getInt(c.getColumnIndexOrThrow("evening_hour")),
                    eveningMinute = c.getInt(c.getColumnIndexOrThrow("evening_min")),
                    availableEquipment = eqSet
                )
            }
        }
        return UserProfile()
    }

    fun saveProfile(profile: UserProfile) {
        writableDatabase.insertWithOnConflict(
            "user_profile",
            null,
            profileToCv(profile),
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    private fun profileToCv(p: UserProfile): ContentValues = ContentValues().apply {
        put("id", 1)
        put("name", p.name)
        put("age", p.age)
        put("height_cm", p.heightCm)
        put("weight_kg", p.weightKg)
        put("target_weight_kg", p.targetWeightKg)
        put("is_male", if (p.isMale) 1 else 0)
        put("knee_safe_mode", if (p.kneeSafeMode) 1 else 0)
        put("pillar_win_rule", p.pillarWinRule.name)
        put("voice_enabled", if (p.voiceCoachEnabled) 1 else 0)
        put("beep_enabled", if (p.beepEnabled) 1 else 0)
        put("haptics_enabled", if (p.hapticsEnabled) 1 else 0)
        put("default_rest_sec", p.defaultRestSeconds)
        put("morning_enabled", if (p.morningReminderEnabled) 1 else 0)
        put("morning_hour", p.morningHour)
        put("morning_min", p.morningMinute)
        put("evening_enabled", if (p.eveningReminderEnabled) 1 else 0)
        put("evening_hour", p.eveningHour)
        put("evening_min", p.eveningMinute)
        put("equipment_csv", p.availableEquipment.joinToString(",") { it.name })
    }

    fun insertWorkoutLog(log: WorkoutSessionLog): Long {
        val cv = ContentValues().apply {
            put("date_iso", log.dateIso)
            put("program_id", log.programId)
            put("program_title", log.programTitle)
            put("duration_min", log.durationMinutes)
            put("sets_completed", log.setsCompleted)
            put("knee_comfort", log.kneeComfort.name)
            put("notes", log.notes)
        }
        return writableDatabase.insert("workout_logs", null, cv)
    }

    fun getWorkoutLogs(): List<WorkoutSessionLog> {
        val list = mutableListOf<WorkoutSessionLog>()
        readableDatabase.rawQuery(
            "SELECT id, date_iso, program_id, program_title, duration_min, sets_completed, knee_comfort, notes FROM workout_logs ORDER BY id DESC",
            null
        ).use { c ->
            while (c.moveToNext()) {
                val knee = runCatching { KneeComfort.valueOf(c.getString(6)) }.getOrDefault(KneeComfort.GREAT)
                list.add(
                    WorkoutSessionLog(
                        id = c.getLong(0),
                        dateIso = c.getString(1),
                        programId = c.getString(2),
                        programTitle = c.getString(3),
                        durationMinutes = c.getInt(4),
                        setsCompleted = c.getInt(5),
                        kneeComfort = knee,
                        notes = c.getString(7) ?: ""
                    )
                )
            }
        }
        return list
    }

    fun getChallengeProgress(): Map<String, Set<Int>> {
        val map = mutableMapOf<String, Set<Int>>()
        readableDatabase.rawQuery("SELECT challenge_id, completed_days_csv FROM challenge_progress", null).use { c ->
            while (c.moveToNext()) {
                val id = c.getString(0)
                val days = c.getString(1)
                    .split(",")
                    .mapNotNull { it.trim().toIntOrNull() }
                    .toSet()
                map[id] = days
            }
        }
        return map
    }

    fun saveChallengeProgress(challengeId: String, days: Set<Int>) {
        val cv = ContentValues().apply {
            put("challenge_id", challengeId)
            put("completed_days_csv", days.sorted().joinToString(","))
        }
        writableDatabase.insertWithOnConflict("challenge_progress", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getPdfLibrary(): List<PdfLibraryItem> {
        val list = mutableListOf<PdfLibraryItem>()
        readableDatabase.rawQuery(
            "SELECT id, title, file_path, page_count, last_page, bookmarks_csv, added_at FROM pdf_library ORDER BY added_at DESC",
            null
        ).use { c ->
            while (c.moveToNext()) {
                val bookmarks = c.getString(5)
                    .split(",")
                    .mapNotNull { it.trim().toIntOrNull() }
                    .toSet()
                list.add(
                    PdfLibraryItem(
                        id = c.getString(0),
                        title = c.getString(1),
                        filePath = c.getString(2),
                        pageCount = c.getInt(3),
                        lastPage = c.getInt(4),
                        bookmarkedPages = bookmarks,
                        addedAtMillis = c.getLong(6)
                    )
                )
            }
        }
        return list
    }

    fun upsertPdfItem(item: PdfLibraryItem) {
        val cv = ContentValues().apply {
            put("id", item.id)
            put("title", item.title)
            put("file_path", item.filePath)
            put("page_count", item.pageCount)
            put("last_page", item.lastPage)
            put("bookmarks_csv", item.bookmarkedPages.sorted().joinToString(","))
            put("added_at", item.addedAtMillis)
        }
        writableDatabase.insertWithOnConflict("pdf_library", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun deletePdfItem(id: String) {
        writableDatabase.delete("pdf_library", "id = ?", arrayOf(id))
    }

    fun getSavedDarebee(): List<SavedDarebeeWorkout> {
        val list = mutableListOf<SavedDarebeeWorkout>()
        readableDatabase.rawQuery(
            "SELECT id, title, url, local_image_path, category, focus, difficulty, description, knee_safe, knee_warning, saved_at FROM saved_darebee ORDER BY saved_at DESC",
            null
        ).use { c ->
            while (c.moveToNext()) {
                list.add(
                    SavedDarebeeWorkout(
                        id = c.getString(0),
                        title = c.getString(1),
                        url = c.getString(2),
                        localImagePath = c.getString(3),
                        category = c.getString(4),
                        focus = c.getString(5),
                        difficulty = c.getString(6),
                        description = c.getString(7),
                        kneeSafe = c.getInt(8) == 1,
                        kneeWarning = c.getString(9),
                        savedAtMillis = c.getLong(10)
                    )
                )
            }
        }
        return list
    }

    fun upsertSavedDarebee(item: SavedDarebeeWorkout) {
        val cv = ContentValues().apply {
            put("id", item.id)
            put("title", item.title)
            put("url", item.url)
            put("local_image_path", item.localImagePath)
            put("category", item.category)
            put("focus", item.focus)
            put("difficulty", item.difficulty)
            put("description", item.description)
            put("knee_safe", if (item.kneeSafe) 1 else 0)
            put("knee_warning", item.kneeWarning)
            put("saved_at", item.savedAtMillis)
        }
        writableDatabase.insertWithOnConflict("saved_darebee", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun deleteSavedDarebee(id: String) {
        writableDatabase.delete("saved_darebee", "id = ?", arrayOf(id))
    }

    fun getWeightLogs(): List<WeightEntry> {
        val list = mutableListOf<WeightEntry>()
        readableDatabase.rawQuery(
            "SELECT date_iso, weight_kg, waist_cm FROM weight_logs ORDER BY date_iso ASC",
            null
        ).use { c ->
            while (c.moveToNext()) {
                val waist = if (c.isNull(2)) null else c.getFloat(2)
                list.add(WeightEntry(c.getString(0), c.getFloat(1), waist))
            }
        }
        return list
    }

    fun upsertWeightLog(entry: WeightEntry) {
        val cv = ContentValues().apply {
            put("date_iso", entry.dateIso)
            put("weight_kg", entry.weightKg)
            if (entry.waistCm != null) put("waist_cm", entry.waistCm) else putNull("waist_cm")
        }
        writableDatabase.insertWithOnConflict("weight_logs", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getSavedExerciseGuides(): List<DarebeeExerciseDetail> {
        val list = mutableListOf<DarebeeExerciseDetail>()
        readableDatabase.rawQuery(
            "SELECT slug, title, url, thumb_url, youtube_embed, youtube_watch, instructions, steps_sep, works_muscles, breathing, common_mistakes, make_easier, make_harder, knee_safe, knee_warning FROM saved_exercise_guides ORDER BY title ASC",
            null
        ).use { c ->
            while (c.moveToNext()) {
                val steps = c.getString(7).split("\u001E").filter { it.isNotBlank() }
                list.add(
                    DarebeeExerciseDetail(
                        slug = c.getString(0),
                        title = c.getString(1),
                        url = c.getString(2),
                        thumbUrl = c.getString(3),
                        youtubeEmbedUrl = c.getString(4),
                        youtubeWatchUrl = c.getString(5),
                        instructions = c.getString(6),
                        steps = steps,
                        worksMuscles = c.getString(8),
                        breathing = c.getString(9),
                        commonMistakes = c.getString(10),
                        makeEasier = c.getString(11),
                        makeHarder = c.getString(12),
                        kneeSafe = c.getInt(13) == 1,
                        kneeWarning = c.getString(14)
                    )
                )
            }
        }
        return list
    }

    fun upsertSavedExerciseGuide(detail: DarebeeExerciseDetail) {
        val cv = ContentValues().apply {
            put("slug", detail.slug)
            put("title", detail.title)
            put("url", detail.url)
            put("thumb_url", detail.thumbUrl)
            put("youtube_embed", detail.youtubeEmbedUrl)
            put("youtube_watch", detail.youtubeWatchUrl)
            put("instructions", detail.instructions)
            put("steps_sep", detail.steps.joinToString("\u001E"))
            put("works_muscles", detail.worksMuscles)
            put("breathing", detail.breathing)
            put("common_mistakes", detail.commonMistakes)
            put("make_easier", detail.makeEasier)
            put("make_harder", detail.makeHarder)
            put("knee_safe", if (detail.kneeSafe) 1 else 0)
            put("knee_warning", detail.kneeWarning)
        }
        writableDatabase.insertWithOnConflict("saved_exercise_guides", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun deleteSavedExerciseGuide(slug: String) {
        writableDatabase.delete("saved_exercise_guides", "slug = ?", arrayOf(slug))
    }

    companion object {
        private const val DB_NAME = "four_wins_tactical.db"
        private const val DB_VERSION = 2
    }
}
