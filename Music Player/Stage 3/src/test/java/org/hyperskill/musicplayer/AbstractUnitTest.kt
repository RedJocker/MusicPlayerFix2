package org.hyperskill.musicplayer

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.view.View
import android.widget.SeekBar
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowActivity
import org.robolectric.shadows.ShadowLooper
import org.robolectric.shadows.ShadowMediaPlayer
import java.time.Duration

@Config(shadows = [CustomMediaPlayerShadow::class])
abstract class AbstractUnitTest<T : Activity>(clazz: Class<T>) {

    private val activityController: ActivityController<T> by lazy {
        Robolectric.buildActivity(clazz)
    }

    protected val activity : Activity by lazy {
        CustomMediaPlayerShadow.setCreateListener(::onMediaPlayerCreated)
        activityController.get()
    }

    protected val shadowActivity: ShadowActivity by lazy {
        shadowOf(activity)
    }

    protected val shadowLooper: ShadowLooper by lazy {
        shadowOf(activity.mainLooper)
    }

    private var playerPrivate: MediaPlayer? = null
    private var shadowPlayerPrivate: ShadowMediaPlayer? = null

    protected var player: MediaPlayer
        get() {
            assertNotNull("No MediaPlayer was found", playerPrivate)
            return this.playerPrivate!!
        }
        set(_) {}

    protected var shadowPlayer: ShadowMediaPlayer
        get() {
            assertNotNull("No MediaPlayer was found", playerPrivate)
            return this.shadowPlayerPrivate!!
        }
        set(_) {}

    private fun onMediaPlayerCreated(player: MediaPlayer, shadow: ShadowMediaPlayer) {
        playerPrivate = player
        shadowPlayerPrivate = shadow
    }

    /**
     * Decorate your test code with this method to ensure better error messages displayed
     * when tests are run with check button and exceptions are thrown by user implementation.
     */
    fun testActivity(arguments: Intent = Intent(), testCodeBlock: (Activity) -> Unit) {
        try {
            activity.intent =  arguments
            activityController.setup()
        } catch (ex: Exception) {
            throw AssertionError("Exception, test failed on activity creation with $ex\n${ex.stackTraceToString()}")
        }

        try {
            testCodeBlock(activity)
        } catch (ex: Exception) {
            throw AssertionError("Exception. Test failed on activity execution with $ex\n${ex.stackTraceToString()}")
        }
    }

    /**
     * Use this method to find views.
     *
     * The view existence will be assert before being returned
     */
    inline fun <reified T> Activity.findViewByString(idString: String): T {
        val id = this.resources.getIdentifier(idString, "id", this.packageName)
        val view: View? = this.findViewById(id)

        val idNotFoundMessage = "View with id \"$idString\" was not found"
        val wrongClassMessage = "View with id \"$idString\" is not from expected class. " +
                "Expected ${T::class.java.simpleName} found ${view?.javaClass?.simpleName}"

        assertNotNull(idNotFoundMessage, view)
        assertTrue(wrongClassMessage, view is T)

        return view as T
    }

    /**
     * Use this method to find views.
     *
     * The view existence will be assert before being returned
     */
    inline fun <reified T> View.findViewByString(idString: String): T {
        val id = this.resources.getIdentifier(idString, "id", context.packageName)
        val view: View? = this.findViewById(id)

        val idNotFoundMessage = "View with id \"$idString\" was not found"
        val wrongClassMessage = "View with id \"$idString\" is not from expected class. " +
                "Expected ${T::class.java.simpleName} found ${view?.javaClass?.simpleName}"

        assertNotNull(idNotFoundMessage, view)
        assertTrue(wrongClassMessage, view is T)

        return view as T
    }

    /**
     * Use this method to perform clicks. It will also advance the clock millis milliseconds and run
     * enqueued Runnable scheduled to run on main looper in that timeframe.
     * Default value for millis is 500
     *
     * Internally it calls performClick() and shadowLooper.idleFor(millis)
     */
    protected fun View.clickAndRun(millis: Long = 500){
        this.performClick()
        shadowLooper.idleFor(Duration.ofMillis(millis))
    }

    /**
     * Use this method to set the progress as a user.
     *
     * Will trigger attached listeners.
     *
     * First onStartTrackingTouch(), then onProgressChanged() as user, and finally onStopTrackingTouch()
     */
    protected fun SeekBar.setProgressAsUser(progress: Int) {
        val shadowSeekBar = shadowOf(this)
        shadowSeekBar.onSeekBarChangeListener.onStartTrackingTouch(this)

        // using java reflection to change progress without triggering listener
        var clazz = this::class.java  // may be subclass of SeekBar
        while(clazz.name != "android.widget.ProgressBar") {  // since SeekBar is a subclass of ProgressBar this should not be an infinite loop
            clazz = clazz.superclass as Class<out SeekBar>
        }
        val progressBarClazz = clazz
        val progressField = progressBarClazz.getDeclaredField("mProgress")
        progressField.isAccessible = true
        progressField.setInt(this, progress)
        //

        shadowSeekBar.onSeekBarChangeListener.onProgressChanged(this, progress, true)
        shadowSeekBar.onSeekBarChangeListener.onStopTrackingTouch(this)
    }
}