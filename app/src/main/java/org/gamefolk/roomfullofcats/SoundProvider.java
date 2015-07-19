package org.gamefolk.roomfullofcats;

public interface SoundProvider {
    Sound loadSound(String filename);

    MusicPlayer loadMusic(String filename);
}