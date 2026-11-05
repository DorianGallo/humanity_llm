package com.jegly.offlineLLM.zim

object ZimEngine {
    init {
        System.loadLibrary("zim_engine")
    }

    external fun getZimVersion(): String
    external fun getZimMetadata(path: String): String
    external fun createZim(path: String, title: String, content: String): Boolean
    external fun searchZim(path: String, query: String): String
    external fun readZimEntry(path: String, entryPath: String): String
}