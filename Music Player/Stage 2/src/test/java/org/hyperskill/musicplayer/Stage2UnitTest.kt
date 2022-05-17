package org.hyperskill.musicplayer

import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Duration



@RunWith(RobolectricTestRunner::class)
@Config(shadows = [CustomMediaPlayerShadow::class])
class Stage2UnitTest : AbstractUnitTest<MainActivity>(MainActivity::class.java){

    private val playPauseButton: ImageButton by lazy {
        activity.findViewByString("playPauseButton")
    }

    private val stopButton: ImageButton by lazy {
        activity.findViewByString("stopButton")
    }

    private val songArtistTv: TextView by lazy {
        activity.findViewByString("songArtist")
    }

    private val songTitleTv: TextView by lazy {
        activity.findViewByString("songTitle")
    }

    private val currentTimeTv: TextView by lazy {
        val view = activity.findViewByString<TextView>("currentTimeTv")
        assertEquals(
            "Incorrect initial text value for currentTimeTv",
            "00:00",
            view.text.toString()
        )
        view
    }

    private val totalTimeTv: TextView by lazy {
        val view = activity.findViewByString<TextView>("totalTimeTv")
        assertEquals(
            "Incorrect initial text value for totalTimeTv",
            "03:30",
            view.text.toString()
        )
        view
    }

    private val seekBar: SeekBar by lazy {
        val view = activity.findViewByString<SeekBar>("seekBar")
        assertEquals("Incorrect min progress value for seekBar", 0, view.min)
        assertEquals("Incorrect max progress value for seekBar", 210, view.max)
        assertEquals("Incorrect initial progress value for seekBar", 0, view.progress)
        view
    }

    @Test
    fun checkCurrentTimeTvExist() {
        testActivity {
            currentTimeTv
        }
    }

    @Test
    fun checkTotalTimeTvExist() {
        testActivity {
            totalTimeTv
        }
    }

    @Test
    fun checkSeekBarExist() {
        testActivity {
            seekBar
        }
    }

    @Test
    fun checkStateAfterPlay() {
        testActivity {
            currentTimeTv
            totalTimeTv
            seekBar
            playPauseButton

            playPauseButton.clickAndRun(3100)
            // total time elapsed = 3100 milliseconds

            val messageCurrentTimeAfterPlay =
                "The currentTimeTv should display the playing time of the song after clicking playPauseButton"
            val expectedCurrentTimeAfterPlay = "00:03"
            val actualCurrentTimeAfterPlay =  currentTimeTv.text.toString()
            assertEquals(messageCurrentTimeAfterPlay, expectedCurrentTimeAfterPlay, actualCurrentTimeAfterPlay)

            val messageSeekBarAfterPlay =
                "The seekBar position should track the current position of the song after clicking playPauseButton"
            val expectedSeekBarAfterPlay = 3
            val actualSeekBarAfterPlay = seekBar.progress
            assertEquals(messageSeekBarAfterPlay, expectedSeekBarAfterPlay, actualSeekBarAfterPlay)

            val messageTotalTimeAfterPlay =
                "The totalTimeTv should not change the total time of the song after clicking playPauseButton"
            val expectedTotalTimeAfterPlay = "03:30"
            val actualTotalTimeAfterPlay =  totalTimeTv.text.toString()
            assertEquals(messageTotalTimeAfterPlay, expectedTotalTimeAfterPlay, actualTotalTimeAfterPlay)

            shadowLooper.idleFor(Duration.ofMillis(60_100))
            // total time elapsed = 63_200 milliseconds

            val messageCurrentTimeAfterMinute =
                "The currentTimeTv should display the playing time of the song in \"clock format\""
            val expectedCurrentTimeAfterMinute = "01:03"
            val actualCurrentTimeAfterMinute =  currentTimeTv.text.toString()
            assertEquals(messageCurrentTimeAfterMinute, expectedCurrentTimeAfterMinute, actualCurrentTimeAfterMinute)

            val messageSeekBarAfterMinute =
                "The seekBar position should keep tracking the current position of the song after " +
                        "minute playing"
            val expectedSeekBarAfterMinute = 63
            val actualSeekBarAfterMinute = seekBar.progress
            assertEquals(messageSeekBarAfterMinute, expectedSeekBarAfterMinute, actualSeekBarAfterMinute)

            val messageTotalTimeAfterMinute =
                "The totalTimeTv should not change the total time of the song after minute playing"
            val expectedTotalTimeAfterMinute = "03:30"
            val actualTotalTimeAfterMinute =  totalTimeTv.text.toString()
            assertEquals(messageTotalTimeAfterMinute, expectedTotalTimeAfterMinute, actualTotalTimeAfterMinute)

            shadowLooper.idleFor(Duration.ofMillis(57_100))
            // total time elapsed = 120_300 milliseconds

            val messageCurrentTimeAfterTwoMinutesOnClock =
                "The currentTimeTv should display the playing time of the song in \"clock format\""
            val expectedCurrentTimeAfterTwoMinutesOnClock = "02:00"
            val actualCurrentTimeAfterTwoMinutesOnClock =  currentTimeTv.text.toString()
            assertEquals(
                messageCurrentTimeAfterTwoMinutesOnClock,
                expectedCurrentTimeAfterTwoMinutesOnClock,
                actualCurrentTimeAfterTwoMinutesOnClock
            )

            val messageSeekBarAfterTwoMinutes =
                "The seekBar position should keep tracking the current position of the song after" +
                        " minute playing"
            val expectedSeekBarAfterTwoMinutes = 120
            val actualSeekBarAfterTwoMinutes = seekBar.progress
            assertEquals(
                messageSeekBarAfterTwoMinutes,
                expectedSeekBarAfterTwoMinutes,
                actualSeekBarAfterTwoMinutes
            )
        }

    }

