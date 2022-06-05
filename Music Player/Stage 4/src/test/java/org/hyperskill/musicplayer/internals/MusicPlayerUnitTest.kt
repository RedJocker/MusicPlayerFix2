package org.hyperskill.musicplayer.internals

import android.app.Activity
import android.media.MediaPlayer
import org.junit.Assert
import org.robolectric.shadows.ShadowMediaPlayer

open class MusicPlayerUnitTest<T : Activity>(clazz: Class<T>) : AbstractUnitTest<T>(clazz) {

    init {
        CustomMediaPlayerShadow.setCreateListener(::onMediaPlayerCreated)
    }

    private var playerPrivate: MediaPlayer? = null
    private var shadowPlayerPrivate: ShadowMediaPlayer? = null

    protected var player: MediaPlayer
        get() {
            Assert.assertNotNull("No MediaPlayer was found", playerPrivate)
            return this.playerPrivate!!
        }
        set(_) {}

    protected var shadowPlayer: ShadowMediaPlayer
        get() {
            Assert.assertNotNull("No MediaPlayer was found", playerPrivate)
            return this.shadowPlayerPrivate!!
        }
        set(_) {}

    private fun onMediaPlayerCreated(player: MediaPlayer, shadow: ShadowMediaPlayer) {
        playerPrivate = player
        shadowPlayerPrivate = shadow
    }
}