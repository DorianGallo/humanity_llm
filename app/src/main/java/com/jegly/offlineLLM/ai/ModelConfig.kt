package com.jegly.offlineLLM.ai

data class ModelConfig(
    val name: String,
    val filePath: String,
    val temperature: Float = 0.2f,
    val topP: Float = 0.85f,
    val topK: Int = 30,
    val repeatPenalty: Float = 1.08f,
    val contextSize: Int = 3072,
    val mirostat: Int = 2,
    val mirostatTau: Float = 3.0f,
    val mirostatEta: Float = 0.1f,
    val systemPrompt: String
)
