package com.jegly.offlineLLM.zim

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)
class ZimEngineTest {

    @Test
    fun testReadZimMetadata() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        
        // 1. Copy asset to internal cache
        val outFile = File(context.cacheDir, "small.zim")
        context.assets.open("small.zim").use { input ->
            FileOutputStream(outFile).use { output ->
                input.copyTo(output)
            }
        }
        
        // 2. Call native bridge
        val result = ZimEngine.getZimMetadata(outFile.absolutePath)
        
        // 3. Verify no exception was thrown and data is returned
        println("ZIM METADATA RESULT: $result")
        assertFalse("Should not contain error", result.startsWith("Error:"))
        assertTrue("Should contain entry count", result.contains("Entries:"))
    }
}