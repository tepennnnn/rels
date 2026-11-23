package com.tepen.fourwins.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import java.util.Locale

class WorkoutCoach(private val context: Context) {

    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var toneGenerator: ToneGenerator? = null

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setSpeechRate(1.02f)
                ttsReady = true
            }
        }
        runCatching {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        }
    }

    fun speak(text: String, voiceEnabled: Boolean) {
        if (!voiceEnabled || !ttsReady) return
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "cue_${System.currentTimeMillis()}")
    }

    fun beepCountdown(beepEnabled: Boolean) {
        if (!beepEnabled) return
        runCatching {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 130)
        }
    }

    fun beepStartOrFinish(beepEnabled: Boolean) {
        if (!beepEnabled) return
        runCatching {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 260)
        }
    }

    fun beepVictory(beepEnabled: Boolean) {
        if (!beepEnabled) return
        runCatching {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 350)
        }
    }

    fun vibrateTick(hapticsEnabled: Boolean) {
        if (!hapticsEnabled) return
        vibrateMs(35L, 120)
    }

    fun vibratePulse(hapticsEnabled: Boolean) {
        if (!hapticsEnabled) return
        vibrateMs(120L, 210)
    }

    fun vibrateVictory(hapticsEnabled: Boolean) {
        if (!hapticsEnabled) return
        val vibrator = getVibrator() ?: return
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val pattern = longArrayOf(0, 70, 50, 90, 50, 150)
                val amps = intArrayOf(0, 160, 0, 200, 0, 255)
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, amps, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(250L)
            }
        }
    }

    private fun vibrateMs(ms: Long, amplitude: Int) {
        val vibrator = getVibrator() ?: return
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(ms, amplitude))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(ms)
            }
        }
    }

    private fun getVibrator(): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val mgr = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            mgr?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun release() {
        runCatching { tts?.stop(); tts?.shutdown() }
        runCatching { toneGenerator?.release() }
    }
}