    @Test
    fun checkStateAfterPauseAndResume() {
        testActivity {
            currentTimeTv
            seekBar
            playPauseButton

            playPauseButton.clickAndRun(10_100)
            // total time elapsed = 10_100 milliseconds

            val messageCurrentTimeAfterPlay =
                "The currentTimeTv should display the playing time of the song after " +
                        "clicking playPauseButton"
            val expectedCurrentTimeAfterPlay = "00:10"
            val actualCurrentTimeAfterPlay =  currentTimeTv.text.toString()
            assertEquals(messageCurrentTimeAfterPlay, expectedCurrentTimeAfterPlay, actualCurrentTimeAfterPlay)

            val messageSeekBarAfterPlay =
                "The seekBar position should track the current position of the song after " +
                        "clicking playPauseButton"
            val expectedSeekBarAfterPlay = 10
            val actualSeekBarAfterPlay = seekBar.progress
            assertEquals(messageSeekBarAfterPlay, expectedSeekBarAfterPlay, actualSeekBarAfterPlay)

            playPauseButton.clickAndRun(10_000)

            val messageCurrentTimeAfterPause =
                "The currentTimeTv should remain paused after " +
                        "playPauseButton click while the song was playing"
            val expectedCurrentTimeAfterPause = "00:10"
            val actualCurrentTimeAfterPause =  currentTimeTv.text.toString()
            assertEquals(
                messageCurrentTimeAfterPause,
                expectedCurrentTimeAfterPause,
                actualCurrentTimeAfterPause)

            val messageSeekBarAfterPause =
                "The seekBar position should remain paused after" +
                        " playPauseButton click while the song was playing"
            val expectedSeekBarAfterPause = 10
            val actualSeekBarAfterPause = seekBar.progress
            assertEquals(messageSeekBarAfterPause, expectedSeekBarAfterPause, actualSeekBarAfterPause)

            playPauseButton.clickAndRun(10_100)
            // total time elapsed = 20_200

            val messageCurrentTimeAfterResume =
                "The currentTimeTv should resume displaying current time after " +
                        "playPauseButton click while the song was paused"
            val expectedCurrentTimeAfterResume = "00:20"
            val actualCurrentTimeAfterResume =  currentTimeTv.text.toString()
            assertEquals(
                messageCurrentTimeAfterResume,
                expectedCurrentTimeAfterResume,
                actualCurrentTimeAfterResume
            )

            val messageSeekBarAfterResume =
                "The seekBar position should resume tracking the current position after " +
                        "playPauseButton click while the song was paused"
            val expectedSeekBarAfterResume = 20
            val actualSeekBarAfterResume = seekBar.progress
            assertEquals(messageSeekBarAfterResume, expectedSeekBarAfterResume, actualSeekBarAfterResume)
        }
    }

