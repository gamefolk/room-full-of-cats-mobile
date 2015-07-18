package org.gamefolk.roomfullofcats;

import javafx.scene.media.MediaPlayer;
import org.robovm.apple.audiotoolbox.AudioServices;
import org.robovm.apple.avfoundation.AVAudioPlayer;
import org.robovm.apple.corefoundation.OSStatusException;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.foundation.NSErrorException;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.foundation.NSURL;

import java.io.File;

public class IOSSoundProvider implements SoundProvider {

    @Override
    public Sound loadSound(String filename) {
        return new IOSSound(filename);
    }

    @Override
    public MusicPlayer loadMusic(String filename) {
        return new IOSMusicPlayer(filename);
    }
}

class IOSSound implements Sound {

    private int soundId;

    public IOSSound(String filename) {
        PlatformService platformService = PlatformService.getInstance();
        File soundFile = platformService.loadJarResourceStreamAsFile(filename);
        NSURL soundUrl = new NSURL(soundFile);
        try {
            soundId = AudioServices.createSystemSoundID(soundUrl);
        } catch (OSStatusException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void play() {
        AudioServices.playSystemSound(soundId);
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCycleCount(int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPlaying() {
        // Only one sound can ever play at once, so we'll just report that the sound is never playing.
        return false;
    }
}

class IOSMusicPlayer implements MusicPlayer {

    private AVAudioPlayer audioPlayer;

    public IOSMusicPlayer(String filename) {
        PlatformService platformService = PlatformService.getInstance();
        File musicFile = platformService.loadJarResourceStreamAsFile(filename);
        NSURL musicUrl = new NSURL(musicFile);
        try {
            audioPlayer = new AVAudioPlayer(musicUrl);
        } catch (NSErrorException e) {
            throw new RuntimeException(e);
        }
        audioPlayer.prepareToPlay();
    }

    @Override
    public void play() {
        audioPlayer.play();
    }

    @Override
    public void setCycleCount(int value) {
        if (value == MediaPlayer.INDEFINITE) {
            audioPlayer.setNumberOfLoops(-1);
        } else {
            audioPlayer.setNumberOfLoops(value);
        }
    }

    @Override
    public void stop() {
        audioPlayer.stop();
    }
}
