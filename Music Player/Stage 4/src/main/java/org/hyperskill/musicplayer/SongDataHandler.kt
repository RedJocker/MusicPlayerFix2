package org.hyperskill.musicplayer

import android.content.ContentResolver
import android.provider.MediaStore

fun getSongListFromStorage(musicResolver: ContentResolver): List<Song> {
    val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.DURATION
    )

    val musicCursor = musicResolver.query(musicUri, projection, null,
        null, null, null)

    return musicCursor?.use {
        generateSequence {
            if(it.moveToNext()) {
                val id = it.getLong(0)
                val artist = it.getString(1)
                val title = it.getString(2)
                val duration  = it.getLong(3)
                Song(title, artist, id, duration)
            } else {
                null
            }
        }.toList()
    } ?: listOf()
}