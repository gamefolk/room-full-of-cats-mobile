package org.gamefolk.roomfullofcats;

/**
 * Simple interface to play music in a device-independent way. Mirrors the JavaFX MediaPlayer API.
 *
 * @see javafx.scene.media.MediaPlayer
 */
public abstract class MusicPlayer {

    private static final Settings settings = Settings.INSTANCE;

    public final void play() {
        if (settings.getBoolean("playMusic", true)) {
            playMusic();
        }
    }

    protected abstract void playMusic();

    public abstract void setCycleCount(int value);

    public abstract void stop();

    public abstract void pause();
}
