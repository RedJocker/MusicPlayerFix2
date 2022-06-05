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


  var playerIsEmpty = true
  var currentSongStatusButton: ImageButton? = null
  var currentPlayingSongPosition: Int? = null


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    println("onCreate stage4")

    bindViews()
    setInitialState()
    initListeners()
  }

  private fun setInitialState() {
    player = MediaPlayer.create(this, R.raw.wisdom)
    timer = Timer(player, ::onTimerTick, ::onTimerStop)
    totalTimeView.text = Timer.timeString(player.duration)
    seekBar.max = player.duration / 1000
    songListView.adapter = SongRecyclerViewAdapter(this, listOf())
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

  private fun playNewSong(context: MainActivity) {
    if (!playerIsEmpty) {
      player.stop()
      player.reset()
      timer.stop()
    }

    val currentSongResourceId = getSongListFromStorage(contentResolver)[currentPlayingSongPosition!!].id
    val trackUri = ContentUris.withAppendedId(
      android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSongResourceId)
    player = MediaPlayer.create(context, trackUri)
//    player.setOnCompletionListener(this)
    player.start()
    println("playing new: ${player.isPlaying}")
    playerIsEmpty = false
    context.seekBar.max = player.duration / 1000
    timer = Timer(player, ::onTimerTick, ::onTimerStop)
    context.totalTimeView.text = Timer.timeString(player.duration)
    timer.start()
  }

  fun onPlayPauseSongButtonClick(songPositionInList: Int, activity: MainActivity,
                                 status: ImageButton
  ) {
    if (playerIsEmpty) {
      currentSongStatusButton = status
    }
    if (currentPlayingSongPosition != songPositionInList) {
      currentSongStatusButton!!.setImageResource(R.drawable.ic_action_play)
      currentPlayingSongPosition = songPositionInList
      status.setImageResource(R.drawable.ic_action_pause)

      playNewSong(activity)
    } else if (player.isPlaying) {
      status.setImageResource(R.drawable.ic_action_play)
//      playPauseButton.setImageResource(R.drawable.ic_action_play)
      pause()
    } else {
      status.setImageResource(R.drawable.ic_action_pause)
//      playPauseButton.setImageResource(R.drawable.ic_action_pause)
      resume()
    }
  }

  private fun refreshSongListView(v: View) {
    if(hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
      songListView.adapter = SongRecyclerViewAdapter(this, getSongListFromStorage(contentResolver))
    } else {
      askPermission()
    }
  }

  private fun pause() {
    player.pause()
    println("paused: ${player.isPlaying}")
    timer.stop()
  }
  private fun resume() {
    player.start()
    println("resumed: ${player.isPlaying}")
    timer.start()
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
