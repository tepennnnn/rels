package com.tepen.fourwins

import com.tepen.fourwins.model.DarebeeExerciseLibraryItem
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

    @Test
    fun `parseExerciseLibraryHtml parses Darebee video catalog`() {
        val html = """
            <script>
            var items = [{"t":"Bird Dog","u":"exercises/bird-dog.html","a":"bird-dog","ty":"strength","f":"full-body","d":"easy"},{"t":"Jump Squats","u":"exercises/jump-squats.html","a":"jump-squats","ty":"cardio","f":"lower-body","d":"hard"}];
            </script>
        """.trimIndent()

        val list = DarebeeScraper.parseExerciseLibraryHtml(html)
        assertEquals(2, list.size)
        assertEquals("Bird Dog", list[0].title)
        assertEquals("https://darebee.com/exercises/bird-dog.html", list[0].url)
        assertTrue(list[0].kneeSafe)
        assertFalse(list[1].kneeSafe)
    }

    @Test
    fun `parseExerciseDetailHtml extracts YouTube embed and step-by-step cues`() {
        val html = """
            <html><body>
              <div class="exercise-video"><iframe src="https://www.youtube.com/embed/iGa9FINcjO4?controls=1"></iframe></div>
              <article>
                <p>Start on all fours with hands under shoulders. Extend one arm straight out and opposite leg straight back. Pause, then return smoothly.</p>
                <p><strong>Works:</strong> abs, lower back, glutes</p>
                <p><strong>Breathing:</strong> Exhale as you extend, inhale as you return.</p>
                <p><strong>Common Mistakes:</strong> Letting hips tilt or arching lower back.</p>
                <p><strong>Make Easier:</strong> Extend only one leg at a time.</p>
                <p><strong>Make Harder:</strong> Add a 3-count hold at full extension.</p>
              </article>
            </body></html>
        """.trimIndent()

        val item = DarebeeExerciseLibraryItem(
            title = "Bird Dog",
            slug = "bird-dog",
            url = "https://darebee.com/exercises/bird-dog.html",
            thumbUrl = "https://darebee.com/images/library/bird-dog.jpg",
            type = "strength",
            focus = "full-body",
            difficulty = "easy",
            kneeSafe = true
        )
        val detail = DarebeeScraper.parseExerciseDetailHtml(html, item)
        assertEquals("https://www.youtube.com/embed/iGa9FINcjO4?rel=0&showinfo=0", detail.youtubeEmbedUrl)
        assertEquals("https://www.youtube.com/watch?v=iGa9FINcjO4", detail.youtubeWatchUrl)
        assertTrue(detail.steps.size >= 2)
        assertEquals("abs, lower back, glutes", detail.worksMuscles)
        assertTrue(detail.breathing.contains("Exhale"))
        assertTrue(detail.commonMistakes.contains("hips tilt"))
        assertTrue(detail.makeEasier.contains("one leg"))
        assertTrue(detail.makeHarder.contains("3-count"))
    }
}
