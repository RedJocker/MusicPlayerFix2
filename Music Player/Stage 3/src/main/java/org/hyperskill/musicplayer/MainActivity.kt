package org.hyperskill.musicplayer

import android.Manifest
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
  lateinit var player: MediaPlayer
  lateinit var timer: Timer
  lateinit var seekBar: SeekBar
  lateinit var currentTimeView: TextView
  lateinit var totalTimeView: TextView
  lateinit var searchButton: Button
  lateinit var playPauseButton: Button
  lateinit var stopButton: Button

  data class Song(val title: String, val artist: String, val id: Long, val duration: Long)

  val songList = mutableListOf<Song>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    askPermission()
    bindViews()

    player = MediaPlayer.create(this, R.raw.wisdom)
    timer = Timer(player, ::onTimerTick, ::onTimerStop)
    totalTimeView.text = Timer.timeString(player.duration)
    seekBar.max = player.duration / 1000

    initListeners()
  }

  private fun bindViews() {
    seekBar = findViewById(R.id.seekBar)
    totalTimeView = findViewById(R.id.totalTimeTv)
    currentTimeView = findViewById(R.id.currentTimeTv)
    searchButton = findViewById(R.id.searchButton)
    playPauseButton = findViewById(R.id.playPauseButton)
    stopButton = findViewById(R.id.stopButton)

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

    searchButton.setOnClickListener {
      updateSongListFromStorage()
      placeSongFragmentsOnScreen(songList)
    }
  }

  private fun updateSongListFromStorage() {
    songList.clear()
    val musicResolver = contentResolver
    val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    val musicCursor = musicResolver.query(musicUri, null, null,
      null, null, null)

    musicCursor?.use {
      if (it.moveToFirst()) {
        val titleColumn = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
        val artistColumn = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
        val idColumn = it.getColumnIndex(MediaStore.Audio.Media._ID)
        val durationColumn = it.getColumnIndex(MediaStore.Audio.Media.DURATION)

        do {
          val title = it.getString(titleColumn)
          val artist = it.getString(artistColumn)
          val id = it.getLong(idColumn)
          val duration  = it.getLong(durationColumn)
          songList.add(Song(title, artist, id, duration))
        } while (it.moveToNext())
      }
    }
  }

  private fun placeSongFragmentsOnScreen(songList: MutableList<Song>) {
    val fragmentTransaction = supportFragmentManager.beginTransaction()
    for ((i, song) in songList.withIndex()) {
      val songFragment = SongFragment(song.title, song.artist, song.id, i, this)
      fragmentTransaction.add(R.id.songList, songFragment)
    }

    fragmentTransaction.commit()
  }

  private fun askPermission() {
    ActivityCompat.requestPermissions(this,
      arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE),
      101)
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
