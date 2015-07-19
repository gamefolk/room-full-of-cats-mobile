package org.gamefolk.roomfullofcats;

/**
 * Simple interface to play and loop sounds in a device-independent way.
 * Mirrors the JavaFX AudioClip API.
 *
 * @see javafx.scene.media.AudioClip
 */
public abstract class Sound {

    private static final Settings settings = Settings.INSTANCE;

    public final void play() {
        playSound();
    }

    protected abstract void playSound();

    public abstract void stop();

    public abstract void setCycleCount(int count);

    public abstract boolean isPlaying();
}
