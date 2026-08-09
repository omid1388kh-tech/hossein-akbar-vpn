package com.v2ray.ang.util

import android.media.AudioManager
import android.media.ToneGenerator
import com.v2ray.ang.AppConfig
import com.v2ray.ang.handler.MmkvManager

/**
 * Short, pleasant confirmation tones for connect/disconnect events.
 * Uses Android's built-in ToneGenerator so no audio assets need to be
 * bundled with the app. Respects a user-toggleable setting (on by default).
 */
object ConnectionSoundPlayer {

    fun playConnected() {
        if (!isEnabled()) return
        runCatching {
            val tg = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 70)
            // Two quick ascending beeps -> reads as a positive "connected" cue.
            tg.startTone(ToneGenerator.TONE_PROP_BEEP2, 90)
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                runCatching {
                    tg.startTone(ToneGenerator.TONE_PROP_ACK, 110)
                }
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    runCatching { tg.release() }
                }, 200)
            }, 110)
        }
    }

    fun playDisconnected() {
        if (!isEnabled()) return
        runCatching {
            val tg = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 60)
            // A single lower tone -> reads as a neutral "disconnected" cue.
            tg.startTone(ToneGenerator.TONE_PROP_NACK, 140)
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                runCatching { tg.release() }
            }, 220)
        }
    }

    private fun isEnabled(): Boolean =
        MmkvManager.decodeSettingsBool(AppConfig.PREF_CONNECTION_SOUNDS_ENABLED, true)
}
