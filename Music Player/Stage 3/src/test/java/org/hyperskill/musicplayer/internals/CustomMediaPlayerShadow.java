package org.hyperskill.musicplayer.internals;

import static org.robolectric.shadows.ShadowMediaPlayer.State.INITIALIZED;

import android.content.Context;
import android.media.MediaPlayer;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowMediaPlayer;
import org.robolectric.shadows.util.DataSource;

@Implements(MediaPlayer.class)
public class CustomMediaPlayerShadow extends ShadowMediaPlayer {

    private static int fakeSongDuration = 210_000;

    @Implementation
    public static MediaPlayer create(Context context, int resid){
        DataSource ds = DataSource.toDataSource(String.valueOf(resid));
        addMediaInfo(ds, new ShadowMediaPlayer.MediaInfo(fakeSongDuration, 0));

        MediaPlayer mp = new MediaPlayer();
        ShadowMediaPlayer shadow = Shadow.extract(mp);
        try {
            shadow.setDataSource(ds);
            shadow.setState(INITIALIZED);
            mp.prepare();
        } catch (Exception e) {
            return null;
        }
        return mp;
    }

    public static void setFakeSongDuration(int fakeSongDuration) {
        CustomMediaPlayerShadow.fakeSongDuration = fakeSongDuration;
    }
}
