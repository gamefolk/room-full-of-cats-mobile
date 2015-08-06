package org.gamefolk.roomfullofcats;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;

import javafxports.android.FXActivity;

import java.util.logging.Logger;

/**
 * Shim class that immediately passes control to FXActivity.
 *
 * We use this to perform any Android-specific initialization.
 */
public class CatsActivity extends Activity {

    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This should work, once javafxports/android #73 and #75 are fixed.
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        Intent intent = new Intent(this, FXActivity.class);
        startActivity(intent);
    }
}
