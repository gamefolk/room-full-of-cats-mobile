package org.gamefolk.roomfullofcats;

/**
 * Simple interface to play music in a device-independent way. Mirrors the JavaFX MediaPlayer API.
 *
 * @see javafx.scene.media.MediaPlayer
 */
public interface MusicPlayer {
    void play();
    void setCycleCount(int value);
    void stop();
}
