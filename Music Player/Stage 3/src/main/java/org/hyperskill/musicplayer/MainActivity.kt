package org.hyperskill.musicplayer

import android.Manifest
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
  lateinit var player: MediaPlayer
  lateinit var timer: Timer
  lateinit var seekBar: SeekBar
  lateinit var currentTimeView: TextView
  lateinit var totalTimeView: TextView
  lateinit var searchButton: Button
  lateinit var playPauseButton: Button
  lateinit var stopButton: Button
  lateinit var songListView: RecyclerView



  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    askPermission(this)
    bindViews()

    player = MediaPlayer.create(this, R.raw.wisdom)
    timer = Timer(player, ::onTimerTick, ::onTimerStop)
    totalTimeView.text = Timer.timeString(player.duration)
    seekBar.max = player.duration / 1000
    songListView.adapter = SongRecyclerViewAdapter(listOf())

    initListeners()
  }

  private fun bindViews() {
    seekBar = findViewById(R.id.seekBar)
    totalTimeView = findViewById(R.id.totalTimeTv)
    currentTimeView = findViewById(R.id.currentTimeTv)
    searchButton = findViewById(R.id.searchButton)
    playPauseButton = findViewById(R.id.playPauseButton)
    stopButton = findViewById(R.id.stopButton)
    songListView = findViewById(R.id.songList)
  }

  private fun initListeners() {
    playPauseButton.setOnClickListener {
      if (player.isPlaying) {
        player.pause()
        timer.pause()
        println("playPause, paused")
      } else {
        player.start()
        timer.start()
        println("playPause, playing")
      }
    }

    stopButton.setOnClickListener {
      player.seekTo(0)
      player.stop()
      player.prepare()
      timer.stop()
    }

    seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
      override fun onProgressChanged(s: SeekBar?, progress: Int, fromUser: Boolean) {
        if(fromUser) {
          println("fromUser onProgressChanged()")
          currentTimeView.text = Timer.timeString(progress * 1000)
        } // else {
//            println("not fromUser onProgressChanged()")
//          }
      }
      override fun onStartTrackingTouch(s: SeekBar?) {
        println("onStartTrackingTouch")
        timer.pause()
      }
      override fun onStopTrackingTouch(s: SeekBar?) {
        player.seekTo(seekBar.progress * 1000)
        if(player.isPlaying) {
          timer.start()
        }
        println("onStopTrackingTouch, currentPosition: ${player.currentPosition}")
      }
    })

    player.setOnCompletionListener {
      it.seekTo(0)
      it.stop()
      it.prepare()
      println("onCompletionListener, isPlaying: ${it.isPlaying}, currentPosition: ${it.currentPosition}")
      timer.stop()
    }

    searchButton.setOnClickListener(::refreshSongListView)
  }

  private fun refreshSongListView(v: View) {
    songListView.adapter = SongRecyclerViewAdapter(getSongList(contentResolver))
  }


  private fun onTimerTick() {
    runOnUiThread {
      timer.apply {
        println("onTimerTick, timeTotalSeconds(): ${timeTotalSeconds()}, timeString: ${timeString()}, player.currentPosition: ${player.currentPosition}")
        seekBar.progress = timeTotalSeconds()
        currentTimeView.text = timeString()
      }
    }
  }

  private fun onTimerStop() {
    runOnUiThread {
      timer.apply {
        seekBar.progress = 0
        currentTimeView.text = "00:00"
        println("onTimerStop, timeTotalSeconds(): ${timeTotalSeconds()}, timeString: ${timeString()}")
      }
    }
  }
}
