package org.hyperskill.musicplayer

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import java.lang.Runnable
import java.util.*

class Timer(private val player: MediaPlayer, private val onTick: () -> Unit, private val onStop: () -> Unit) {
    companion object {
        fun timeString(timeInMilliseconds: Int): String {
            return String.format(
                Locale.getDefault(),
                "%02d:%02d",
                timeInMilliseconds / 60_000, timeInMilliseconds % 60_000 / 1000
            )
        }
    }

    private var running = false
    private val handler = Handler(Looper.getMainLooper())

    private val runnable: Runnable = object: Runnable {
        override fun run() {
            if (running) {
                onTick()
                handler.postDelayed(this, 999)
            }
        }
    }

    fun pause() {
        running = false
        handler.removeCallbacks(runnable)
    }
    fun start() {
        running = true
        runTracker()
    }
    fun stop() {
        running = false
        onStop()
        handler.removeCallbacks(runnable)
    }

    fun timeString(): String {
        return timeString(player.currentPosition)
    }

    fun timeTotalSeconds() = player.currentPosition / 1000

    private fun runTracker() {
        handler.removeCallbacks(runnable)
        handler.post(runnable)
    }
}