package org.hyperskill.musicplayer

import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(shadows = [CustomMediaPlayerShadow::class])
class Stage1UnitTestB : AbstractUnitTest<MainActivity>(MainActivity::class.java){

    private val playPauseButton: Button by lazy {
        activity.findViewByString("playPauseButton")
    }

    private val stopButton: Button by lazy {
        activity.findViewByString("stopButton")
    }

    @Test
    fun checkIsPlayPauseButtonExist() {
        testActivity {
            playPauseButton
        }
    }

    @Test
    fun checkIsStopButtonExist() {
        testActivity {
            stopButton
        }
    }

    @Test
    fun checkPlayPauseButtonWorks() {
        testActivity {
            activity
            playPauseButton

            val messageBeginning =
                "The MediaPlayer should not be playing before clicking on playPauseButton"
            val expectedBeginning = false
            val actualBeginning = player.isPlaying
            assertEquals(messageBeginning, expectedBeginning, actualBeginning)

            playPauseButton.clickAndRun()

            val messageAfterInitialClick =
                "The MediaPlayer should be playing after initial click on playPauseButton"
            val expectedAfterInitialClick = true
            val actualAfterInitialClick = player.isPlaying
            assertEquals(messageAfterInitialClick, expectedAfterInitialClick, actualAfterInitialClick)

            playPauseButton.clickAndRun()

            val messageAfterSecondClick =
                "If The MediaPlayer is playing it should stop playing after click on playPauseButton"
            val expectedAfterSecondClick = false
            val actualAfterSecondClick = player.isPlaying
            assertEquals(messageAfterSecondClick, expectedAfterSecondClick, actualAfterSecondClick)

            val messagePositionOnPause =
                "The position after pausing the MediaPlayer should remain the same as it was in the instant it was paused"
            val actualPlayerPositionOnPause = player.currentPosition
            val expectedPlayerPositionOnPause = 500
            assertEquals(messagePositionOnPause, expectedPlayerPositionOnPause, actualPlayerPositionOnPause)

            playPauseButton.clickAndRun()

            val messageAfterThirdClick =
                "The Media player should resume playing if it is paused and playPauseButton was clicked"
            val expectedAfterThirdClick = true
            val actualAfterThirdClick = player.isPlaying
            assertEquals(messageAfterThirdClick, expectedAfterThirdClick, actualAfterThirdClick)

            val messageResumeWhereItWasPaused =
                "The Media player should resume playing in the same position it was paused"
            val expectedPositionAfterResume = 1000
            val actualPositionAfterResume = player.currentPosition
            assertEquals(messageResumeWhereItWasPaused, expectedPositionAfterResume, actualPositionAfterResume)
        }
    }

    @Test
    fun checkStopButtonWorks() {
        testActivity {
            activity
            playPauseButton
            stopButton

            val messageBeginning =
                "The MediaPlayer should not be playing before clicking on playPauseButton"
            val expectedBeginning = false
            val actualBeginning = player.isPlaying
            assertEquals(messageBeginning, expectedBeginning, actualBeginning)

            playPauseButton.clickAndRun()

            val messageAfterInitialClick =
                "The MediaPlayer should be playing after initial click on playPauseButton"
            val expectedAfterInitialClick = true
            val actualAfterInitialClick = player.isPlaying
            assertEquals(messageAfterInitialClick, expectedAfterInitialClick, actualAfterInitialClick)

            val messagePositionAfterInitialClick =
                "The position of the MediaPlayer should be increasing while playing"
            val expectedPositionAfterInitialClick = 500
            val actualPositionAfterInitialClick = player.currentPosition
            assertEquals(
                messagePositionAfterInitialClick,
                expectedPositionAfterInitialClick,
                actualPositionAfterInitialClick
            )

            stopButton.clickAndRun()

            val messageAfterStopClick =
                "The MediaPlayer should stop playing after click on stopButton"
            val expectedAfterStopClick = false
            val actualAfterStopClick = player.isPlaying
            assertEquals(messageAfterStopClick, expectedAfterStopClick, actualAfterStopClick)

            val messagePositionAfterStop =
                "The position of the MediaPlayer should reset after click on stopButton"
            val expectedPositionAfterStop = 0
            val actualPositionAfterStop = player.currentPosition
            assertEquals(messagePositionAfterStop, expectedPositionAfterStop, actualPositionAfterStop)

            playPauseButton.clickAndRun()

            val messagePlayAfterStop =
                "The MediaPlayer should be able to play again after it was stopped"
            val expectedPlayAfterStop = true
            val actualPlayAfterStop = player.isPlaying
            assertEquals(messagePlayAfterStop, expectedPlayAfterStop, actualPlayAfterStop)

            val messagePositionPlayAfterStop =
                "The position of the MediaPlayer should be resumed from 0 after stop and increase after play"
            val expectedPositionPlayAfterStop = 500
            val actualPositionPlayAfterStop = player.currentPosition
            assertEquals(
                messagePositionPlayAfterStop,
                expectedPositionPlayAfterStop,
                actualPositionPlayAfterStop
            )
        }
    }

    @Test
    fun checkEndOfMusicState() {
        testActivity {
            activity
            playPauseButton

            val messageBeginning =
                "The MediaPlayer should not be playing before clicking on playPauseButton"
            val expectedBeginning = false
            val actualBeginning = player.isPlaying
            assertEquals(messageBeginning, expectedBeginning, actualBeginning)

            playPauseButton.clickAndRun()

            val messageAfterInitialClick =
                "The MediaPlayer should be playing after initial click on playPauseButton"
            val expectedAfterInitialClick = true
            val actualAfterInitialClick = player.isPlaying
            assertEquals(messageAfterInitialClick, expectedAfterInitialClick, actualAfterInitialClick)

            val messagePositionAfterInitialClick =
                "The position of the MediaPlayer should be increasing while playing"
            val expectedPositionAfterInitialClick = 500
            val actualPositionAfterInitialClick = player.currentPosition
            assertEquals(
                messagePositionAfterInitialClick,
                expectedPositionAfterInitialClick,
                actualPositionAfterInitialClick
            )

            shadowLooper.runToEndOfTasks()

            val messageAfterSongFinish =
                "The MediaPlayer should stop playing after song finishes"
            val expectedAfterSongFinish = false
            val actualAfterSongFinish = player.isPlaying
            assertEquals(messageAfterSongFinish, expectedAfterSongFinish, actualAfterSongFinish)

            val messagePositionAfterSongFinish =
                "The position of the MediaPlayer reset after song finishes"
            val expectedPositionAfterSongFinish = 0
            val actualPositionAfterSongFinish = player.currentPosition
            assertEquals(
                messagePositionAfterSongFinish,
                expectedPositionAfterSongFinish,
                actualPositionAfterSongFinish
            )
        }
    }
}