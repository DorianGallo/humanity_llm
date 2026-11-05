package com.jegly.offlineLLM.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.util.concurrent.TimeUnit

object WebScraper {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
    
    suspend fun scrapeUrl(url: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "HumanityLLM/1.0 (Offline-First)")
                .build()
            
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}"))
            }
            
            val html = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response"))
            
            // Use Jsoup to extract readable text, removing scripts/styles
            val doc = Jsoup.parse(html)
            doc.select("script, style, nav, footer, header, aside").remove()
            
            val text = doc.body()?.text() ?: ""
            
            // Limit to reasonable size for LLM context
            val truncated = if (text.length > 4000) text.take(4000) + "..." else text
            
            Result.success(truncated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
