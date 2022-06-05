package org.hyperskill.musicplayer

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import org.hyperskill.musicplayer.internals.CustomMediaPlayerShadow
import org.hyperskill.musicplayer.internals.FakeSongRepository
import org.hyperskill.musicplayer.internals.MusicPlayerUnitTest
import org.hyperskill.musicplayer.internals.SongFake
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(shadows = [CustomMediaPlayerShadow::class])
class Stage3UnitTest : MusicPlayerUnitTest<MainActivity>(MainActivity::class.java){

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

}