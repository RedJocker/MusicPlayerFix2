package org.hyperskill.musicplayer

import android.Manifest
import android.content.pm.ProviderInfo
import android.provider.MediaStore
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.AssertionError


@RunWith(RobolectricTestRunner::class)
@Config(shadows = [CustomMediaPlayerShadow::class])
class Stage3UnitTest : AbstractUnitTest<MainActivity>(MainActivity::class.java){

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

    @Test
    fun checkSearchButtonExist() {
        testActivity {
            searchButton
        }
    }

    @Test
    fun testMusicFilesRetrievalAllFiles() {
        shadowActivity.grantPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
        val info = ProviderInfo().apply {
            authority = MediaStore.AUTHORITY
        }
        Robolectric.buildContentProvider(FakeContentProvider::class.java).create(info)
        FakeContentProvider.fakeSongResult = FakeSongRepository.fakeSongData

        testActivity {
            searchButton.clickAndRun()
            val songList = activity.findViewByString<RecyclerView>("songList")

            assertNotNull("Your recycler view adapter should not be null", songList.adapter)

            val expectedSize = 14
            val actualSize = songList.adapter!!.itemCount
            assertEquals("Incorrect number of list items", expectedSize, actualSize)

            val firstItemViewHolder = songList.findViewHolderForAdapterPosition(0)
                ?: throw AssertionError("No item is being displayed on songList RecyclerView, is it big enough to display one item?")

            // setting height to ensure that all items are inflated
            songList.layout(0,0, songList.width, firstItemViewHolder.itemView.height * 15)

            for((i, song) in FakeSongRepository.fakeSongData.withIndex()) {
                val listItem = songList.findViewHolderForAdapterPosition(i)!!.itemView
                val songTitleTv: TextView = listItem.findViewByString("songTitleTv")
                val songArtistTv: TextView = listItem.findViewByString("songArtistTv")
                val actualSongTitle = songTitleTv.text.toString()
                assertEquals("songTitleTv with incorrect text", actualSongTitle, song.title)
                val actualSongArtist = songArtistTv.text.toString()
                assertEquals("songArtistTv with incorrect text", actualSongArtist, song.artist)
            }
        }
    }

    @Test
    fun testMusicFilesRetrievalNoFiles() {
        shadowActivity.grantPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
        val info = ProviderInfo().apply {
            authority = MediaStore.AUTHORITY
        }
        Robolectric.buildContentProvider(FakeContentProvider::class.java).create(info)
        FakeContentProvider.fakeSongResult = listOf()

        testActivity {
            searchButton.clickAndRun()
            val songList = activity.findViewByString<RecyclerView>("songList")

            assertNotNull("Your recycler view adapter should not be null", songList.adapter)

            val expectedSize = 0
            val actualSize = songList.adapter!!.itemCount
            assertEquals("Incorrect number of list items", expectedSize, actualSize)
        }
    }

    @Test
    fun testPermissionRequestGranted() {
        testActivity {

        }
    }

    @Test
    fun testPermissionRequestDenied() {
        testActivity {

        }
    }

    @Test
    fun testPermissionRequestAgainGranted() {
        testActivity {

        }
    }
}