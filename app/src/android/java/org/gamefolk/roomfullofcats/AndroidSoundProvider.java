package org.gamefolk.roomfullofcats;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import javafx.scene.media.MediaPlayer;
import javafxports.android.FXActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class AndroidSoundProvider implements SoundProvider {

    static {
        Context context = FXActivity.getInstance();

        // FIXME: This does not work
        // Use the hardware buttons to manipulate sound volume.
        ((Activity) context).setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public Sound loadSound(String filename) {
        return new AndroidSound(filename);
    }

    @Override
    public MusicPlayer loadMusic(String filename) {
        return new AndroidMusicPlayer(filename);
    }

}

class AndroidMusicPlayer implements MusicPlayer {
    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());

    private final android.media.MediaPlayer mediaPlayer;

    public AndroidMusicPlayer(String filename) {
        Context context = FXActivity.getInstance();
        PlatformService platformService = PlatformService.getInstance();
        Uri uri = Uri.fromFile(platformService.loadJarResourceStreamAsFile(filename));
        Log.info("Loading media file " + uri.getPath());
        mediaPlayer = android.media.MediaPlayer.create(context, uri);
    }

    @Override
    public void play() {
        mediaPlayer.start();
    }

    @Override
    public void setCycleCount(int value) {
        switch (value) {
            case 0:
                mediaPlayer.setLooping(false);
                break;
            case MediaPlayer.INDEFINITE:
                mediaPlayer.setLooping(true);
                break;
            default:
                throw new UnsupportedOperationException(
                        getClass() + " does not support looping a set number of times.");
        }
    }

    @Override
    public void stop() {
        mediaPlayer.stop();
    }
}

class AndroidSound implements Sound {
    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());

    private static final SoundPool soundPool;
    private static final Map<Integer, CountDownLatch> loadProgress = new HashMap<>();

    static {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            synchronized (loadProgress) {
                loadProgress.get(sampleId).countDown();
            }
            if (status == 0) {
                Log.info("Sound " + sampleId + " loaded successfully.");
            } else {
                Log.warning("Sound " + sampleId + " failed to load! Error code: " + status);
            }
        });
    }

    private final int soundId;
    private int cycleCount = 0;
    private float volume;

    public AndroidSound(String filename) {
        PlatformService platformService = PlatformService.getInstance();
        String path = platformService.loadJarResourceStreamAsFile(filename).getAbsolutePath();
        Log.info("Loading sound file: " + path);

        // Create a CountDownLatch to signal when the sound has loaded.
        CountDownLatch loadedSignal = new CountDownLatch(1);
        synchronized (loadProgress) {
            soundId = soundPool.load(path, 1);
            loadProgress.put(soundId, loadedSignal);
        }

        // Determine the volume that the sound should play at
        Context context = FXActivity.getInstance();
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actualVolume / maxVolume;

        // Now, we block on loading the sounds so they are guaranteed to be ready when we play them.
        try {
            loadedSignal.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    @Override
    public void play() {
        Log.info("Playing sound " + soundId + " with volume " + volume + " and cycle count " + cycleCount);
        soundPool.play(soundId, volume, volume, 1, cycleCount, 1);
    }

    @Override
    public void stop() {
        soundPool.stop(soundId);
    }

    @Override
    public void setCycleCount(int count) {
        if (count == MediaPlayer.INDEFINITE) {
            cycleCount = -1;
        } else {
            cycleCount = count;
        }
    }

    @Override
    public boolean isPlaying() {
        return false;   // FIXME
    }
}
