package org.hyperskill.musicplayer

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

    private val playPauseButtonId: Int by lazy { getViewId("playPauseButton") }
    private val stopButtonId: Int by lazy { getViewId("stopButton") }
    private val songTitleId: Int by lazy { getViewId("songTitleTextView") }
    private val songArtistId: Int by lazy { getViewId("songArtistTextView") }
    private val totalTimeViewId: Int by lazy { getViewId("totalTimeTextView") }
    private val currentTimeViewId: Int by lazy { getViewId("currentTimeTextView") }
    private val seekBarId: Int by lazy { getViewId("seekBar") }


    @Test
    fun checkIsTotalTimeViewExist() {
        assertView<TextView>(totalTimeViewId) { R.id.currentTimeTextView}
    }

    @Test
    fun checkIsCurrentTimeViewExist() {
        assertView<TextView>(currentTimeViewId) {}
    }

    @Test
    fun checkIsSeekbarExist() {
        assertView<SeekBar>(seekBarId) {}
    }

    @Test
    fun checkSeekbarInitialValues() {
        val messageStart = "seekBar should start with progress at the beginning"
        val messageMax = "seekBar should have max value matching the song duration in seconds"
        val expectedInitialProgress = 0
        val expectedInitialMax = 214
        val seekBarInteraction: ViewInteraction = onView(withId(seekBarId))

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

        val currentTimeViewInteraction: ViewInteraction = onView(withId(currentTimeViewId))
        val totalTimeViewInteraction: ViewInteraction = onView(withId(totalTimeViewId))

        currentTimeViewInteraction.check { view, _ ->
            val currentTimeTextView = view as TextView
            val expectedInitialTime = "00:00"
            val actualInitialTime = currentTimeTextView.text.toString()
            assertEquals(messageInitialCurrentTimeView, expectedInitialTime, actualInitialTime)
        }

        totalTimeViewInteraction.check { view, _ ->
            val totalTimeTextView = view as TextView
            val expectedInitialTime = "03:34"
            val actualInitialTime = totalTimeTextView.text.toString()
            assertEquals(messageInitialTotalTimeView, expectedInitialTime, actualInitialTime)
        }
    }

    @Test
    fun checkIsSeekbarMoving() {
        val messageStart = "seekBar should start with progress at the beginning"
        val messageProgressChanges = "Is seekbar progress changing while music playing?"
        val messageMatchProgress = "The progress of seekBar should match `player.currentPosition / 1000`"

        val seekBarInteraction: ViewInteraction = onView(withId(seekBarId))
        val playPauseButtonInteraction: ViewInteraction = onView(withId(playPauseButtonId))
        val stopInteraction: ViewInteraction = onView(withId(stopButtonId))

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
        stopInteraction.perform(click())
    }

    @Test
    fun checkStateAfterSeekBarTrackingTouch() {
        val seekBarInteraction: ViewInteraction = onView(withId(seekBarId))
        val currentTimeInteraction = onView(withId(currentTimeViewId))
        val playPauseButtonInteraction: ViewInteraction = onView(withId(playPauseButtonId))
        val totalTimeViewInteraction: ViewInteraction = onView(withId(totalTimeViewId))
        val stopInteraction: ViewInteraction = onView(withId(stopButtonId))
        val progressSetByTest = 71

        seekBarInteraction.perform(clickSeekBarAction(progressSetByTest))

        val messageCurrentTextViewChangeExpected =
            "Changing the seekBar progress should change the currentTimeTextView"
        currentTimeInteraction.check { view, _ ->
            val actualCurrentTime = (view as TextView).text
            val expectedCurrentTime = progressSetByTest.secondsToTimeString()
            assertEquals(messageCurrentTextViewChangeExpected, expectedCurrentTime, actualCurrentTime)
        }

        val messagePlayerChangeExpected =
            "Changing the seekBar progress should change the player current position"
        assertPlayer { player ->
            val actualCurrentPosition = player.currentPosition
            val expectedCurrentPosition = progressSetByTest * 1000
            assertEquals(messagePlayerChangeExpected, expectedCurrentPosition, actualCurrentPosition)
        }

        val messageTotalTimeShouldNotChange =
            "totalTimeTextView should not change text value after seekBar changes"
        totalTimeViewInteraction.check { view, _ ->
            val totalTimeTextView = view as TextView
            val expectedTime = "03:34"
            val actualTime = totalTimeTextView.text.toString()
            assertEquals(messageTotalTimeShouldNotChange, expectedTime, actualTime)
        }

        playPauseButtonInteraction.perform(click())
        Thread.sleep(1800)

        val messageProgressChanges =
            "After changing the seekBar progress and clicking on playButton, progress should resume based on new value"
        val messageMatchProgress =
            "After changing the seekBar progress and clicking on playButton, player should start based on new value"

        seekBarInteraction.check { view, _ ->
            val actualProgress: Int = (view as SeekBar).progress
            assertTrue(messageProgressChanges, actualProgress > progressSetByTest)

            assertPlayer { player ->
                val playerDuration = player.currentPosition / 1000
                val actualProgress2 = view.progress
                assertTrue(messageMatchProgress, playerDuration > progressSetByTest)
                assertEquals(messageMatchProgress, playerDuration, actualProgress2)
            }
        }


        stopInteraction.perform(click())
        val messageCurrentTextViewChangeAfterStop =
            "After clicking on stopButton currentTimeTextView should change back to initial value"
        currentTimeInteraction.check { view, _ ->
            val actualCurrentTime = (view as TextView).text
            val expectedCurrentTime = "00:00"
            assertEquals(messageCurrentTextViewChangeAfterStop, expectedCurrentTime, actualCurrentTime)
        }
    }

    @Test
    fun checkSeekBarAfterStop() {
        val seekBarInteraction: ViewInteraction = onView(withId(seekBarId))
        val currentTimeInteraction = onView(withId(currentTimeViewId))
        val playPauseButtonInteraction: ViewInteraction = onView(withId(playPauseButtonId))
        val stopInteraction: ViewInteraction = onView(withId(stopButtonId))
        val progressSetByTest = 122

        playPauseButtonInteraction.perform(click())
        stopInteraction.perform(click())

        seekBarInteraction.perform(clickSeekBarAction(progressSetByTest))

        val messageCurrentTextViewChangeExpected =
            "After stopButton clicked changing the seekBar progress should change the currentTimeTextView"
        currentTimeInteraction.check { view, _ ->
            val actualCurrentTime = (view as TextView).text
            val expectedCurrentTime = progressSetByTest.secondsToTimeString()
            assertEquals(messageCurrentTextViewChangeExpected, expectedCurrentTime, actualCurrentTime)
        }

        val messagePlayerChangeExpected =
            "After stopButton clicked changing the seekBar progress should change the player current position"
        assertPlayer { player ->
            val actualCurrentPosition = player.currentPosition
            val expectedCurrentPosition = progressSetByTest * 1000
            assertEquals(messagePlayerChangeExpected, expectedCurrentPosition, actualCurrentPosition)
        }
    }
}