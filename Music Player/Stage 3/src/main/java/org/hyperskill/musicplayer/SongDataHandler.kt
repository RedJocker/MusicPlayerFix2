package org.hyperskill.musicplayer

import android.content.ContentResolver
import android.provider.MediaStore

fun getSongList(musicResolver: ContentResolver): List<Song> {
    val songList = mutableListOf<Song>()

    val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    val musicCursor = musicResolver.query(musicUri, null, null,
        null, null, null)

    musicCursor?.use {
        if (it.moveToFirst()) {
            val titleColumn = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val idColumn = it.getColumnIndex(MediaStore.Audio.Media._ID)
            val durationColumn = it.getColumnIndex(MediaStore.Audio.Media.DURATION)

            do {
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)
                val id = it.getLong(idColumn)
                val duration  = it.getLong(durationColumn)
                songList.add(Song(title, artist, id, duration))
            } while (it.moveToNext())
        }
    }

    return songList
}