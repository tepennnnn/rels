package com.tepen.fourwins.model

enum class WinPillar(
    val title: String,
    val tag: String,
    val colorHex: Long,
    val description: String
) {
    PHYSICAL("Physical", "PHY", 0xFFFF3B4E, "Body movement, gym, & stamina"),
    MENTAL("Mental", "MNT", 0xFF00E5FF, "Learning, writing, & creation"),
    FINANCIAL("Financial", "FIN", 0xFF00E676, "Work, business, & wealth"),
    SPIRITUAL("Spiritual", "SPR", 0xFFFFB300, "Gratitude, meditation, & clarity")
}

enum class PillarWinRule(val label: String) {
    AT_LEAST_ONE("Any 1+ task wins pillar"),
    ALL_TASKS("All tasks win pillar")
}

enum class KneeComfort(val label: String, val score: Int, val colorHex: Long) {
    GREAT("Pain-Free / Strong", 3, 0xFF00E676),
    MILD("Mild Stiffness", 2, 0xFFFFB300),
    SORE("Sore — Ease Up", 1, 0xFFFF3B4E)
}

enum class Equipment(val label: String) {
    ROWING_MACHINE("Rowing Machine"),
    EXERCISE_BENCH("Exercise Bench"),
    LAT_PULLDOWN("Lat Pulldown"),
    LEG_PRESS("Leg Press"),
    TREADMILL("Treadmill"),
    DUMBBELLS("Dumbbells"),
    BARBELLS("Barbells"),
    BODYWEIGHT("Bodyweight / Floor")
}

data class WinHabit(
    val id: String,
    val pillar: WinPillar,
    val title: String,
    val sortOrder: Int,
    val isActive: Boolean = true
)

data class DailyWinEntry(
    val dateIso: String, // yyyy-MM-dd
    val completedHabitIds: Set<String>,
    val victoryNote: String = "",
    val kneeComfort: KneeComfort? = null
)

data class WinStatsSummary(
    val todayPillarStatus: Map<WinPillar, Boolean>,
    val todayPillarsWon: Int,
    val todayHabitsDone: Int,
    val todayHabitsTotal: Int,
    val perfectDayStreak: Int,
    val activeDayStreak: Int,
    val bestActiveStreak: Int,
    val totalAllTimeHabitWins: Int,
    val totalPerfectDays: Int,
    val xp: Int,
    val level: Int,
    val rankTitle: String,
    val xpIntoLevel: Int,
    val xpPerLevel: Int,
    val pillarWinCountsLast30: Map<WinPillar, Int>
)

data class UserProfile(
    val name: String = "Athlete",
    val age: Int = 34,
    val heightCm: Float = 162.5f, // 5 ft 4 in
    val weightKg: Float = 75.0f,
    val targetWeightKg: Float = 68.0f,
    val isMale: Boolean = true,
    val kneeSafeMode: Boolean = true,
    val pillarWinRule: PillarWinRule = PillarWinRule.AT_LEAST_ONE,
    val voiceCoachEnabled: Boolean = true,
    val beepEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val defaultRestSeconds: Int = 60,
    val morningReminderEnabled: Boolean = true,
    val morningHour: Int = 7,
    val morningMinute: Int = 30,
    val eveningReminderEnabled: Boolean = true,
    val eveningHour: Int = 20,
    val eveningMinute: Int = 30,
    val availableEquipment: Set<Equipment> = Equipment.entries.toSet()
)

data class WorkoutExercise(
    val id: String,
    val name: String,
    val equipment: Equipment,
    val sets: Int,
    val repsOrSeconds: String,
    val isTimed: Boolean,
    val durationSeconds: Int = 45,
    val restSeconds: Int = 60,
    val formCue: String,
    val kneeSafetyNote: String,
    val kneeSwapName: String? = null,
    val kneeSwapCue: String? = null
)

data class WorkoutProgram(
    val id: String,
    val dayNumber: Int?, // 1..7 for tailored weekly split, null for express/darebee workouts
    val title: String,
    val subtitle: String,
    val category: String,
    val durationMinutes: Int,
    val focus: String,
    val kneeSafeRating: String,
    val darebeeSourceRef: String,
    val exercises: List<WorkoutExercise>
)

data class WorkoutSessionLog(
    val id: Long = 0,
    val dateIso: String,
    val programId: String,
    val programTitle: String,
    val durationMinutes: Int,
    val setsCompleted: Int,
    val kneeComfort: KneeComfort,
    val notes: String = ""
)

data class Challenge30Day(
    val id: String,
    val title: String,
    val subtitle: String,
    val darebeeRef: String,
    val linkedPillar: WinPillar,
    val kneeSafe: Boolean,
    val dailyTargets: List<String>, // 30 items
    val completedDays: Set<Int> = emptySet() // 1..30
)

data class PdfLibraryItem(
    val id: String,
    val title: String,
    val filePath: String,
    val pageCount: Int,
    val lastPage: Int = 0,
    val bookmarkedPages: Set<Int> = emptySet(),
    val addedAtMillis: Long = System.currentTimeMillis()
)

data class DarebeeRemoteItem(
    val title: String,
    val url: String,
    val thumbUrl: String,
    val posterUrl: String,
    val category: String, // "workout" or "challenge" or "book"
    val focus: String = "",
    val difficulty: String = "",
    val description: String = "",
    val kneeSafeFlag: Boolean = true,
    val kneeWarning: String = "",
    val pdfDownloadUrl: String? = null
)

data class SavedDarebeeWorkout(
    val id: String,
    val title: String,
    val url: String,
    val localImagePath: String,
    val category: String,
    val focus: String,
    val difficulty: String,
    val description: String,
    val kneeSafe: Boolean,
    val kneeWarning: String,
    val savedAtMillis: Long
)

data class WeightEntry(
    val dateIso: String,
    val weightKg: Float,
    val waistCm: Float? = null
)

data class RecompMetrics(
    val bmi: Float,
    val bmiLabel: String,
    val bmrKcal: Int,
    val tdeeKcal: Int,
    val targetKcal: Int,
    val proteinGrams: Int,
    val stepsOrCardioTarget: String
)
