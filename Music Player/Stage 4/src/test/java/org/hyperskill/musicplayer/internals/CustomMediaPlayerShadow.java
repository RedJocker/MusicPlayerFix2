package org.hyperskill.musicplayer.internals;

import static org.robolectric.shadows.ShadowMediaPlayer.State.INITIALIZED;

import android.content.ContentUris;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;


import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowMediaPlayer;
import org.robolectric.shadows.util.DataSource;

import static org.junit.Assert.assertEquals;

@Implements(MediaPlayer.class)
public class CustomMediaPlayerShadow extends ShadowMediaPlayer {

    private static SongFake fakeSong = new SongFake(
            "-1",
            "Guggenheim grotto",
            "Wisdom",
            210_000
    );

    @Implementation
    public static MediaPlayer create(Context context, int resid){
        DataSource dataSource = DataSource.toDataSource(String.valueOf(resid));
        MediaInfo info = new MediaInfo(fakeSong.getDuration(), 0);

        addMediaInfo(dataSource, info);

        MediaPlayer mediaPlayer = new MediaPlayer();
        ShadowMediaPlayer shadow = Shadow.extract(mediaPlayer);
        try {
            shadow.setDataSource(dataSource);
            shadow.setState(INITIALIZED);
            mediaPlayer.prepare();
        } catch (Exception e) {
            return null;
        }
        return mediaPlayer;
    }

    @Implementation
    public static MediaPlayer create(Context context, Uri trackUri){
        Uri expectedSongUri = ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(fakeSong.getId())
        );

        assertEquals("Media player created with incorrect uri", expectedSongUri.getPath(), trackUri.getPath());

        DataSource dataSource = DataSource.toDataSource(context, trackUri);
        MediaInfo info = new MediaInfo((int) fakeSong.getDuration(), 0);
        addMediaInfo(dataSource, info);

        MediaPlayer mediaPlayer = new MediaPlayer();
        ShadowMediaPlayer shadow = Shadow.extract(mediaPlayer);
        try {
            shadow.setDataSource(dataSource);
            shadow.setState(INITIALIZED);
            mediaPlayer.prepare();
        } catch (Exception e) {
            return null;
        }
        return mediaPlayer;
    }

    public static void setFakeSong(SongFake fakeSong) {
        CustomMediaPlayerShadow.fakeSong = fakeSong;
    }
}
