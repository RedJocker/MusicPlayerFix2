package org.hyperskill.musicplayer;

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

    @Implementation
    public static MediaPlayer create(Context context, int resid){
        DataSource ds = DataSource.toDataSource(String.valueOf(resid));
        final int fakeSongDuration=  210_000;
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
}