    @Test
    fun checkStateAfterStop() {
        testActivity {
            activity
            playPauseButton
            stopButton
            currentTimeTv
            seekBar
            totalTimeTv

            playPauseButton.clickAndRun(119_200)
            // total time elapsed = 119_200 milliseconds

            val messageCurrentTimeAfterPlay =
                "The currentTimeTv should display the playing time of the song after clicking playPauseButton"
            val expectedCurrentTimeAfterPlay = "01:59"
            val actualCurrentTimeAfterPlay =  currentTimeTv.text.toString()
            assertEquals(messageCurrentTimeAfterPlay, expectedCurrentTimeAfterPlay, actualCurrentTimeAfterPlay)

            val messageSeekBarAfterPlay =
                "The seekBar position should track the current position of the song after clicking playPauseButton"
            val expectedSeekBarAfterPlay = 119
            val actualSeekBarAfterPlay = seekBar.progress
            assertEquals(messageSeekBarAfterPlay, expectedSeekBarAfterPlay, actualSeekBarAfterPlay)

            stopButton.clickAndRun()
            // total time elapsed = 0 milliseconds

            val messageCurrentTimeAfterStop =
                "The currentTimeTv should reset the playing time of the song after" +
                        " clicking stopButton"
            val expectedCurrentTimeAfterStop = "00:00"
            val actualCurrentTimeAfterStop =  currentTimeTv.text.toString()
            assertEquals(messageCurrentTimeAfterStop, expectedCurrentTimeAfterStop, actualCurrentTimeAfterStop)

            val messageSeekBarAfterStop =
                "The seekBar position should reset the current position of the song after" +
                        " clicking stopButton"
            val expectedSeekBarAfterStop = 0
            val actualSeekBarAfterStop = seekBar.progress
            assertEquals(messageSeekBarAfterStop, expectedSeekBarAfterStop, actualSeekBarAfterStop)

            val messageTotalTimeAfterMinute =
                "The totalTimeTv should not change the total time of the song after clicking stopButton"
            val expectedTotalTimeAfterMinute = "03:30"
            val actualTotalTimeAfterMinute =  totalTimeTv.text.toString()
            assertEquals(
                messageTotalTimeAfterMinute,
                expectedTotalTimeAfterMinute,
                actualTotalTimeAfterMinute
            )

            playPauseButton.clickAndRun(2_100)
            // total time elapsed = 2_100 milliseconds

            val messageCurrentTimePlayAfterStop =
                "The currentTimeTv should display the current time of the song after" +
                        " clicking playPauseButton after the song was stopped"
            val expectedCurrentTimePlayAfterStop = "00:02"
            val actualCurrentTimePlayAfterStop =  currentTimeTv.text.toString()
            assertEquals(
                messageCurrentTimePlayAfterStop,
                expectedCurrentTimePlayAfterStop,
                actualCurrentTimePlayAfterStop
            )

            val messageSeekBarPlayAfterStop =
                "The seekBar position should reset the current position of the song after" +
                        " clicking stopButton"
            val expectedSeekBarPlayAfterStop = 2
            val actualSeekBarPlayAfterStop = seekBar.progress
            assertEquals(
                messageSeekBarPlayAfterStop,
                expectedSeekBarPlayAfterStop,
                actualSeekBarPlayAfterStop
            )
        }

    }

