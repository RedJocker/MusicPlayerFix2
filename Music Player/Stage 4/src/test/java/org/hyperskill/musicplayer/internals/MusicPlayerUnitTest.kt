package org.hyperskill.musicplayer.internals

import android.app.Activity
import android.content.pm.ProviderInfo
import android.media.MediaPlayer
import android.provider.MediaStore
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.junit.Assert
import org.robolectric.Robolectric
import org.robolectric.shadows.ShadowMediaPlayer
import java.lang.AssertionError

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

    fun setupContentProvider(fakeSongResult: List<SongFake>){
        val info = ProviderInfo().apply {
            authority = MediaStore.AUTHORITY
        }
        Robolectric.buildContentProvider(FakeContentProvider::class.java).create(info)
        FakeContentProvider.fakeSongResult = fakeSongResult
    }

    fun RecyclerView.assertItems(fakeSongResult: List<SongFake>) {
        Assert.assertNotNull("Your recycler view adapter should not be null", this.adapter)

        val expectedSize = fakeSongResult.size

        val actualSize = this.adapter!!.itemCount
        Assert.assertEquals("Incorrect number of list items", expectedSize, actualSize)

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
                Assert.assertEquals("songTitleTv with incorrect text", actualSongTitle, song.title)
                val actualSongArtist = songArtistTv.text.toString()
                Assert.assertEquals(
                    "songArtistTv with incorrect text",
                    actualSongArtist,
                    song.artist
                )
            }
        }
    }
}