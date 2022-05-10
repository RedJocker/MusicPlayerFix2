package org.hyperskill.musicplayer

import android.media.MediaPlayer
import android.view.View

class Player(val player: MediaPlayer) {

    init {
        player.setOnCompletionListener {
            println("completed")
            player.seekTo(0)
        }
    }

    fun playPause(view: View) {
        println("playPause clicked")
        if (player.isPlaying) {
            player.pause()
//            player.seekTo(0)  // produces "The position after pausing the MediaPlayer should remain the same as it was in the instant it was paused expected:<500> but was:<0>"
            println("pause(), isPlaying: ${player.isPlaying}")
        } else {
//            player.seekTo(0)  // The Media player should resume playing in the same position it was paused expected:<1000> but was:<500>
            player.start()
            println("start(), isPlaying: ${player.isPlaying}")
        }
    }

    fun stop(view: View) {
        println("stop clicked")
        player.stop()
        player.seekTo(0)
        println("stop(), isPlaying: ${player.isPlaying}")
        player.prepare()
    }
}