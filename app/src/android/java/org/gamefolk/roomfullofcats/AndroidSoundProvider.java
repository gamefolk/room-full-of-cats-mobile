package org.gamefolk.roomfullofcats;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import javafx.scene.media.MediaPlayer;
import javafxports.android.FXActivity;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class AndroidSoundProvider implements SoundProvider {
    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());

    static {
        Context context = FXActivity.getInstance();

        // FIXME: This does not work
        // Use the hardware buttons to manipulate sound volume.
        ((Activity) context).setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    /**
     * This method allows loading a JAR resource as a File.
     * <p>
     * First, a brief warning:
     * <pre>
     *                                                 ,  ,
     *                                                / \/ \
     *                                               (/ //_ \_
     *      .-._                                      \||  .  \
     *       \  '-._                            _,:__.-"/---\_ \
     *  ______/___  '.    .--------------------'~-'--.)__( , )\ \
     * `'--.___  _\  /    |                         ,'    \)|\ `\|
     *      /_.-' _\ \ _:,_       Here be dragons!        " ||   (
     *    .'__ _.' \'-/,`-~`                                |/
     *        '. ___.> /=,|                                 |
     *         / .-'/_ )  '---------------------------------'
     *    snd  )'  ( /(/
     *              \\ "
     *               '=='
     * </pre>
     * <p>
     * Why use this deep magic?
     * <p>
     * On Android, we want to avoid using the R object (this would require us to copy the sound assets to the
     * androidResources folder as well), so we use methods that load either a path or a URI, such as {@link
     * android.media.SoundPool#load(String, int) SoundPool.load(String, int)} or
     * {@link android.media.MediaPlayer#create(Context, Uri) MediaPlayer.create(Context, Uri)} to load sound
     * files.
     * <p>
     * However, these methods can't read resources that have been packed into the JAR. Therefore, we read the
     * resource in as a stream, and write the stream to a temporary file. This file can then be read in by the
     * any Android method to load the sound.
     * <p>
     * In short, this hack lets us keep the sound resources packed into main/resources, and still have Android
     * load them successfully.
     *
     * @param jarResource A String that represents a path in the JAR resources folder.
     * @returns A File containing the absolute path of a file containing that resource's data.
     */
    protected static File loadJarResourceStreamAsFile(String jarResource) {
        File file;
        try {
            file = convertInputStreamToFile(AndroidSoundProvider.class.getResourceAsStream(jarResource));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    private static File convertInputStreamToFile(InputStream inputStream) throws IOException {
        File tempFile;
        OutputStream outputStream = null;
        try {
            Context context = FXActivity.getInstance();
            tempFile = File.createTempFile("sound", null, context.getCacheDir());
            outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[10 * 1024];   // 10KB
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            Log.info("Wrote temporary file " + tempFile.getName() + " with size " + tempFile.length());
            return tempFile;
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
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

    private android.media.MediaPlayer mediaPlayer;

    public AndroidMusicPlayer(String filename) {
        Context context = FXActivity.getInstance();
        Uri uri = Uri.fromFile(AndroidSoundProvider.loadJarResourceStreamAsFile(filename));
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
        String path = AndroidSoundProvider.loadJarResourceStreamAsFile(filename).getAbsolutePath();
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