    @Test
    fun checkStateOnMusicEnd() {
        testActivity {
            currentTimeTv
            totalTimeTv
            seekBar
            playPauseButton

            playPauseButton.clickAndRun(20_100)
            // total time elapsed = 20_100 milliseconds

            val messageCurrentTimeAfterPlay =
                "The currentTimeTv should display the playing time of the song after" +
                        " clicking playPauseButton"
            val expectedCurrentTimeAfterPlay = "00:20"
            val actualCurrentTimeAfterPlay =  currentTimeTv.text.toString()
            assertEquals(messageCurrentTimeAfterPlay, expectedCurrentTimeAfterPlay, actualCurrentTimeAfterPlay)

            val messageSeekBarAfterPlay =
                "The seekBar position should track the current position of the song after" +
                        " clicking playPauseButton"
            val expectedSeekBarAfterPlay = 20
            val actualSeekBarAfterPlay = seekBar.progress
            assertEquals(messageSeekBarAfterPlay, expectedSeekBarAfterPlay, actualSeekBarAfterPlay)


            shadowLooper.idleFor(Duration.ofMillis(189_200))
            // total time elapsed = 209_300 milliseconds


            val messageCurrentTimeNearMusicEnd =
                "The currentTimeTv should display the playing time of the song in \"clock format\""
            val expectedCurrentTimeNearMusicEnd = "03:29"
            val actualCurrentTimeNearMusicEnd =  currentTimeTv.text.toString()
            assertEquals(
                messageCurrentTimeNearMusicEnd,
                expectedCurrentTimeNearMusicEnd,
                actualCurrentTimeNearMusicEnd
            )

            val messageSeekBarNearMusicEnd =
                "The seekBar position should keep tracking the current position of the song after" +
                        " minute playing"
            val expectedSeekBarNearMusicEnd = 209
            val actualSeekBarNearMusicEnd = seekBar.progress
            assertEquals(
                messageSeekBarNearMusicEnd,
                expectedSeekBarNearMusicEnd,
                actualSeekBarNearMusicEnd
            )

            shadowLooper.idleFor(Duration.ofMillis(10_000))
            // total time elapsed = 219_300 milliseconds

            val messageCurrentTimeAfterMusicEnd =
                "The currentTimeTv should reset time displayed after music end"
            val expectedCurrentTimeAfterMusicEnd = "00:00"
            val actualCurrentTimeAfterMusicEnd =  currentTimeTv.text.toString()
            assertEquals(messageCurrentTimeAfterMusicEnd,
                expectedCurrentTimeAfterMusicEnd,
                actualCurrentTimeAfterMusicEnd
            )

            val messageSeekBarAfterMusicEnd =
                "The seekBar should reset position after music end"
            val expectedSeekBarAfterMusicEnd = 0
            val actualSeekBarAfterMusicEnd = seekBar.progress
            assertEquals(
                messageSeekBarAfterMusicEnd,
                expectedSeekBarAfterMusicEnd,
                actualSeekBarAfterMusicEnd
            )
        }
    }

    @Test
    fun checkStateAfterSeekBarChangeDuringPlay() {
        testActivity {
            player
            currentTimeTv
            seekBar
            playPauseButton

            totalTimeTv

            playPauseButton.clickAndRun(30_100)
            // total time elapsed = 30_100 milliseconds

            val messageCurrentTimeAfterPlay =
                "The currentTimeTv should display the playing time of the song after" +
                        " clicking playPauseButton"
            val expectedCurrentTimeAfterPlay = "00:30"
            val actualCurrentTimeAfterPlay =  currentTimeTv.text.toString()
            assertEquals(messageCurrentTimeAfterPlay, expectedCurrentTimeAfterPlay, actualCurrentTimeAfterPlay)

            val messageSeekBarAfterPlay =
                "The seekBar position should track the current position of the song after" +
                        " clicking playPauseButton"
            val expectedSeekBarAfterPlay = 30
            val actualSeekBarAfterPlay = seekBar.progress
            assertEquals(messageSeekBarAfterPlay, expectedSeekBarAfterPlay, actualSeekBarAfterPlay)

            seekBar.setProgressAsUser(120)
            shadowLooper.idleFor(Duration.ofMillis(10_100))
            // total time elapsed = 130_100 millis

            val messagePlayerPlayingAfterSeekBarChange =
                "The player should remain playing after user changes the seekBar position if it was playing"
            val expectedPlayerPlayingAfterSeekBarChange = true
            val actualPlayerPlayingAfterSeekBarChange = player.isPlaying
            assertEquals(
                messagePlayerPlayingAfterSeekBarChange,
                expectedPlayerPlayingAfterSeekBarChange,
                actualPlayerPlayingAfterSeekBarChange
            )

            val messagePlayerPositionAfterSeekBarChange =
                "The player should change the  current position after user changes the seekBar position"
            val expectedPlayerPositionAfterSeekBarChange = 130100
            val actualPlayerPositionAfterSeekBarChange = player.currentPosition
            assertEquals(
                messagePlayerPositionAfterSeekBarChange,
                expectedPlayerPositionAfterSeekBarChange,
                actualPlayerPositionAfterSeekBarChange
            )

            val messageCurrentTimeAfterSeekbarChange =
                "The currentTimeTv should keep tracking the current time of the song after" +
                        " user changes the seekBar position"
            val expectedCurrentTimeAfterSeekBarChange = "02:10"
            val actualCurrentTimeAfterSeekBarChange =  currentTimeTv.text.toString()
            assertEquals(
                messageCurrentTimeAfterSeekbarChange,
                expectedCurrentTimeAfterSeekBarChange,
                actualCurrentTimeAfterSeekBarChange
            )

            val messageSeekBarAfterSeekBarChange =
                "The seekBar position should keep tracking the current position of the song after" +
                        " user changes the seekBar position"
            val expectedSeekBarAfterSeekBarChange = 130
            val actualSeekBarAfterSeekBarChange = seekBar.progress
            assertEquals(messageSeekBarAfterSeekBarChange, expectedSeekBarAfterSeekBarChange, actualSeekBarAfterSeekBarChange)


            val messageTotalTimeAfterSeekBarChange =
                "The totalTimeTv should not change the total time of the song after" +
                        " user changes the seekBar position"
            val expectedTotalTimeAfterSeekBarChange = "03:30"
            val actualTotalTimeAfterSeekBarChange =  totalTimeTv.text.toString()
            assertEquals(
                messageTotalTimeAfterSeekBarChange,
                expectedTotalTimeAfterSeekBarChange,
                actualTotalTimeAfterSeekBarChange
            )
        }
    }

