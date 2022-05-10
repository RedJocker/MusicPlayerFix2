package org.hyperskill.musicplayer

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {

  val player: Player by lazy {
    Player(MediaPlayer.create(this, R.raw.wisdom))
  }
  private val playPauseButton by lazy {
    findViewById<ImageButton>(R.id.playPauseButton)
  }
  private val stopButton by lazy {
    findViewById<ImageButton>(R.id.stopButton)
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    initListeners()
  }

  fun initListeners() {
    player
    playPauseButton.setOnClickListener(player::playPause)
    stopButton.setOnClickListener(player::stop)
  }
}





