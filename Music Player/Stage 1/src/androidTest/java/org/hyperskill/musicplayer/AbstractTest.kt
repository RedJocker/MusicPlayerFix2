package org.hyperskill.musicplayer

import android.app.Activity
import android.media.MediaPlayer
import android.view.View
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue

abstract class AbstractTest<T : Activity>{

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    /*
        Assumes id to be constants based on this
        https://stackoverflow.com/questions/64951792/are-integer-value-of-view-ids-are-always-constant-in-android
        so it should not be a problem to hold references to ids
    */
    protected fun getViewId(idString: String) : Int {
        var id = 0;
        activityRule.scenario.onActivity { activity ->
            id = activity.id(idString)
        }
        return id
    }

    /**
     * Use this method to make assertions directly on views.
     * Prefer using espresso.ViewAssertion if possible (ex: onView(...).check {...})
     *
     * Don't hold direct references to views outside this method for the same reason
     * you should not hold direct references to activity outside of activityRule.scenario.onActivity,
     * that is, because activity can be recreated at anytime during state transitions.
     * */
    protected inline fun <reified V: View> assertView(viewId: Int, crossinline assertion: (V) -> Unit){
        activityRule.scenario.onActivity {  activity ->
            val view = findExistingViewById<V>(activity, viewId)
            assertion(view)
        }
    }

    /**
     * Use this method to make assertions on the player.
     * Don't hold direct references to player outside this method for the same reason
     * you should not hold direct references to activity outside of activityRule.scenario.onActivity,
     * that is, because activity can be recreated at anytime during state transitions.
     * */
    protected fun assertPlayer(assertion: (MediaPlayer) -> Unit) {
        activityRule.scenario.onActivity { activity :Activity ->
            assertion(activity.getMusicPlayer())
        }
    }

    /**
     * On classes that implement AbstractTest prefer using assertView instead.
     *
     * Don't use this method outside of activityRule.scenario.onActivity.
     *
     * Don't keep holding direct references to views for the same reason
     * you should not hold direct references to activity outside of activityRule.scenario.onActivity,
     * that is, because activity can be recreated at anytime during state transitions.
     */
    /* this method had to be protected instead of private because it had to have the same level
     as its consumers because of inline, but it should be thought as being private */
    protected inline fun <reified V: View> findExistingViewById(activity: Activity, id: Int): V {
       val view: View? = activity.findViewById(id)

       val idNotFoundMessage = "View with id number \"$id\" was not found"
       val wrongClassMessage = "View with id number \"$id\" is not from expected class. " +
               "Expected ${V::class.java.simpleName} found ${view?.javaClass?.simpleName}"

       assertNotNull(idNotFoundMessage, view)
       assertTrue(wrongClassMessage, view is V)

       return view as V
    }

    private fun Activity.id(idString: String) : Int {
        val identifier = resources.getIdentifier(idString, "id", packageName)
        println("idString: $idString, id: $identifier")
        return identifier
    }

    private fun Activity.getMusicPlayer(): MediaPlayer {
        assert(this is MusicPlayable) { "Your Activity should implement the MusicPlayable interface" }
        return (this as MusicPlayable).player
    }
}