package com.tepen.fourwins

import com.tepen.fourwins.model.DarebeeRemoteItem
import com.tepen.fourwins.network.DarebeeScraper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DarebeeScraperTest {

    @Test
    fun `parseFeedHtml extracts workouts and deduplicates links`() {
        val html = """
            <html><body>
              <a href="/workouts/bulwark-workout.html" title="Bulwark Workout">
                <img src="/images/workouts/bulwark-workout-intro.jpg?v=123">
              </a>
              <a href="/workouts/bulwark-workout.html" title="Bulwark Workout">Duplicate</a>
              <a href="/workouts/jump-squat-blast-workout.html" title="Jump Squat Blast">
                <img src="/images/workouts/jump-squat-blast-intro.jpg">
              </a>
            </body></html>
        """.trimIndent()

        val items = DarebeeScraper.parseFeedHtml(html, "workout")
        assertEquals(2, items.size)
        assertEquals("Bulwark Workout", items[0].title)
        assertEquals("https://darebee.com/images/workouts/bulwark-workout.jpg", items[0].posterUrl)
        assertTrue(items[0].kneeSafeFlag)
        assertFalse(items[1].kneeSafeFlag)
    }

    @Test
    fun `parseDetailHtml extracts metadata and knee warning`() {
        val html = """
            <html>
              <head><meta property="og:image" content="https://darebee.com/images/workouts/counter-workout.jpg"></head>
              <body>
                <div class="infobox-works"><img alt="Focus: Upperbody"></div>
                <div class="infobox-difficulty"><img alt="Difficulty: level 3 of 5"></div>
                <article>
                  <p>This is a clean upper body boxing counter workout with zero jumping squats or lunges.</p>
                </article>
              </body>
            </html>
        """.trimIndent()

        val base = DarebeeRemoteItem(
            title = "Counter Workout",
            url = "https://darebee.com/workouts/counter-workout.html",
            thumbUrl = "",
            posterUrl = "",
            category = "workout"
        )
        val parsed = DarebeeScraper.parseDetailHtml(html, base)
        assertEquals("Upperbody", parsed.focus)
        assertEquals("level 3 of 5", parsed.difficulty)
        assertEquals("https://darebee.com/images/workouts/counter-workout.jpg", parsed.posterUrl)
    }
}
