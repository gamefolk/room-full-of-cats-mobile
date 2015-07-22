package org.gamefolk.roomfullofcats;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class DesktopSoundProvider implements SoundProvider {
    @Override
    public Sound loadSound(String filename) {
        return new DesktopSound(filename);
    }

    @Override
    public MusicPlayer loadMusic(String filename) {
        return new DesktopMusicPlayer(filename);
    }
}

class DesktopMusicPlayer extends MusicPlayer {
    private final MediaPlayer mediaPlayer;

    public DesktopMusicPlayer(String filename) {
        Media media = new Media(getClass().getResource(filename).toString());
        mediaPlayer = new MediaPlayer(media);
    }

    @Override
    public void playMusic() {
        mediaPlayer.play();
    }

    @Override
    public void setCycleCount(int value) {
        mediaPlayer.setCycleCount(value);
    }

    @Override
    public void stop() {
        mediaPlayer.stop();
    }
}

class DesktopSound extends Sound {
    private final AudioClip clip;

    public DesktopSound(String filename) {
        clip = new AudioClip(RoomFullOfCatsApp.class.getResource(filename).toString());
    }

    @Override
    public void playSound() {
        clip.play();
    }

    @Override
    public void stop() {
        clip.stop();
    }

    @Override
    public void setCycleCount(int count) {
        clip.setCycleCount(count);
    }

    @Override
    public boolean isPlaying() {
        return clip.isPlaying();
    }
}