    @Test
    fun checkStateAfterSeekBarChangeDuringStop() {
        testActivity {
            player
            currentTimeTv
            totalTimeTv
            seekBar
            playPauseButton
            stopButton


            playPauseButton.clickAndRun(40_100)
            // total time elapsed = 40_100 milliseconds

            val messageCurrentTimeAfterPlay =
                "The currentTimeTv should display the playing time of the song after" +
                        " clicking playPauseButton"
            val expectedCurrentTimeAfterPlay = "00:40"
            val actualCurrentTimeAfterPlay =  currentTimeTv.text.toString()
            assertEquals(
                messageCurrentTimeAfterPlay,
                expectedCurrentTimeAfterPlay,
                actualCurrentTimeAfterPlay
            )

            val messageSeekBarAfterPlay =
                "The seekBar position should track the current position of the song after" +
                        " clicking playPauseButton"
            val expectedSeekBarAfterPlay = 40
            val actualSeekBarAfterPlay = seekBar.progress
            assertEquals(messageSeekBarAfterPlay, expectedSeekBarAfterPlay, actualSeekBarAfterPlay)

            stopButton.clickAndRun(0)
            seekBar.setProgressAsUser(120)
            shadowLooper.idleFor(Duration.ofMillis(10_100))
            // total time elapsed = 120_000 millis

            val messagePlayerStoppedAfterStopSeekBarChange =
                "The player should remain stopped after user changes the seekBar position if it was stopped"
            val expectedPlayerStoppedAfterStopSeekBarChange = false
            val actualPlayerStoppedAfterStopSeekBarChange = player.isPlaying
            assertEquals(
                messagePlayerStoppedAfterStopSeekBarChange,
                expectedPlayerStoppedAfterStopSeekBarChange,
                actualPlayerStoppedAfterStopSeekBarChange
            )

            val messagePlayerPositionAfterStopSeekBarChange =
                "The player should change the current position after user changes the seekBar position"
            val expectedPlayerPositionAfterStopSeekBarChange = 120_000
            val actualPlayerPositionAfterStopSeekBarChange = player.currentPosition
            assertEquals(
                messagePlayerPositionAfterStopSeekBarChange,
                expectedPlayerPositionAfterStopSeekBarChange,
                actualPlayerPositionAfterStopSeekBarChange
            )


            val messageCurrentTimeAfterStopSeekbarChange =
                "The currentTimeTv should keep tracking the current time of the song after" +
                        " user changes the seekBar position"
            val expectedCurrentTimeAfterStopSeekBarChange = "02:00"
            val actualCurrentTimeAfterStopSeekBarChange =  currentTimeTv.text.toString()
            assertEquals(
                messageCurrentTimeAfterStopSeekbarChange,
                expectedCurrentTimeAfterStopSeekBarChange,
                actualCurrentTimeAfterStopSeekBarChange
            )

            val messageSeekBarAfterSeekBarChange =
                "The seekBar position should keep tracking the current position of the song after" +
                        " user changes the seekBar position"
            val expectedSeekBarAfterSeekBarChange = 120
            val actualSeekBarAfterSeekBarChange = seekBar.progress
            assertEquals(
                messageSeekBarAfterSeekBarChange,
                expectedSeekBarAfterSeekBarChange,
                actualSeekBarAfterSeekBarChange
            )

            val messageTotalTimeAfterSeekBarChange =
                "The totalTimeTv should not change the total time of the song after" +
                        " user changes the seekBar position"
            val expectedTotalTimeAfterSeekBarChange = "03:30"
            val actualTotalTimeAfterSeekBarChange =  totalTimeTv.text.toString()
            assertEquals(
                messageTotalTimeAfterSeekBarChange,
                expectedTotalTimeAfterSeekBarChange,
                actualTotalTimeAfterSeekBarChange
            )

            playPauseButton.clickAndRun(10_100)
            // total time elapsed = 130_100 millis

            val messagePlayerPlayingAfterPlayAgain =
                "The player should play again after click on playPauseButton if it was stopped"
            val expectedPlayerPlayingAfterPlayAgain = true
            val actualPlayerPlayingAfterPlayAgain = player.isPlaying
            assertEquals(
                messagePlayerPlayingAfterPlayAgain,
                expectedPlayerPlayingAfterPlayAgain,
                actualPlayerPlayingAfterPlayAgain
            )

            val messagePlayerPositionAfterPlayAgain =
                "The player should change the current position after user changes the seekBar position"
            val expectedPlayerPositionAfterPlayAgain = 130_100
            val actualPlayerPositionAfterPlayAgain = player.currentPosition
            assertEquals(
                messagePlayerPositionAfterPlayAgain,
                expectedPlayerPositionAfterPlayAgain,
                actualPlayerPositionAfterPlayAgain
            )

            val messageCurrentTimeAfterPlayAgain =
                "The currentTimeTv should keep tracking the current time of the song after" +
                        " user changes the seekBar position"
            val expectedCurrentTimeAfterPlayAgain = "02:10"
            val actualCurrentTimeAfterPlayAgain =  currentTimeTv.text.toString()
            assertEquals(
                messageCurrentTimeAfterPlayAgain,
                expectedCurrentTimeAfterPlayAgain,
                actualCurrentTimeAfterPlayAgain
            )

            val messageSeekBarAfterPlayAgain =
                "The seekBar position should keep tracking the current position of the song after" +
                        " user changes the seekBar position"
            val expectedSeekBarAfterPlayAgain = 130
            val actualSeekBarAfterPlayAgain = seekBar.progress
            assertEquals(
                messageSeekBarAfterPlayAgain,
                expectedSeekBarAfterPlayAgain,
                actualSeekBarAfterPlayAgain
            )
        }
    }


