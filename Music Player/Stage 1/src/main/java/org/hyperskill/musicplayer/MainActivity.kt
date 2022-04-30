package org.hyperskill.musicplayer

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class MainActivity : AppCompatActivity(), MusicPlayable {

  override lateinit var player: MediaPlayer

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    player = MediaPlayer.create(this, R.raw.wisdom)
    val playPauseButton = findViewById<ImageButton>(R.id.play_pause_button)
    playPauseButton.setOnClickListener {
      println("play_pause clicked")
      if (player.isPlaying) {
        player.pause()
      } else {
        player.start()
      }
    }
    val stopButton = findViewById<ImageButton>(R.id.stop_button)
    stopButton.setOnClickListener {
      println("stop clicked")
      player.stop()
      player.prepare()
    }
  }
}
