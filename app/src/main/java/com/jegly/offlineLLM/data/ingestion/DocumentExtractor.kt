package com.jegly.offlineLLM.data.ingestion

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.InputStream

object DocumentExtractor {

    suspend fun extractText(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        val mimeType = context.contentResolver.getType(uri) ?: ""
        val fileName = getFileName(context, uri)
        
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            when {
                fileName.endsWith(".txt", true) || mimeType.contains("text/plain") -> extractTxt(inputStream)
                fileName.endsWith(".html", true) || mimeType.contains("html") -> extractHtml(inputStream)
                fileName.endsWith(".pdf", true) || mimeType.contains("pdf") -> extractPdf(inputStream)
                else -> throw IllegalArgumentException("Unsupported file type: $fileName ($mimeType)")
            }
        } ?: throw IllegalArgumentException("Could not open input stream for URI")
    }

    private fun extractTxt(inputStream: InputStream): String {
        return inputStream.bufferedReader().use { it.readText() }
    }

    private fun extractHtml(inputStream: InputStream): String {
        val html = inputStream.bufferedReader().use { it.readText() }
        return Jsoup.parse(html).text()
    }

    private fun extractPdf(inputStream: InputStream): String {
        PDDocument.load(inputStream).use { document ->
            val stripper = PDFTextStripper()
            return stripper.getText(document)
        }
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var result = "unknown"
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) result = cursor.getString(nameIndex)
                }
            }
        } else {
            result = uri.lastPathSegment ?: "unknown"
        }
        return result
    }
}