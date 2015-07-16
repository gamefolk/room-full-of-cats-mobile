package org.gamefolk.roomfullofcats;

/**
 * Simple interface to play and loop sounds in a device-independent way.
 * Mirrors the JavaFX AudioClip API.
 *
 * @see javafx.scene.media.AudioClip
 */
public interface Sound {
    void play();
    void stop();
    void setCycleCount(int count);
    boolean isPlaying();
}
