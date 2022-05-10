package org.hyperskill.musicplayer

import android.app.Activity
import android.media.MediaPlayer
import android.view.View
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
        activityController.setup().get()
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
     * Use this method to perform clicks. It will also advance the clock 500 milliseconds and run
     * enqueued Runnable scheduled to run on main looper in that timeframe.
     *
     * Internally it calls performClick() and shadowLooper.idleFor(Duration.ofMillis(500))
     */
    protected fun View.clickAndRun(){
        this.performClick()
        shadowLooper.idleFor(Duration.ofMillis(500))
    }
}