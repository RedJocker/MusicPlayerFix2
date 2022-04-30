package org.hyperskill.musicplayer


import android.widget.ImageButton
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Stage1Test: AbstractTest<MainActivity>() {

    private val playPauseButtonId: Int by lazy { getViewId("play_pause_button") }
    private val stopButtonId: Int by lazy { getViewId("stop_button") }
    private val songTitleId: Int by lazy { getViewId("song_title") }
    private val songArtistId: Int by lazy { getViewId("song_artist") }

    @Test
    fun checkIsSongTitleExist() {
        assertView<TextView>(songTitleId) {}
    }

    @Test
    fun checkIsSongArtistExist() {
        assertView<TextView>(songArtistId) {}
    }

    @Test
    fun checkIsPlayPauseButtonExist() {
        assertView<ImageButton>(playPauseButtonId) {}
    }

    @Test
    fun checkIsStopButtonExist() {
        assertView<ImageButton>(stopButtonId) {}
    }

    @Test
    fun checkIsPlayPauseButtonWorks() {
        val message = "Does play_pause_button controls playback?"

        onView(withId(playPauseButtonId))
            .perform(click())
            .check { _, _ ->
                assertPlayer { player ->
                    assertEquals(message, true, player.isPlaying)
                }
            }.perform(click())
            .check { _, _ ->
                assertPlayer { player ->
                    assertEquals(message, false, player.isPlaying)
                }
            }.perform(click())
            .check { _, _ ->
                assertPlayer { player ->
                    assertEquals(message, true, player.isPlaying)
                }
            }.perform(click())
            .check { _, _ ->
                assertPlayer { player ->
                    assertEquals(message, false, player.isPlaying)
                }
            }
    }

    @Test
    fun checkIsStopButtonWorks() {
        val message = "Does stop button stops playback?"

        onView(withId(playPauseButtonId)).perform(click())

        assertPlayer { player ->
            assertEquals(message, true, player.isPlaying)
        }

        onView(withId(stopButtonId)).perform(click())

        assertPlayer { player ->
            assertEquals(message, false, player.isPlaying)
        }

        onView(withId(playPauseButtonId)).perform(click())

        val messageResume = "Can you resume playback after clicking stop button"

        assertPlayer { player ->
            assertEquals(messageResume, true, player.isPlaying)
        }
    }
}