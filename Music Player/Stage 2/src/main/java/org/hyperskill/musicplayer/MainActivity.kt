package org.hyperskill.musicplayer

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.SeekBar
import org.hyperskill.musicplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), MusicPlayable {

  override lateinit var player: MediaPlayer
  lateinit var timer: Timer

  private val binding by lazy {
    ActivityMainBinding.inflate(layoutInflater)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)


    player = MediaPlayer.create(this, R.raw.wisdom)
    timer = Timer(player, ::onTimerTick , ::onTimerStop)

    binding.totalTimeTv.text = Timer.timeString(player.duration)
    binding.seekBar.max = player.duration / 1000

    bindListeners()
  }

  private fun bindListeners() {
    binding.apply {

      playPauseButton.setOnClickListener {
        if (player.isPlaying) {
          player.pause()
          timer.pause()
        } else {
          player.start()
          timer.start()
        }
      }

      stopButton.setOnClickListener {
        player.seekTo(0)
        player.stop()
        player.prepare()
        timer.stop()
      }

      seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
          if(fromUser) {
            println("fromUser onProgressChanged()")
            binding.currentTimeTv.text = Timer.timeString(progress * 1000)
          } // else {
//            println("not fromUser onProgressChanged()")
//          }
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {
          println("onStartTrackingTouch")
          timer.pause()
        }
        override fun onStopTrackingTouch(seekBar: SeekBar?) {
          player.seekTo(binding.seekBar.progress * 1000)
          if(player.isPlaying) {
            timer.start()
          }
          println("onStopTrackingTouch, currentPosition: ${player.currentPosition}")
        }
      })

      player.setOnCompletionListener {
        it.seekTo(0)
        it.prepare()
        println("onCompletionListener, isPlaying: ${it.isPlaying}, currentPosition: ${it.currentPosition}")
        timer.stop()
      }
    }
  }


  private fun onTimerTick() {
    runOnUiThread {
      timer.apply {
        println("onTimerTick, timeTotalSeconds(): ${timeTotalSeconds()}, timeString: ${timeString()}, player.currentPosition: ${player.currentPosition}")
        binding.seekBar.progress = timeTotalSeconds()
        binding.currentTimeTv.text = timeString()
      }
    }
  }

  private fun onTimerStop() {
    runOnUiThread {
      timer.apply {
        binding.seekBar.progress = 0
        binding.currentTimeTv.text = "00:00"
        println("onTimerStop, timeTotalSeconds(): ${timeTotalSeconds()}, timeString: ${timeString()}")
      }
    }
  }
}
