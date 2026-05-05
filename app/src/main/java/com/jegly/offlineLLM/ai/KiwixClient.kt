package com.jegly.offlineLLM.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

data class ZimResult(val title: String, val snippet: String)

@Singleton
class KiwixClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    // Reset discovered book name if URL changes
    var serverUrl: String = "localhost:8080"
        set(value) { field = value; bookName = null }

    // Lazily discovered from first search response (books.name in pagination links)
    private var bookName: String? = null

    // kiwix-serve API: GET /search?pattern=QUERY[&books.name=NAME]
    // Returns HTML; book name auto-discovered from first response and reused.
    suspend fun search(query: String): List<ZimResult> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        try {
            val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
            val url = buildUrl(encodedQuery)
            val body = fetch(url) ?: return@withContext emptyList()
            if (bookName == null) bookName = extractBookName(body)
            parseHtmlResults(body)
        } catch (_: IOException) {
            emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun isReachable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val body = fetch("http://$serverUrl/search?pattern=test") ?: return@withContext false
            if (bookName == null) bookName = extractBookName(body)
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun buildUrl(encodedQuery: String): String {
        val base = "http://$serverUrl/search?pattern=$encodedQuery"
        return bookName?.let { "$base&books.name=$it" } ?: base
    }

    private fun fetch(url: String): String? {
        val response = okHttpClient.newCall(Request.Builder().url(url).build()).execute()
        if (!response.isSuccessful) return null
        return response.body?.string()
    }

    /** Extracts the full book name (e.g. wikipedia_en_ray-charles_nopic_2026-05) from pagination links. */
    private fun extractBookName(html: String): String? =
        Regex("""books\.name=([^&"\s]+)""").find(html)?.groupValues?.get(1)

    /** Parses up to 5 results from kiwix-serve HTML search response. */
    private fun parseHtmlResults(html: String): List<ZimResult> {
        val results = mutableListOf<ZimResult>()
        val liRegex = Regex(
            """<li>\s*<a[^>]*>\s*([^<]+?)\s*</a>\s*<cite>(.*?)</cite>""",
            setOf(RegexOption.DOT_MATCHES_ALL)
        )
        for (match in liRegex.findAll(html)) {
            val title = match.groupValues[1].trim()
            val snippet = match.groupValues[2]
                .replace(Regex("<[^>]+>"), "")
                .replace("&amp;", "&").replace("&apos;", "'")
                .replace("&quot;", "\"").replace("&lt;", "<").replace("&gt;", ">")
                .trim().take(200)
            if (title.isNotBlank()) results.add(ZimResult(title = title, snippet = snippet))
            if (results.size >= 5) break
        }
        return results
    }
}
