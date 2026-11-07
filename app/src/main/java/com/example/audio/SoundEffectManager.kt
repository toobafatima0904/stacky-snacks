package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.model.MusicTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SoundEffectManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    var isSoundEnabled: Boolean = true
    var isHapticsEnabled: Boolean = true
    var currentMusicTheme: MusicTheme = MusicTheme.CHIME_ARCADE

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    private fun playTone(
        frequencies: List<Float>,
        durationsMs: List<Int>,
        waveType: Int = 0, // 0 = sine, 1 = square, 2 = triangle
        volume: Float = 0.7f
    ) {
        if (!isSoundEnabled) return

        scope.launch {
            try {
                val sampleRate = 22050
                var totalSamples = 0
                val segments = frequencies.zip(durationsMs).map { (freq, durationMs) ->
                    val numSamples = (sampleRate * (durationMs / 1000f)).toInt().coerceAtLeast(1)
                    totalSamples += numSamples
                    Triple(freq, durationMs, numSamples)
                }

                val generatedSnd = ShortArray(totalSamples)
                var writeIndex = 0

                for ((freq, _, numSamples) in segments) {
                    val period = sampleRate / freq
                    for (i in 0 until numSamples) {
                        val t = i.toFloat() / sampleRate
                        val rawSample = when (waveType) {
                            1 -> { // Square (Retro 8-bit)
                                val phase = (i % period) / period
                                if (phase < 0.5f) 0.8f else -0.8f
                            }
                            2 -> { // Triangle
                                val phase = (i % period) / period
                                2f * kotlin.math.abs(2f * (phase - kotlin.math.floor(phase + 0.5f))) - 1f
                            }
                            else -> { // Sine + harmonics
                                (sin(2.0 * PI * freq * t) * 0.7 + sin(4.0 * PI * freq * t) * 0.3).toFloat()
                            }
                        }

                        // Decay envelope
                        val envelope = exp(-3.5f * (i.toFloat() / numSamples))
                        val sampleVal = (rawSample * envelope * volume * Short.MAX_VALUE).toInt()
                        generatedSnd[writeIndex++] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    }
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(totalSamples * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(generatedSnd, 0, generatedSnd.size)
                audioTrack.play()

                // Release track after playback completes
                val durationMs = durationsMs.sum()
                kotlinx.coroutines.delay(durationMs.toLong() + 50)
                audioTrack.release()
            } catch (_: Exception) {
                // Audio synthesis fallback silent
            }
        }
    }

    fun playDrop() {
        playTone(listOf(340f), listOf(60), volume = 0.4f)
        vibrate(15)
    }

    fun playLandingNormal() {
        playTone(listOf(220f, 180f), listOf(70, 70), waveType = 0, volume = 0.5f)
        vibrate(30)
    }

    fun playLandingGood() {
        val base = currentMusicTheme.baseFrequency
        playTone(listOf(base * 1.25f, base * 1.5f), listOf(80, 110), volume = 0.6f)
        vibrate(40)
    }

    fun playPerfect(combo: Int) {
        val wave = if (currentMusicTheme == MusicTheme.RETRO_8BIT) 1 else 0
        // Ascending pentatonic notes based on combo
        val pentatonicPitches = listOf(523.25f, 587.33f, 659.25f, 783.99f, 880.00f, 1046.50f, 1174.66f, 1318.51f)
        val noteIndex = (combo - 1).coerceIn(0, pentatonicPitches.size - 1)
        val root = pentatonicPitches[noteIndex]
        val fifth = root * 1.5f

        playTone(listOf(root, fifth), listOf(100, 160), waveType = wave, volume = 0.8f)
        vibrate(55)
    }

    fun playCoin() {
        playTone(listOf(987.77f, 1318.51f), listOf(60, 120), volume = 0.6f)
        vibrate(25)
    }

    fun playWobble() {
        playTone(listOf(150f, 130f, 160f), listOf(80, 80, 80), waveType = 2, volume = 0.45f)
        vibrate(50)
    }

    fun playPowerUp() {
        playTone(listOf(440f, 554.37f, 659.25f, 880f), listOf(70, 70, 70, 150), volume = 0.75f)
        vibrate(60)
    }

    fun playGameOver() {
        playTone(listOf(330f, 293f, 261f, 196f), listOf(120, 120, 140, 260), waveType = 1, volume = 0.7f)
        vibrate(100)
    }

    fun playSaveMe() {
        playTone(listOf(587f, 784f, 1046f), listOf(90, 90, 180), volume = 0.8f)
        vibrate(70)
    }

    private fun vibrate(durationMs: Long) {
        if (!isHapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {
            // Ignore vibration permissions error
        }
    }
}
