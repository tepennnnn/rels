package com.tepen.fourwins

import com.tepen.fourwins.data.PreloadedCatalog
import com.tepen.fourwins.domain.FitnessCalculations
import com.tepen.fourwins.model.DailyWinEntry
import com.tepen.fourwins.model.PillarWinRule
import com.tepen.fourwins.model.UserProfile
import com.tepen.fourwins.model.WinPillar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class FitnessCalculationsTest {

    @Test
    fun `recomp metrics match 34yo 5ft4 75kg baseline`() {
        val profile = UserProfile(age = 34, heightCm = 162.5f, weightKg = 75.0f, isMale = true)
        val metrics = FitnessCalculations.computeRecompMetrics(profile)
        // 75 / (1.625^2) = 28.40
        assertEquals(28.4f, metrics.bmi, 0.1f)
        assertTrue(metrics.targetKcal in 1500..2200)
        assertTrue(metrics.proteinGrams in 125..150)
    }

    @Test
    fun `win counter evaluates 4 pillars and streaks accurately`() {
        val habits = PreloadedCatalog.defaultHabits
        val today = LocalDate.of(2026, 11, 22)
        val yesterday = today.minusDays(1)
        val twoDaysAgo = today.minusDays(2)

        val entries = mapOf(
            // 2 days ago: only Physical + Mental
            twoDaysAgo.toString() to DailyWinEntry(
                dateIso = twoDaysAgo.toString(),
                completedHabitIds = setOf("phy_walk", "mnt_read")
            ),
            // Yesterday: 4/4 perfect day
            yesterday.toString() to DailyWinEntry(
                dateIso = yesterday.toString(),
                completedHabitIds = setOf("phy_gym", "mnt_write", "fin_working", "spr_meditate")
            ),
            // Today: 4/4 perfect day
            today.toString() to DailyWinEntry(
                dateIso = today.toString(),
                completedHabitIds = setOf("phy_walk", "mnt_create", "fin_business", "spr_gratitude")
            )
        )

        val summary = FitnessCalculations.computeWinStats(
            today = today,
            habits = habits,
            entriesByDate = entries,
            rule = PillarWinRule.AT_LEAST_ONE
        )

        assertEquals(4, summary.todayPillarsWon)
        assertEquals(2, summary.perfectDayStreak)
        assertEquals(3, summary.activeDayStreak)
        assertEquals(10, summary.totalAllTimeHabitWins)
        assertEquals(2, summary.totalPerfectDays)
        assertTrue(summary.todayPillarStatus[WinPillar.PHYSICAL] == true)
        assertTrue(summary.todayPillarStatus[WinPillar.SPIRITUAL] == true)
    }

    @Test
    fun `all tasks rule requires all habits in a pillar`() {
        val habits = PreloadedCatalog.defaultHabits
        val today = LocalDate.of(2026, 11, 22)
        val entries = mapOf(
            today.toString() to DailyWinEntry(
                dateIso = today.toString(),
                completedHabitIds = setOf("phy_walk", "mnt_read", "mnt_write", "mnt_create")
            )
        )

        val summary = FitnessCalculations.computeWinStats(
            today = today,
            habits = habits,
            entriesByDate = entries,
            rule = PillarWinRule.ALL_TASKS
        )

        assertFalse(summary.todayPillarStatus[WinPillar.PHYSICAL] == true)
        assertTrue(summary.todayPillarStatus[WinPillar.MENTAL] == true)
        assertEquals(1, summary.todayPillarsWon)
    }

    @Test
    fun `knee safety analyzer flags high impact moves and passes low impact moves`() {
        val risky = FitnessCalculations.analyzeKneeSafety("10 jump squats, 20 burpees, 20 jumping lunges")
        assertFalse(risky.isKneeSafe)
        assertTrue(risky.warningMessage.contains("jump squat"))

        val safe = FitnessCalculations.analyzeKneeSafety("Dumbbell bench press, lat pulldown, incline walk, glute bridges")
        assertTrue(safe.isKneeSafe)
    }
}
