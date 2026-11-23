package com.tepen.fourwins.network

import android.content.Context
import com.tepen.fourwins.domain.FitnessCalculations
import com.tepen.fourwins.model.DarebeeExerciseDetail
import com.tepen.fourwins.model.DarebeeExerciseLibraryItem
import com.tepen.fourwins.model.DarebeeRemoteItem
import com.tepen.fourwins.model.SavedDarebeeWorkout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.concurrent.TimeUnit

object DarebeeScraper {

    enum class FeedType(val label: String, val path: String, val category: String) {
        WORKOUTS("Latest Workouts", "https://darebee.com/workouts.html", "workout"),
        CHALLENGES("30-Day Challenges", "https://darebee.com/challenges.html", "challenge"),
        JOINT_SAFE("Arthritis & Joint-Safe", "https://darebee.com/collections/arthritis-stiff-joints-workouts.html", "workout"),
        POSTERIOR("Posterior Chain", "https://darebee.com/collections/posterior-chain-workouts.html", "workout"),
        WAIST_ABS("Waist & Obliques", "https://darebee.com/collections/waist-and-obliques-workouts.html", "workout")
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    suspend fun fetchFeed(feedType: FeedType, page: Int = 0): Result<List<DarebeeRemoteItem>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val url = if (page > 0 && feedType in listOf(FeedType.WORKOUTS, FeedType.CHALLENGES)) {
                    "${feedType.path}?start=${page * 15}"
                } else {
                    feedType.path
                }
                val html = fetchHtml(url)
                parseFeedHtml(html, feedType.category)
            }
        }

    fun parseFeedHtml(html: String, defaultCategory: String): List<DarebeeRemoteItem> {
        val doc = Jsoup.parse(html, "https://darebee.com/")
        val seen = mutableSetOf<String>()
        val items = mutableListOf<DarebeeRemoteItem>()

        val anchors = doc.select("a[href*=/workouts/], a[href*=/challenges/]")
        for (a in anchors) {
            val href = a.absUrl("href")
            if (!href.endsWith(".html")) continue
            if (href.contains("/workouts.html") || href.contains("/challenges.html")) continue
            if (!seen.add(href)) continue

            val img = a.selectFirst("img")
            val title = a.attr("title").ifBlank { a.text() }.trim()
            if (title.isBlank()) continue

            val thumbSrc = img?.absUrl("src").orEmpty().ifBlank {
                img?.attr("data-src")?.let {
                    if (it.startsWith("http")) it else "https://darebee.com$it"
                }.orEmpty()
            }
            val posterGuess = thumbSrc
                .replace("-intro.jpg", ".jpg")
                .substringBefore("?")

            val cat = if (href.contains("/challenges/")) "challenge" else defaultCategory
            val safety = FitnessCalculations.analyzeKneeSafety(title)

            items.add(
                DarebeeRemoteItem(
                    title = title,
                    url = href,
                    thumbUrl = thumbSrc,
                    posterUrl = posterGuess,
                    category = cat,
                    kneeSafeFlag = safety.isKneeSafe,
                    kneeWarning = safety.warningMessage
                )
            )
        }
        return items
    }

    suspend fun fetchWorkoutDetail(item: DarebeeRemoteItem): Result<DarebeeRemoteItem> =
        withContext(Dispatchers.IO) {
            runCatching {
                val html = fetchHtml(item.url)
                parseDetailHtml(html, item)
            }
        }

    fun parseDetailHtml(html: String, baseItem: DarebeeRemoteItem): DarebeeRemoteItem {
        val doc = Jsoup.parse(html, "https://darebee.com/")
        val ogImage = doc.selectFirst("meta[property=og:image]")?.attr("content").orEmpty()
        val figureImg = doc.selectFirst("figure img")?.absUrl("src").orEmpty()
        val posterUrl = ogImage.ifBlank { figureImg }.ifBlank { baseItem.posterUrl }.substringBefore("?")

        val focusImg = doc.selectFirst(".infobox-works img")?.attr("alt").orEmpty().removePrefix("Focus:").trim()
        val diffImg = doc.selectFirst(".infobox-difficulty img")?.attr("alt").orEmpty().removePrefix("Difficulty:").trim()
        val paragraphs = doc.select("article p, .com-content-article__body p")
            .map { it.text().trim() }
            .filter { it.length > 35 && !it.contains("donate", ignoreCase = true) }
        val description = paragraphs.take(2).joinToString("\n\n")
        val safety = FitnessCalculations.analyzeKneeSafety("${baseItem.title} $description")

        return baseItem.copy(
            posterUrl = posterUrl,
            focus = focusImg.ifBlank { baseItem.focus },
            difficulty = diffImg.ifBlank { baseItem.difficulty },
            description = description,
            kneeSafeFlag = safety.isKneeSafe,
            kneeWarning = safety.warningMessage
        )
    }

    suspend fun downloadWorkoutOffline(
        context: Context,
        detail: DarebeeRemoteItem
    ): Result<SavedDarebeeWorkout> = withContext(Dispatchers.IO) {
        runCatching {
            val dir = File(context.filesDir, "darebee_posters").apply { mkdirs() }
            val imageUrl = detail.posterUrl.ifBlank { detail.thumbUrl }
            val targetFile = File(dir, "db_${UUID.randomUUID().toString().take(10)}.jpg")

            if (imageUrl.isNotBlank()) {
                val req = Request.Builder()
                    .url(imageUrl)
                    .header("User-Agent", "FourWinsAndroid/1.0")
                    .build()
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) error("HTTP ${resp.code} downloading poster")
                    resp.body?.byteStream()?.use { input ->
                        FileOutputStream(targetFile).use { output -> input.copyTo(output) }
                    } ?: error("Empty image body")
                }
            }

            SavedDarebeeWorkout(
                id = UUID.randomUUID().toString().take(12),
                title = detail.title,
                url = detail.url,
                localImagePath = targetFile.absolutePath,
                category = detail.category,
                focus = detail.focus,
                difficulty = detail.difficulty,
                description = detail.description,
                kneeSafe = detail.kneeSafeFlag,
                kneeWarning = detail.kneeWarning,
                savedAtMillis = System.currentTimeMillis()
            )
        }
    }

    suspend fun fetchExerciseLibrary(): Result<List<DarebeeExerciseLibraryItem>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val html = fetchHtml("https://darebee.com/library.html")
                parseExerciseLibraryHtml(html)
            }
        }

    fun parseExerciseLibraryHtml(html: String): List<DarebeeExerciseLibraryItem> {
        val marker = "var items ="
        val startIdx = html.indexOf(marker)
        if (startIdx < 0) return emptyList()
        val arrStart = html.indexOf('[', startIdx)
        if (arrStart < 0) return emptyList()

        // Find matching closing bracket of JSON array
        var depth = 0
        var inStr = false
        var escape = false
        var arrEnd = -1
        for (i in arrStart until html.length) {
            val ch = html[i]
            if (escape) {
                escape = false
                continue
            }
            if (ch == '\\' && inStr) {
                escape = true
                continue
            }
            if (ch == '"') {
                inStr = !inStr
                continue
            }
            if (!inStr) {
                if (ch == '[') depth++
                else if (ch == ']') {
                    depth--
                    if (depth == 0) {
                        arrEnd = i
                        break
                    }
                }
            }
        }
        if (arrEnd <= arrStart) return emptyList()

        val jsonArr = JSONArray(html.substring(arrStart, arrEnd + 1))
        val result = ArrayList<DarebeeExerciseLibraryItem>(jsonArr.length())
        for (i in 0 until jsonArr.length()) {
            val obj = jsonArr.getJSONObject(i)
            val title = obj.optString("t").trim()
            val relUrl = obj.optString("u").trim()
            val slug = obj.optString("a").trim()
            if (title.isBlank() || relUrl.isBlank()) continue
            val safety = FitnessCalculations.analyzeKneeSafety(title)
            result.add(
                DarebeeExerciseLibraryItem(
                    title = title,
                    slug = slug,
                    url = if (relUrl.startsWith("http")) relUrl else "https://darebee.com/$relUrl",
                    thumbUrl = "https://darebee.com/images/library/$slug.jpg",
                    type = obj.optString("ty", "strength"),
                    focus = obj.optString("f", "full-body"),
                    difficulty = obj.optString("d", "normal"),
                    kneeSafe = safety.isKneeSafe
                )
            )
        }
        return result
    }

    suspend fun fetchExerciseDetail(item: DarebeeExerciseLibraryItem): Result<DarebeeExerciseDetail> =
        withContext(Dispatchers.IO) {
            runCatching {
                val html = fetchHtml(item.url)
                parseExerciseDetailHtml(html, item)
            }
        }

    fun parseExerciseDetailHtml(html: String, item: DarebeeExerciseLibraryItem): DarebeeExerciseDetail {
        val doc = Jsoup.parse(html, "https://darebee.com/")
        val iframeSrc = doc.selectFirst(".exercise-video iframe, iframe[src*=youtube]")?.attr("src").orEmpty()
        val cleanEmbed = if (iframeSrc.isNotBlank()) {
            iframeSrc.substringBefore("?") + "?rel=0&showinfo=0"
        } else ""
        val videoId = cleanEmbed.substringAfter("/embed/", "").substringBefore("?")
        val watchUrl = if (videoId.isNotBlank()) "https://www.youtube.com/watch?v=$videoId" else item.url

        var works = ""
        var breathing = ""
        var commonMistakes = ""
        var makeEasier = ""
        var makeHarder = ""
        val instructionParts = mutableListOf<String>()

        val paragraphs = doc.select("article p, .com-content-article__body p, .item-page p")
        for (p in paragraphs) {
            val txt = p.text().trim()
            if (txt.isBlank()) continue
            if (txt.contains("donate", ignoreCase = true) || txt.contains("consider donating", ignoreCase = true)) continue
            when {
                txt.startsWith("Works:", ignoreCase = true) -> works = txt.removePrefix("Works:").trim()
                txt.startsWith("Breathing:", ignoreCase = true) -> breathing = txt.removePrefix("Breathing:").trim()
                txt.startsWith("Common Mistakes:", ignoreCase = true) -> commonMistakes = txt.removePrefix("Common Mistakes:").trim()
                txt.startsWith("Make Easier:", ignoreCase = true) -> makeEasier = txt.removePrefix("Make Easier:").trim()
                txt.startsWith("Make Harder:", ignoreCase = true) -> makeHarder = txt.removePrefix("Make Harder:").trim()
                txt.length > 20 -> instructionParts.add(txt)
            }
        }

        val fullInstr = instructionParts.joinToString(" ")
        val steps = fullInstr
            .split(Regex("(?<=[.!?])\\s+"))
            .map { it.trim() }
            .filter { it.length > 10 }
            .ifEmpty { listOf(fullInstr.ifBlank { "Follow the video demonstration carefully with controlled tempo." }) }

        val safety = FitnessCalculations.analyzeKneeSafety("${item.title} $fullInstr")

        return DarebeeExerciseDetail(
            slug = item.slug,
            title = item.title,
            url = item.url,
            thumbUrl = item.thumbUrl,
            youtubeEmbedUrl = cleanEmbed,
            youtubeWatchUrl = watchUrl,
            instructions = fullInstr,
            steps = steps,
            worksMuscles = works,
            breathing = breathing,
            commonMistakes = commonMistakes,
            makeEasier = makeEasier,
            makeHarder = makeHarder,
            kneeSafe = safety.isKneeSafe,
            kneeWarning = safety.warningMessage
        )
    }

    private fun fetchHtml(url: String): String {
        val req = Request.Builder()
            .url(url)
            .header("User-Agent", "Mozilla/5.0 (Linux; Android 14) FourWinsFitness/1.0")
            .build()
        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) error("Network error HTTP ${resp.code}")
            return resp.body?.string() ?: error("Empty response")
        }
    }
}
