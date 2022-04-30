package org.hyperskill.musicplayer

import android.app.Activity
import android.media.MediaPlayer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Stage1Test: AbstractUnitTest<MainActivity>() {

    @Rule @JvmField
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun checkIsSongTitleExist() {

        activityRule.scenario.onActivity { activity: Activity ->
            val message = "Is song title visible for user?"
            assertNotNull(message, activity.find("song_title"))
        }
    }

    @Test
    fun checkIsSongArtistExist() {
        activityRule.scenario.onActivity { activity: Activity ->
            val message = "Is song artist name visible for user?"
            assertNotNull(message, activity.find("song_artist"))
        }

    }

    @Test
    fun checkIsPlayPauseButtonExist() {
        activityRule.scenario.onActivity { activity: Activity ->
            val message = "Is play_pause_button visible for user?"
            assertNotNull(message, activity.find("play_pause_button"))
        }
    }

    @Test
    fun checkIsStopButtonExist() {
        activityRule.scenario.onActivity { activity: Activity ->
            val message = "Is stop_button visible for user?"
            assertNotNull(message, activity.find("stop_button"))
        }
    }

    @Test
    fun checkIsPlayPauseButtonWorks() {
        activityRule.scenario.onActivity { activity: Activity ->
            val message = "Does play_pause_button controls playback?"
            val player = activity.getMusicPlayer()
            onView(withId(activity.id("play_pause_button"))).perform(click()).check { view, noViewFoundException ->
                assertEquals(message, true, player.isPlaying)
            }.perform(click()).check { view, noViewFoundException ->
                assertEquals(message, true, player.isPlaying)
            }
        }
    }

    @Test
    fun checkIsStopButtonWorks() {
        activityRule.scenario.onActivity { activity: Activity ->
            val playPauseButton = activity.id("play_pause_button")
            val stopButton = activity.id("stop_button")
            val player = activity.getMusicPlayer()
            onView(withId(playPauseButton)).perform(click()).check(matches(isClickable()))
            val message = "Does stop button stops playback?"
            onView(withId(stopButton)).perform(click())
            assertEquals(message, false, player.isPlaying)
        }
    }

    private fun Activity.getMusicPlayer(): MediaPlayer {
        assert(this is MusicPlayable) { "Your Activity should implement the MusicPlayable interface" }
        return (this as MusicPlayable).player
    }
}