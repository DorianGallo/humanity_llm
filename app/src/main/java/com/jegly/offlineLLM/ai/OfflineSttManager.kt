package com.jegly.offlineLLM.ai

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineSttManager @Inject constructor() {
    private var audioRecord: AudioRecord? = null
    private var isRecording = false

    @SuppressLint("MissingPermission")
    fun startListening(locale: Locale = Locale.getDefault()): Flow<String> = callbackFlow {
        val sampleRate = 16000
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            trySend("Error: Failed to initialize AudioRecord")
            close()
            return@callbackFlow
        }

        isRecording = true
        audioRecord?.startRecording()
        trySend("Listening... (Offline STT Pipeline Active)")

        val buffer = ShortArray(bufferSize / 2)

        withContext(Dispatchers.IO) {
            while (isRecording && isActive) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: -1
                if (read > 0) {
                    // Audio captured successfully - STT inference will be added in future task
                }
            }
        }

        trySend("Offline transcription placeholder: Audio captured successfully.")
        close()
    }

    fun stopListening() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }
}