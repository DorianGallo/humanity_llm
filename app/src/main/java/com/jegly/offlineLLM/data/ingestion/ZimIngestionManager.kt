package com.jegly.offlineLLM.data.ingestion

import android.content.Context
import android.net.Uri
import com.jegly.offlineLLM.zim.ZimEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

object ZimIngestionManager {

    /**
     * Extracts text from a document URI and compiles it into a ZIM file.
     * 
     * @param context Application context
     * @param uri URI of the source document (PDF, HTML, TXT)
     * @param title Title to be stored in the ZIM metadata
     * @return Result containing the created File, or an Exception
     */
    suspend fun ingestDocument(
        context: Context, 
        uri: Uri, 
        title: String
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            // 1. Extract text from document
            val extractedText = DocumentExtractor.extractText(context, uri)
            if (extractedText.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Extracted text is empty"))
            }

            // 2. Prepare output directory and file
            val zimDir = File(context.filesDir, "zim_files")
            if (!zimDir.exists()) zimDir.mkdirs()
            
            val safeFileName = "${UUID.randomUUID()}.zim"
            val outputFile = File(zimDir, safeFileName)

            // 3. Compile to ZIM via native bridge
            val success = ZimEngine.createZim(outputFile.absolutePath, title, extractedText)
            
            if (success && outputFile.exists() && outputFile.length() > 0) {
                Result.success(outputFile)
            } else {
                Result.failure(Exception("Native ZIM creation failed or file was not generated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}