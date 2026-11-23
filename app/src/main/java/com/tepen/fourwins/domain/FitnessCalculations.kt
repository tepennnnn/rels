package com.tepen.fourwins.domain

import com.tepen.fourwins.model.DailyWinEntry
import com.tepen.fourwins.model.PillarWinRule
import com.tepen.fourwins.model.RecompMetrics
import com.tepen.fourwins.model.UserProfile
import com.tepen.fourwins.model.WinHabit
import com.tepen.fourwins.model.WinPillar
import com.tepen.fourwins.model.WinStatsSummary
import java.time.LocalDate
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

object FitnessCalculations {

    fun calculateBmi(weightKg: Float, heightCm: Float): Float {
        if (heightCm <= 0f) return 0f
        val meters = heightCm / 100f
        return (weightKg / meters.pow(2))
    }

    fun bmiCategory(bmi: Float): String = when {
        bmi < 18.5f -> "Underweight"
        bmi < 25.0f -> "Healthy Weight"
        bmi < 30.0f -> "Overweight — Recomp Zone"
        else -> "Obese — Steady Fat Loss"
    }

    fun computeRecompMetrics(profile: UserProfile): RecompMetrics {
        val bmi = calculateBmi(profile.weightKg, profile.heightCm)
        // Mifflin-St Jeor equation
        val base = (10f * profile.weightKg) + (6.25f * profile.heightCm) - (5f * profile.age)
        val bmr = (if (profile.isMale) base + 5f else base - 161f).roundToInt()
        // Beginner/returning training 4-5 days x 45-60 min + daily walk => ~1.45 activity multiplier
        val tdee = (bmr * 1.45f).roundToInt()
        // Safe sustainable recomp deficit (~400 kcal, floored at max(1450, bmr))
        val targetKcal = max(max(1450, bmr), tdee - 400)
        // ~1.8g protein per kg bodyweight (or target weight if higher BMI) for muscle retention & tone
        val proteinGrams = (profile.weightKg * 1.8f).roundToInt().coerceIn(110, 170)
        return RecompMetrics(
            bmi = bmi,
            bmiLabel = bmiCategory(bmi),
            bmrKcal = bmr,
            tdeeKcal = tdee,
            targetKcal = targetKcal,
            proteinGrams = proteinGrams,
            stepsOrCardioTarget = "7,500–9,000 low-impact steps or 25 min Row/Incline Walk"
        )
    }

    fun evaluatePillarWon(
        pillar: WinPillar,
        habits: List<WinHabit>,
        completedIds: Set<String>,
        rule: PillarWinRule
    ): Boolean {
        val activeInPillar = habits.filter { it.pillar == pillar && it.isActive }
        if (activeInPillar.isEmpty()) return false
        val doneCount = activeInPillar.count { it.id in completedIds }
        return when (rule) {
            PillarWinRule.AT_LEAST_ONE -> doneCount >= 1
            PillarWinRule.ALL_TASKS -> doneCount == activeInPillar.size
        }
    }

