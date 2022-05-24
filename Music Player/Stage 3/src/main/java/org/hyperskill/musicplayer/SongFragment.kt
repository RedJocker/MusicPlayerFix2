package org.hyperskill.musicplayer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView

class SongFragment(
    private val title: String,
    private val artist: String,
    private val id: Long,
    private val position: Int,
    private val activity: MainActivity
) : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_song, container, false)

        if (view != null) {
            val titleView = view.findViewById<TextView>(R.id.songTitleTv)
            val artistView = view.findViewById<TextView>(R.id.songArtistTv)
            val playButton = view.findViewById<ImageButton>(R.id.songStatusImgBtn)

            titleView?.text = title
            artistView.text = artist
            playButton.setOnClickListener {
                println("Fragment playButton clicked for " +
                        "id: $id, position: $position, title: $title, artist: $artist"
                )
            }
        }

        return view
    }
}