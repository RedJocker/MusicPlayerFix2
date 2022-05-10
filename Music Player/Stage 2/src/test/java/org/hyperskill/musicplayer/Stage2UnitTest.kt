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
                "The currentTimeTv should display resume playing after " +
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
    fun checkStateAfterSeekBarTrackingTouch() {

    }
}