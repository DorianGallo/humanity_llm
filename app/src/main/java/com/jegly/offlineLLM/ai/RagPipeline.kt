package com.jegly.offlineLLM.ai

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RagPipeline @Inject constructor(
    private val kiwixClient: KiwixClient
) {
    var zimEnabled: Boolean = true

    suspend fun enrichPrompt(userQuery: String): String {
        if (!zimEnabled) return userQuery

        val results = kiwixClient.search(userQuery)
        if (results.isEmpty()) return userQuery

        val context = results.joinToString("\n\n") { "[Source: ${it.title}]\n${it.snippet}" }
        return buildString {
            append("[ZIM Context]\n")
            append(context)
            append("\n\n[User Question]\n")
            append(userQuery)
            append("\n\n[Instructions: cite sources, use <think> tags]")
        }
    }
}
