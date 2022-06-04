package org.hyperskill.musicplayer

import android.Manifest
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import android.provider.MediaStore
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
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

    private val searchButton: Button by lazy {
        val view = activity.findViewByString<Button>("searchButton")
        val actualText = view.text.toString().lowercase()
        assertEquals("Incorrect text for searchButton", "search", actualText)
        view
    }

    private val songList: RecyclerView by lazy {
        activity.findViewByString("songList")
    }

    private val expectedRequestCode = 1

    @Test
    fun checkSearchButtonExist() {
        testActivity {
            searchButton
        }
    }

    @Test
    fun testMusicFilesRetrievalAllFiles() {
        shadowActivity.grantPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
        val fakeSongResult = FakeSongRepository.fakeSongData
        setupContentProvider(fakeSongResult)

        testActivity {
            searchButton
            songList

            searchButton.clickAndRun()
            songList.assertItems(fakeSongResult)
        }
    }

    @Test
    fun testMusicFilesRetrievalNoFiles() {
        shadowActivity.grantPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
        val fakeSongResult = listOf<SongFake>()
        setupContentProvider(fakeSongResult)

        testActivity {
            searchButton
            songList
            songList.assertItems(fakeSongResult)
        }
    }

    @Test
    fun testPermissionRequestGranted() {
        val fakeSongResult = FakeSongRepository.fakeSongData.take(1)

        setupContentProvider(fakeSongResult)

        testActivity {
            searchButton
            songList

            searchButton.clickAndRun()
            assertRequestPermissions(listOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            shadowActivity.grantPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            activity.onRequestPermissionsResult(
                expectedRequestCode,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                intArrayOf(PackageManager.PERMISSION_GRANTED)
            )
            shadowLooper.runToEndOfTasks()

            songList.assertItems(fakeSongResult)
        }
    }

    @Test
    fun testPermissionRequestDenied() {
        val fakeSongResult = FakeSongRepository.fakeSongData

        setupContentProvider(fakeSongResult)

        testActivity {
            searchButton
            songList

            searchButton.clickAndRun()
            assertRequestPermissions(listOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            shadowActivity.denyPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            activity.onRequestPermissionsResult(
                expectedRequestCode,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                intArrayOf(PackageManager.PERMISSION_DENIED)
            )
            shadowLooper.runToEndOfTasks()

            songList.assertItems(listOf())
        }
    }

    @Test
    fun testPermissionRequestAgainGranted() {
        val fakeSongResult = FakeSongRepository.fakeSongData

        setupContentProvider(fakeSongResult)

        testActivity {
            searchButton
            songList

            searchButton.clickAndRun()
            assertRequestPermissions(listOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            shadowActivity.denyPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            activity.onRequestPermissionsResult(
                expectedRequestCode,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                intArrayOf(PackageManager.PERMISSION_DENIED)
            )
            shadowLooper.runToEndOfTasks()

            songList.assertItems(listOf())

            searchButton.clickAndRun()
            assertRequestPermissions(listOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            shadowActivity.grantPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            activity.onRequestPermissionsResult(
                expectedRequestCode,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                intArrayOf(PackageManager.PERMISSION_GRANTED)
            )
            shadowLooper.runToEndOfTasks()

            songList.assertItems(fakeSongResult)
        }
    }

    private fun setupContentProvider(fakeSongResult: List<SongFake>){
        val info = ProviderInfo().apply {
            authority = MediaStore.AUTHORITY
        }
        Robolectric.buildContentProvider(FakeContentProvider::class.java).create(info)
        FakeContentProvider.fakeSongResult = fakeSongResult
    }

    private fun RecyclerView.assertItems(fakeSongResult: List<SongFake>) {
        assertNotNull("Your recycler view adapter should not be null", this.adapter)

        val expectedSize = fakeSongResult.size

        val actualSize = this.adapter!!.itemCount
        assertEquals("Incorrect number of list items", expectedSize, actualSize)

        if(expectedSize > 0) {
            val firstItemViewHolder = this.findViewHolderForAdapterPosition(0)
                ?: throw AssertionError("No item is being displayed on songList RecyclerView, is it big enough to display one item?")

            // setting height to ensure that all items are inflated
            this.layout(0,0, this.width, firstItemViewHolder.itemView.height * (expectedSize + 1))

            for((i, song) in fakeSongResult.withIndex()) {
                val listItem = this.findViewHolderForAdapterPosition(i)!!.itemView
                val songTitleTv: TextView = listItem.findViewByString("songTitleTv")
                val songArtistTv: TextView = listItem.findViewByString("songArtistTv")
                val actualSongTitle = songTitleTv.text.toString()
                assertEquals("songTitleTv with incorrect text", actualSongTitle, song.title)
                val actualSongArtist = songArtistTv.text.toString()
                assertEquals("songArtistTv with incorrect text", actualSongArtist, song.artist)
            }
        }
    }
}