    @Test
    fun checkStateAfterSeekBarChangeBeforePlaying() {
        testActivity {
            player
            currentTimeTv
            totalTimeTv
            seekBar
            playPauseButton


            seekBar.setProgressAsUser(150)
            shadowLooper.idleFor(Duration.ofMillis(10_100))
            // total time elapsed = 150_000 millis

            val messagePlayerBeforePlayingAfterSeekBarChange =
                "The player should remain stopped after user changes the seekBar position if it was stopped"
            val expectedPlayerBeforePlayingAfterSeekBarChange = false
            val actualPlayerBeforePlayingAfterSeekBarChange = player.isPlaying
            assertEquals(
                messagePlayerBeforePlayingAfterSeekBarChange,
                expectedPlayerBeforePlayingAfterSeekBarChange,
                actualPlayerBeforePlayingAfterSeekBarChange
            )

            val messagePlayerPositionBeforePlayingAfterSeekBarChange =
                "The player should change the current position after user changes the seekBar position"
            val expectedPlayerPositionAfterStopSeekBarChange = 150_000
            val actualPlayerPositionAfterStopSeekBarChange = player.currentPosition
            assertEquals(
                messagePlayerPositionBeforePlayingAfterSeekBarChange,
                expectedPlayerPositionAfterStopSeekBarChange,
                actualPlayerPositionAfterStopSeekBarChange
            )

            val messageCurrentTimeBeforePlayingAfterSeekBarChange =
                "The currentTimeTv should keep tracking the current time of the song after" +
                        " user changes the seekBar position"
            val expectedCurrentTimeBeforePlayingAfterSeekBarChange = "02:30"
            val actualCurrentTimeBeforePlayingAfterSeekBarChange =  currentTimeTv.text.toString()
            assertEquals(
                messageCurrentTimeBeforePlayingAfterSeekBarChange,
                expectedCurrentTimeBeforePlayingAfterSeekBarChange,
                actualCurrentTimeBeforePlayingAfterSeekBarChange
            )

            val messageSeekBarBeforePlayingAfterSeekBarChange =
                "The seekBar position should keep tracking the current position of the song after" +
                        " user changes the seekBar position"
            val expectedSeekBarBeforePlayingAfterSeekBarChange = 150
            val actualSeekBarBeforePlayingAfterSeekBarChange = seekBar.progress
            assertEquals(
                messageSeekBarBeforePlayingAfterSeekBarChange,
                expectedSeekBarBeforePlayingAfterSeekBarChange,
                actualSeekBarBeforePlayingAfterSeekBarChange
            )

            val messageTotalTimeBeforePlayingAfterSeekBarChange =
                "The totalTimeTv should not change the total time of the song after" +
                        " user changes the seekBar position"
            val expectedTotalTimeBeforePlayingAfterSeekBarChange = "03:30"
            val actualTotalTimeBeforePlayingAfterSeekBarChange =  totalTimeTv.text.toString()
            assertEquals(
                messageTotalTimeBeforePlayingAfterSeekBarChange,
                expectedTotalTimeBeforePlayingAfterSeekBarChange,
                actualTotalTimeBeforePlayingAfterSeekBarChange
            )

            playPauseButton.clickAndRun(20_100)
            // total time elapsed = 170_100 millis

            val messagePlayerPlayingAfterPlay =
                "The player should be able to start a song for the first time after seekBar changes"
            val expectedPlayerPlayingAfterPlay = true
            val actualPlayerPlayingAfterPlay = player.isPlaying
            assertEquals(
                messagePlayerPlayingAfterPlay,
                expectedPlayerPlayingAfterPlay,
                actualPlayerPlayingAfterPlay
            )

            val messagePlayerPositionAfterPlay =
                "The player should change the current position after user changes the seekBar position"
            val expectedPlayerPositionAfterPlay = 170_100
            val actualPlayerPositionAfterPlay = player.currentPosition
            assertEquals(
                messagePlayerPositionAfterPlay,
                expectedPlayerPositionAfterPlay,
                actualPlayerPositionAfterPlay
            )

            val messageCurrentTimeAfterPlay =
                "The currentTimeTv should keep tracking the current time of the song after" +
                        " user changes the seekBar position"
            val expectedCurrentTimeAfterPlay = "02:50"
            val actualCurrentTimeAfterPlay =  currentTimeTv.text.toString()
            assertEquals(
                messageCurrentTimeAfterPlay,
                expectedCurrentTimeAfterPlay,
                actualCurrentTimeAfterPlay
            )

            val messageSeekBarAfterPlay =
                "The seekBar position should keep tracking the current position of the song after" +
                        " user changes the seekBar position"
            val expectedSeekBarAfterPlay = 170
            val actualSeekBarAfterPlay = seekBar.progress
            assertEquals(messageSeekBarAfterPlay, expectedSeekBarAfterPlay, actualSeekBarAfterPlay)
        }
    }