    fun computeWinStats(
        today: LocalDate,
        habits: List<WinHabit>,
        entriesByDate: Map<String, DailyWinEntry>,
        rule: PillarWinRule
    ): WinStatsSummary {
        val todayIso = today.toString()
        val todayEntry = entriesByDate[todayIso]
        val todayDoneIds = todayEntry?.completedHabitIds ?: emptySet()

        val activeHabits = habits.filter { it.isActive }
        val todayPillarStatus = WinPillar.entries.associateWith { pillar ->
            evaluatePillarWon(pillar, activeHabits, todayDoneIds, rule)
        }
        val todayPillarsWon = todayPillarStatus.values.count { it }
        val todayHabitsDone = activeHabits.count { it.id in todayDoneIds }

        val sortedDates = entriesByDate.keys
            .mapNotNull { runCatching { LocalDate.parse(it) }.getOrNull() }
            .sorted()

        var totalHabitWins = 0
        var totalPerfectDays = 0
        val pillarCountsLast30 = WinPillar.entries.associateWith { 0 }.toMutableMap()
        val thirtyDaysAgo = today.minusDays(29)

        val activeDaySet = mutableSetOf<LocalDate>()
        val perfectDaySet = mutableSetOf<LocalDate>()

        for ((dateStr, entry) in entriesByDate) {
            val d = runCatching { LocalDate.parse(dateStr) }.getOrNull() ?: continue
            totalHabitWins += entry.completedHabitIds.size
            val pillarsWon = WinPillar.entries.count { pillar ->
                val won = evaluatePillarWon(pillar, activeHabits, entry.completedHabitIds, rule)
                if (won && !d.isBefore(thirtyDaysAgo) && !d.isAfter(today)) {
                    pillarCountsLast30[pillar] = (pillarCountsLast30[pillar] ?: 0) + 1
                }
                won
            }
            if (pillarsWon > 0) activeDaySet.add(d)
            if (pillarsWon == 4) {
                perfectDaySet.add(d)
                totalPerfectDays++
            }
        }

        // Streak helper: counts backwards from today if today has activity, or yesterday if today isn't logged yet
        fun currentStreakFrom(daySet: Set<LocalDate>): Int {
            var cursor = if (today in daySet) today else today.minusDays(1)
            var count = 0
            while (cursor in daySet) {
                count++
                cursor = cursor.minusDays(1)
            }
            return count
        }

        var bestActive = 0
        var run = 0
        var prev: LocalDate? = null
        for (d in activeDaySet.sorted()) {
            run = if (prev != null && prev.plusDays(1) == d) run + 1 else 1
            if (run > bestActive) bestActive = run
            prev = d
        }

        val xp = (totalHabitWins * 15) + (totalPerfectDays * 50)
        val xpPerLevel = 250
        val level = (xp / xpPerLevel) + 1
        val xpIntoLevel = xp % xpPerLevel
        val rankTitle = when {
            level >= 20 -> "APEX"
            level >= 12 -> "TACTICIAN"
            level >= 7 -> "VANGUARD"
            level >= 3 -> "OPERATOR"
            else -> "RECRUIT"
        }

        return WinStatsSummary(
            todayPillarStatus = todayPillarStatus,
            todayPillarsWon = todayPillarsWon,
            todayHabitsDone = todayHabitsDone,
            todayHabitsTotal = activeHabits.size,
            perfectDayStreak = currentStreakFrom(perfectDaySet),
            activeDayStreak = currentStreakFrom(activeDaySet),
            bestActiveStreak = max(bestActive, currentStreakFrom(activeDaySet)),
            totalAllTimeHabitWins = totalHabitWins,
            totalPerfectDays = totalPerfectDays,
            xp = xp,
            level = level,
            rankTitle = rankTitle,
            xpIntoLevel = xpIntoLevel,
            xpPerLevel = xpPerLevel,
            pillarWinCountsLast30 = pillarCountsLast30
        )
    }

    data class KneeSafetyAnalysis(
        val isKneeSafe: Boolean,
        val warningMessage: String
    )

    private val highImpactKeywords = listOf(
        "jump squat", "jumping jack", "jumping lunge", "burpee", "box jump",
        "high knee", "squat hold", "lunge", "hops", "sprint", "plyo", "jump rope",
        "pistol", "tuck jump", "star jump", "skater"
    )

    fun analyzeKneeSafety(text: String): KneeSafetyAnalysis {
        val lower = text.lowercase()
        val hits = highImpactKeywords.filter { it in lower }
        return if (hits.isEmpty()) {
            KneeSafetyAnalysis(
                isKneeSafe = true,
                warningMessage = "Knee-friendly: no high-impact jumping keywords detected."
            )
        } else {
            val preview = hits.take(3).joinToString(", ")
            KneeSafetyAnalysis(
                isKneeSafe = false,
                warningMessage = "Joint Caution ($preview): swap jumps/lunges for low-impact rowing, incline walk, or glute bridges."
            )
        }
    }
}
