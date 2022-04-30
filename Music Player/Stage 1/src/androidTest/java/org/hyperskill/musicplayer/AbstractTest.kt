package org.hyperskill.musicplayer

import android.app.Activity
import android.content.Context
import android.view.View

abstract class AbstractUnitTest<T : Activity>{

    protected fun Activity.id(idString: String) : Int {
        return resources.getIdentifier(idString, "id", packageName)
    }

    protected fun Context.id(id: String) : Int {
        return resources.getIdentifier(id, "id", packageName)
    }

    protected fun <V: View> Activity.find(idString: String) : V = findViewById(id(idString))

    protected fun <T: View> View.find(idString: String): T = findViewById(context.id(idString))
}