package com.example.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class SoundBoxAnnouncer(private val context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Default to Hindi Locale if available, else English
            val hindi = Locale("hi", "IN")
            val result = tts?.setLanguage(hindi)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("SoundBoxAnnouncer", "Hindi language is not supported or missing resources, using US English.")
                tts?.setLanguage(Locale.US)
            } else {
                Log.d("SoundBoxAnnouncer", "Hindi voice engine initialized successfully.")
            }
            tts?.setPitch(1.0f)
            tts?.setSpeechRate(0.95f) // Warm, polite, slightly slower speech tempo
            isReady = true
        } else {
            Log.e("SoundBoxAnnouncer", "Failed to initialize TTS engine.")
        }
    }

    fun speakHindi(amount: Double) {
        if (!isReady) {
            Log.w("SoundBoxAnnouncer", "TTS Engine is not fully initialized yet.")
            return
        }
        val text = "राज क्यू आर पर ${amount.toInt()} रुपये प्राप्त हुए।"
        // For Android Lollipop and above
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "RajQrSoundBoxPaymentNotification")
    }

    fun speakEnglish(amount: Double) {
        if (!isReady) {
            Log.w("SoundBoxAnnouncer", "TTS Engine is not fully initialized yet.")
            return
        }
        val text = "Received ${amount.toInt()} rupees on Raj QR."
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "RajQrSoundBoxPaymentNotification")
    }

    fun setLanguage(isHindi: Boolean) {
        if (isHindi) {
            val result = tts?.setLanguage(Locale("hi", "IN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.US)
            }
        } else {
            tts?.setLanguage(Locale.US)
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
