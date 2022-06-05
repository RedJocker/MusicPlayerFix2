package org.hyperskill.musicplayer

import android.Manifest
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import org.hyperskill.musicplayer.internals.CustomMediaPlayerShadow
import org.hyperskill.musicplayer.internals.FakeSongRepository
import org.hyperskill.musicplayer.internals.MusicPlayerUnitTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [CustomMediaPlayerShadow::class])
class Stage4UnitTest : MusicPlayerUnitTest<MainActivity>(MainActivity::class.java){

    private val playPauseButton: Button by lazy {
        activity.findViewByString("playPauseButton")
    }

    private val stopButton: Button by lazy {
        activity.findViewByString("stopButton")
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

    private val searchButton: Button by lazy {
        val view = activity.findViewByString<Button>("searchButton")
        val actualText = view.text.toString().lowercase()
        assertEquals("Incorrect text for searchButton", "search", actualText)
        view
    }

    private val songList: RecyclerView by lazy {
        activity.findViewByString("songList")
    }


    @Test
    fun testSongFromSongListIsPlayedWhenSongItemBtnClicked() {
        shadowActivity.grantPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
        val fakeSongResult = FakeSongRepository.fakeSongData
        setupContentProvider(fakeSongResult)

        testActivity {
            currentTimeTv
            totalTimeTv
            searchButton
            songList

            searchButton.clickAndRun()
            songList.assertItems(fakeSongResult)

            val expectedSong = FakeSongRepository.fakeSongData[0]
            CustomMediaPlayerShadow.setFakeSong(expectedSong)
            val songItemBtn = songList[0].findViewByString<ImageButton>("songItemPlayPauseImgBtn")

            songItemBtn.clickAndRun(2_100)
            // total time = 2_100

            assertTrue(
                "player should start playing song after initial click to songItemPlayPauseImgBtn",
                player.isPlaying
            )

            assertEquals(
                "player should update duration after initial click to songItemPlayPauseImgBtn",
                expectedSong.duration,
                player.duration
            )

            assertEquals(
                "totalTimeTv should update text after initial click to songItemPlayPauseImgBtn",
                "05:50",
                totalTimeTv.text
            )

            assertEquals(
                "currentTimeTv should track time after initial click to songItemPlayPauseImgBtn",
                "00:02",
                currentTimeTv.text
            )

            assertEquals(
                "When a song from the song list is playing the image of songItemPlayPauseImgBtn should be R.drawable.ic_action_pause",
                R.drawable.ic_action_pause,
                shadowOf(songItemBtn.drawable).createdFromResId
            )

            songItemBtn.clickAndRun(2_100)

            assertFalse(
                "player should pause playing song after second click to songItemPlayPauseImgBtn",
                player.isPlaying
            )

            assertEquals(
                "player should keep duration after second click to songItemPlayPauseImgBtn",
                expectedSong.duration,
                player.duration
            )

            assertEquals(
                "totalTimeTv should keep text after second click to songItemPlayPauseImgBtn",
                "05:50",
                totalTimeTv.text
            )

            assertEquals(
                "currentTimeTv should pause after second click to songItemPlayPauseImgBtn",
                "00:02",
                currentTimeTv.text
            )

            assertEquals(
                "When a song from the song list is paused the image of songItemPlayPauseImgBtn should be R.drawable.ic_action_play",
                R.drawable.ic_action_play,
                shadowOf(songItemBtn.drawable).createdFromResId
            )

            songItemBtn.clickAndRun(3_100)
            // total time = 5_200

            assertTrue(
                "player should resume playing song after third click to songItemPlayPauseImgBtn",
                player.isPlaying
            )

            assertEquals(
                "player should resume tracking duration after third click to songItemPlayPauseImgBtn",
                expectedSong.duration,
                player.duration
            )

            assertEquals(
                "totalTimeTv should keep text after third click to songItemPlayPauseImgBtn",
                "05:50",
                totalTimeTv.text
            )

            assertEquals(
                "currentTimeTv should pause after second click to songItemPlayPauseImgBtn",
                "00:05",
                currentTimeTv.text
            )

            assertEquals(
                "When a song from the song list is playing the image of songItemPlayPauseImgBtn should be R.drawable.ic_action_pause",
                R.drawable.ic_action_pause,
                shadowOf(songItemBtn.drawable).createdFromResId
            )
        }
    }
}