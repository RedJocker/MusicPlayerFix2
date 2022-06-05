package org.hyperskill.musicplayer

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
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


  var currentSongStatusButton: ImageButton? = null
  var currentPlayingSongPosition: Int? = null


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
//    println("onCreate stage4")

    bindViews()
    setInitialState()
    initListeners()
  }

  private fun setInitialState() {
    player = MediaPlayer.create(this, R.raw.wisdom)
    timer = Timer(player, ::onTimerTick, ::onTimerStop)
    totalTimeView.text = Timer.timeString(player.duration)
    seekBar.max = player.duration / 1000
    songListView.adapter = SongRecyclerViewAdapter(listOf(), ::onListItemClick)
  }

  private fun bindViews() {
    seekBar = findViewById(R.id.seekBar)
    totalTimeView = findViewById(R.id.totalTimeTv)
    currentTimeView = findViewById(R.id.currentTimeTv)
    playPauseButton = findViewById(R.id.playPauseButton)
    searchButton = findViewById(R.id.searchButton)
    playPauseButton = findViewById(R.id.playPauseButton)
    stopButton = findViewById(R.id.stopButton)
    songListView = findViewById(R.id.songList)
  }

  private fun initListeners() {
    playPauseButton.setOnClickListener(::playPause)

    stopButton.setOnClickListener(::stop)

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

    player.setOnCompletionListener(::onCompletionListener)

    searchButton.setOnClickListener(::refreshSongListView)
  }

  private fun playPause(playPauseButton: View = this.playPauseButton) {
    if (player.isPlaying) {
      player.pause()
      timer.pause()
      println("playPause, paused")
      currentSongStatusButton?.setImageResource(R.drawable.ic_action_play)
    } else {
      player.start()
      timer.start()
      println("playPause, playing")
      currentSongStatusButton?.setImageResource(R.drawable.ic_action_pause)
    }
  }

  private fun onCompletionListener(mediaPlayer: MediaPlayer) {
    stop()
  }

  private fun playNewSong(songId: Long) {
    if (player.isPlaying) {
      stop()
    }

    val trackUri = ContentUris.withAppendedId(
      android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId)

    player = MediaPlayer.create(this, trackUri)
    player.setOnCompletionListener(::onCompletionListener)
    timer = Timer(player, ::onTimerTick, ::onTimerStop)
    seekBar.max = player.duration / 1000
    totalTimeView.text = Timer.timeString(player.duration)

    playPause()
    println("playing new: ${player.isPlaying}")

  }

  private fun onListItemClick(songPositionInList: Int, status: ImageButton, songId: Long) {
    if (currentPlayingSongPosition != songPositionInList) {
      currentSongStatusButton?.setImageResource(R.drawable.ic_action_play)
      status.setImageResource(R.drawable.ic_action_pause)

      currentSongStatusButton = status
      currentPlayingSongPosition = songPositionInList

      playNewSong(songId)
    } else {
      playPause()
    }
  }

  private fun refreshSongListView(v: View) {
    if(hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
      songListView.adapter = SongRecyclerViewAdapter(getSongListFromStorage(contentResolver), ::onListItemClick)
    } else {
      askPermission()
    }
  }

  private fun stop(stopButton: View = this.stopButton) {
    player.seekTo(0)
    player.stop()
    player.prepare()
    timer.stop()
    currentSongStatusButton?.setImageResource(R.drawable.ic_action_play)
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

  override fun onRequestPermissionsResult(
    code: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(code, permissions, grantResults)

    if(code == requestCode) {
      grantResults.forEachIndexed { index: Int, result: Int ->
        if (result == PackageManager.PERMISSION_GRANTED) {
          Log.d("PermissionRequest", "${permissions[index]} granted")
          if(permissions[index] == Manifest.permission.READ_EXTERNAL_STORAGE) {
            searchButton.callOnClick()
          }
        } else {
          Log.d("PermissionRequest", "${permissions[index]} denied")
        }
      }
    }
  }
}
