package org.hyperskill.musicplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongRecyclerViewAdapter(
    private val songList: List<Song>,
    private val onListItemClick: (position: Int, button: ImageButton, songId: Long) -> Unit)
    : RecyclerView.Adapter<SongRecyclerViewAdapter.SongViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songList[position]
        holder.itemView.findViewById<TextView>(R.id.songTitleTv).text = song.title
        holder.itemView.findViewById<TextView>(R.id.songArtistTv).text = song.artist
        holder.itemView.findViewById<TextView>(R.id.songDurationTv).text = Timer.timeString(song.duration.toInt())
        val songStatusImgBtn = holder.itemView.findViewById<ImageButton>(R.id.songStatusImgBtn)

        songStatusImgBtn.setOnClickListener {
            onListItemClick(position, songStatusImgBtn, song.id)
        }
    }

    override fun getItemCount(): Int {
        return songList.size
    }

    class SongViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}