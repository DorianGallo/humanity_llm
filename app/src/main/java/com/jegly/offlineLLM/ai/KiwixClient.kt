package com.jegly.offlineLLM.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

data class ZimResult(val title: String, val snippet: String)

@Singleton
class KiwixClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    var serverUrl: String = "localhost:8080"

    suspend fun search(query: String): List<ZimResult> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        try {
            val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
            val request = Request.Builder()
                .url("http://$serverUrl/search?q=$encodedQuery&count=5")
                .build()
            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) return@withContext emptyList()
            parseResults(response.body?.string() ?: return@withContext emptyList())
        } catch (_: IOException) {
            // Server not running — graceful fallback
            emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun parseResults(body: String): List<ZimResult> {
        return try {
            val json = JSONObject(body)
            val items = json.optJSONArray("results") ?: return emptyList()
            (0 until minOf(items.length(), 5)).map { i ->
                val item = items.getJSONObject(i)
                ZimResult(
                    title = item.optString("title", ""),
                    snippet = item.optString("snippet", item.optString("description", ""))
                )
            }.filter { it.title.isNotBlank() }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun isReachable(): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("http://$serverUrl/search?q=test&count=1")
                .build()
            okHttpClient.newCall(request).execute().use { it.isSuccessful }
        } catch (_: Exception) {
            false
        }
    }
}
