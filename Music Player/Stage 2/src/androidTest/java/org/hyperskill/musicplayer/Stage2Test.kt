package org.hyperskill.musicplayer

import android.media.MediaPlayer
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Stage2Test: AbstractTest<MainActivity>() {

    private val currentTimeTvId: Int by lazy { getViewId("currentTimeTextView") }
    private val playPauseButtonId: Int by lazy { getViewId("playPauseButton") }
    private val seekBarId: Int by lazy { getViewId("seekBar") }
    private val songTitleId: Int by lazy { getViewId("songTitleTextView") }
    private val songArtistId: Int by lazy { getViewId("songArtistTextView") }
    private val stopButtonId: Int by lazy { getViewId("stopButton") }
    private val totalTimeViewId: Int by lazy { getViewId("totalTimeTextView") }

    private val currentTimeTvInteraction: ViewInteraction by lazy {
        onView(withId(currentTimeTvId))
    }
    private val playPauseButtonInteraction: ViewInteraction by lazy {
        onView(withId(playPauseButtonId))
    }
    private val seekBarInteraction: ViewInteraction by lazy {
        onView(withId(seekBarId))
    }
    private val stopButtonInteraction: ViewInteraction by lazy {
        onView(withId(stopButtonId))
    }
    private val totalTimeTvInteraction: ViewInteraction by lazy {
        onView(withId(totalTimeViewId))
    }

    @Test
    fun checkViewsExist() {
        assertView<TextView>("currentTimeTextView") {}
        assertView<ImageButton>("playPauseButton") {}
        assertView<SeekBar>("seekBar") {}
        assertView<TextView>("songTitleTextView") {}
        assertView<TextView>("songArtistTextView") {}
        assertView<ImageButton>("stopButton") {}
        assertView<TextView>("totalTimeTextView") {}
    }

    @Test
    fun checkSeekbarInitialValues() {
        val messageStart = "seekBar should start with progress at the beginning"
        val messageMax = "seekBar should have max value matching the song duration in seconds"
        val expectedInitialProgress = 0
        val expectedInitialMax = 214

        seekBarInteraction.check { view, _ ->
            val seekBar = (view as SeekBar)

            val actualInitialProgress = seekBar.progress
            assertEquals(messageStart, expectedInitialProgress, actualInitialProgress)

            val actualInitialMax = seekBar.max
            assertEquals(messageMax, expectedInitialMax, actualInitialMax)
        }
    }

    @Test
    fun checkTimeTextViewsInitialValues() {
        val messageInitialCurrentTimeView = "currentTimeTextView should display the initial value"
        val messageInitialTotalTimeView = "totalTimeTextView should display the music total duration"

        currentTimeTvInteraction.check { view, _ ->
            val currentTimeTextView = view as TextView
            val expectedInitialTime = "00:00"
            val actualInitialTime = currentTimeTextView.text.toString()
            assertEquals(messageInitialCurrentTimeView, expectedInitialTime, actualInitialTime)
        }

        totalTimeTvInteraction.check { view, _ ->
            val totalTimeTextView = view as TextView
            val expectedInitialTime = "03:34"
            val actualInitialTime = totalTimeTextView.text.toString()
            assertEquals(messageInitialTotalTimeView, expectedInitialTime, actualInitialTime)
        }
    }

    @Test
    fun checkIsSeekbarMoving() {
        val messageStart = "seekBar should start with progress at the beginning"
        val messageProgressChanges = "Is seekBar progress changing while music playing?"
        val messageMatchProgress = "The progress of seekBar should match `player.currentPosition / 1000`"

        seekBarInteraction.check { view, _ ->
            val expectedProgress = 0
            val actualProgress: Int = (view as SeekBar).progress
            assertEquals(messageStart, expectedProgress, actualProgress)
        }

        playPauseButtonInteraction.perform(click())
        Thread.sleep(1800)

        seekBarInteraction.check { view, _ ->
            val actualProgress: Int = (view as SeekBar).progress
            assertTrue(messageProgressChanges, actualProgress > 0)

            assertPlayer { player ->
                val playerDuration = player.currentPosition / 1000
                val actualProgress2 = view.progress
                assertEquals(messageMatchProgress, playerDuration, actualProgress2)
            }
        }
        stopButtonInteraction.perform(click())
    }

    @Test
    fun checkStateAfterSeekBarTrackingTouch() {
        val progressSetByTest = 71

        seekBarInteraction.perform(clickSeekBarAction(progressSetByTest))

        currentTimeTvInteraction.check { view, _ ->
            val messageCurrentTextViewChangeExpected =
                "Changing the seekBar progress should change the currentTimeTextView"
            val actualCurrentTime = (view as TextView).text
            val expectedCurrentTime = progressSetByTest.secondsToTimeString()
            assertEquals(messageCurrentTextViewChangeExpected, expectedCurrentTime, actualCurrentTime)
        }

        assertPlayer { player ->
            val messagePlayerChangeExpected =
                "Changing the seekBar progress should change the player current position"
            val actualCurrentPosition = player.currentPosition
            val expectedCurrentPosition = progressSetByTest * 1000
            assertEquals(messagePlayerChangeExpected, expectedCurrentPosition, actualCurrentPosition)
        }


        totalTimeTvInteraction.check { view, _ ->
            val messageTotalTimeShouldNotChange =
                "totalTimeTextView should not change text value after seekBar changes"
            val totalTimeTextView = view as TextView
            val expectedTime = "03:34"
            val actualTime = totalTimeTextView.text.toString()
            assertEquals(messageTotalTimeShouldNotChange, expectedTime, actualTime)
        }

        playPauseButtonInteraction.perform(click())
        Thread.sleep(1800)

        seekBarInteraction.check { view, _ ->
            val messageProgressChanges =
                "After changing the seekBar progress and clicking on playButton, " +
                        "progress should resume based on new value"
            val actualProgress: Int = (view as SeekBar).progress
            assertTrue(messageProgressChanges, actualProgress > progressSetByTest)

            assertPlayer { player ->
                val messageMatchProgress =
                    "After changing the seekBar progress and clicking on playButton, " +
                            "player should start based on new value"
                val playerDuration = player.currentPosition / 1000
                val actualProgress2 = view.progress
                assertTrue(messageMatchProgress, playerDuration > progressSetByTest)
                assertEquals(messageMatchProgress, playerDuration, actualProgress2)
            }
        }

        stopButtonInteraction.perform(click())

        currentTimeTvInteraction.check { view, _ ->
            val messageCurrentTextViewChangeAfterStop =
                "After clicking on stopButton currentTimeTextView should change back to initial value"
            val actualCurrentTime = (view as TextView).text
            val expectedCurrentTime = "00:00"
            assertEquals(messageCurrentTextViewChangeAfterStop, expectedCurrentTime, actualCurrentTime)
        }
    }

    @Test
    fun checkSeekBarAfterStop() {
        val progressSetByTest = 122

        playPauseButtonInteraction.perform(click())
        stopButtonInteraction.perform(click())
        seekBarInteraction.perform(clickSeekBarAction(progressSetByTest))

        currentTimeTvInteraction.check { view, _ ->
            val messageCurrentTextViewChangeExpected =
                "After stopButton clicked changing the seekBar progress should change the currentTimeTextView"
            val actualCurrentTime = (view as TextView).text
            val expectedCurrentTime = progressSetByTest.secondsToTimeString()
            assertEquals(messageCurrentTextViewChangeExpected, expectedCurrentTime, actualCurrentTime)
        }

        assertPlayer { player ->
            val messagePlayerChangeExpected =
                "After stopButton clicked changing the seekBar progress should change the player current position"
            val actualCurrentPosition = player.currentPosition
            val expectedCurrentPosition = progressSetByTest * 1000
            assertEquals(messagePlayerChangeExpected, expectedCurrentPosition, actualCurrentPosition)
        }
    }

    @Test
    fun checkOnMusicEndState() {
        val getNearEndMusic: MediaPlayer.() -> Int = { (this.duration - 1000) / 1000 }

        var nearEndMusic = 0
        assertPlayer { nearEndMusic = it.getNearEndMusic() }

        seekBarInteraction.perform(clickSeekBarAction(nearEndMusic))

        currentTimeTvInteraction.check { view, _ ->
            val messageCurrentTimeChangeExpected =
                "Changing the seekBar progress should change the currentTimeTextView"
            val actualCurrentTime = (view as TextView).text
            val expectedCurrentTime = nearEndMusic.secondsToTimeString()
            assertEquals(messageCurrentTimeChangeExpected, expectedCurrentTime, actualCurrentTime)
        }

        assertPlayer { player ->
            val messagePlayerChangeExpected =
                "Changing the seekBar progress should change the player current position"
            val actualCurrentPosition = player.currentPosition
            val expectedCurrentPosition = nearEndMusic * 1000
            assertEquals(messagePlayerChangeExpected, expectedCurrentPosition, actualCurrentPosition)
        }

        playPauseButtonInteraction.perform(click())
        Thread.sleep(3000)

        currentTimeTvInteraction.check { view, _ ->
            val messageCurrentTimeChangeExpected =
                "currentTimeTextView text should reset on music end"
            val actualCurrentTime = (view as TextView).text
            val expectedCurrentTime = "00:00"
            assertEquals(messageCurrentTimeChangeExpected, expectedCurrentTime, actualCurrentTime)
        }

        assertPlayer { player ->
            val messagePlayerChangeExpected =
                "Getting to the end of music should stop the player"
            val expectedIsPlaying = false
            val actualIsPlaying = player.isPlaying
            assertEquals(messagePlayerChangeExpected, expectedIsPlaying, actualIsPlaying)
        }

        assertPlayer { player ->
            val messagePlayerChangeExpected =
                "Getting to the end of music should reset player time position"
            val expectedPlayerPosition = 0
            val actualPlayerPosition = player.currentPosition
            assertEquals(messagePlayerChangeExpected, expectedPlayerPosition, actualPlayerPosition)
        }

        playPauseButtonInteraction.perform(click())

        assertPlayer { player ->
            val messagePlayerChangeExpected =
                "It should be possible to play again a music that has reached end"
            val expectedIsPlaying = true
            val actualIsPlaying = player.isPlaying
            assertEquals(messagePlayerChangeExpected, expectedIsPlaying, actualIsPlaying)
        }
        stopButtonInteraction.perform(click())
    }
}