package com.jegly.offlineLLM.data.repository

import com.jegly.offlineLLM.data.local.KnowledgeBaseDao
import com.jegly.offlineLLM.data.local.KnowledgeBaseEntity
import com.jegly.offlineLLM.zim.ZimEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KnowledgeBaseRepository @Inject constructor(
    private val dao: KnowledgeBaseDao
) {
    fun getAllDocuments(): Flow<List<KnowledgeBaseEntity>> = dao.getAllDocuments()

    suspend fun addDocument(filePath: String, title: String) {
        dao.insertDocument(KnowledgeBaseEntity(filePath = filePath, title = title))
    }

    suspend fun removeDocument(filePath: String) {
        dao.deleteDocument(filePath)
    }

    /**
     * Searches all mounted ZIM files and formats the results for LLM context injection.
     */
    suspend fun retrieveContext(query: String): String = withContext(Dispatchers.IO) {
        val contextBuilder = StringBuilder()
        contextBuilder.append("<knowledge_base_context>\n")
        
        val documents = dao.getAllDocuments().first()
        for (doc in documents) {
            try {
                val searchResults = ZimEngine.searchZim(doc.filePath, query)
                if (searchResults.isNotBlank() && !searchResults.startsWith("Error:") && !searchResults.startsWith("No results")) {
                    contextBuilder.append("Source: ${doc.title}\n")
                    contextBuilder.append(searchResults)
                    contextBuilder.append("\n")
                }
            } catch (e: Exception) {
                // Silently fail for individual documents to ensure others are still searched
            }
        }
        
        contextBuilder.append("</knowledge_base_context>\n")
        contextBuilder.toString()
    }
}