    @Test
    fun checkStateWithSeekBarChangeAfterMusicEnd() {
        testActivity {
            player
            currentTimeTv
            totalTimeTv
            seekBar
            playPauseButton

            playPauseButton.clickAndRun(300_000) // plays the entire song

            seekBar.setProgressAsUser(200)
            shadowLooper.idleFor(Duration.ofMillis(10_100))
            // total time elapsed = 200_000 millis

            val messagePlayerWithSeekBarChangeAfterCompletingSong =
                "The player should remain stopped after user changes the seekBar position if it was stopped"
            val expectedPlayerWithSeekBarChangeAfterCompletingSong = false
            val actualPlayerWithSeekBarChangeAfterCompletingSong = player.isPlaying
            assertEquals(
                messagePlayerWithSeekBarChangeAfterCompletingSong,
                expectedPlayerWithSeekBarChangeAfterCompletingSong,
                actualPlayerWithSeekBarChangeAfterCompletingSong
            )

            val messagePlayerPositionWithSeekBarChangeAfterCompletingSong =
                "The player should change the current position after user changes the seekBar position"
            val expectedPlayerPositionWithSeekBarChangeAfterCompletingSong = 200_000
            val actualPlayerPositionWithSeekBarChangeAfterCompletingSong = player.currentPosition
            assertEquals(
                messagePlayerPositionWithSeekBarChangeAfterCompletingSong,
                expectedPlayerPositionWithSeekBarChangeAfterCompletingSong,
                actualPlayerPositionWithSeekBarChangeAfterCompletingSong
            )

            val messageCurrentTimeWithSeekBarChangeAfterCompletingSong =
                "The currentTimeTv should keep tracking the current time of the song after" +
                        " user changes the seekBar position"
            val expectedCurrentTimeWithSeekBarChangeAfterCompletingSong = "03:20"
            val actualCurrentTimeWithSeekBarChangeAfterCompletingSong =  currentTimeTv.text.toString()
            assertEquals(
                messageCurrentTimeWithSeekBarChangeAfterCompletingSong,
                expectedCurrentTimeWithSeekBarChangeAfterCompletingSong,
                actualCurrentTimeWithSeekBarChangeAfterCompletingSong
            )

            val messageSeekBarWithSeekBarChangeAfterCompletingSong =
                "The seekBar position should keep tracking the current position of the song after" +
                        " user changes the seekBar position"
            val expectedSeekBarWithSeekBarChangeAfterCompletingSong = 200
            val actualSeekBarWithSeekBarChangeAfterCompletingSong = seekBar.progress
            assertEquals(
                messageSeekBarWithSeekBarChangeAfterCompletingSong,
                expectedSeekBarWithSeekBarChangeAfterCompletingSong,
                actualSeekBarWithSeekBarChangeAfterCompletingSong
            )

            val messageTotalTimeWithSeekBarChangeAfterCompletingSong =
                "The totalTimeTv should not change the total time of the song after" +
                        " user changes the seekBar position"
            val expectedTotalTimeWithSeekBarChangeAfterCompletingSong = "03:30"
            val actualTotalTimeWithSeekBarChangeAfterCompletingSong =  totalTimeTv.text.toString()
            assertEquals(
                messageTotalTimeWithSeekBarChangeAfterCompletingSong,
                expectedTotalTimeWithSeekBarChangeAfterCompletingSong,
                actualTotalTimeWithSeekBarChangeAfterCompletingSong
            )

            playPauseButton.clickAndRun(5_100)
            // total time elapsed = 205_100 millis

            val messagePlayerPlayingAfterPlayAgain =
                "The player should be able to play again a song that had seekBar changes after song was finished"
            val expectedPlayerPlayingAfterPlayAgain = true
            val actualPlayerPlayingAfterPlayAgain = player.isPlaying
            assertEquals(
                messagePlayerPlayingAfterPlayAgain,
                expectedPlayerPlayingAfterPlayAgain,
                actualPlayerPlayingAfterPlayAgain
            )

            val messagePlayerPositionAfterPlayAgain =
                "The player should change the current position after user changes the seekBar position"
            val expectedPlayerPositionAfterPlayAgain = 205_100
            val actualPlayerPositionAfterPlayAgain = player.currentPosition
            assertEquals(
                messagePlayerPositionAfterPlayAgain,
                expectedPlayerPositionAfterPlayAgain,
                actualPlayerPositionAfterPlayAgain
            )

            val messageCurrentTimeAfterPlayAgain =
                "The currentTimeTv should keep tracking the current time of the song after" +
                        " user changes the seekBar position"
            val expectedCurrentTimeAfterPlayAgain = "03:25"
            val actualCurrentTimeAfterPlayAgain =  currentTimeTv.text.toString()
            assertEquals(
                messageCurrentTimeAfterPlayAgain,
                expectedCurrentTimeAfterPlayAgain,
                actualCurrentTimeAfterPlayAgain
            )

            val messageSeekBarAfterPlayAgain =
                "The seekBar position should keep tracking the current position of the song after" +
                        " user changes the seekBar position"
            val expectedSeekBarAfterPlayAgain = 205
            val actualSeekBarAfterPlayAgain = seekBar.progress
            assertEquals(
                messageSeekBarAfterPlayAgain,
                expectedSeekBarAfterPlayAgain,
                actualSeekBarAfterPlayAgain
            )
        }
    }
}