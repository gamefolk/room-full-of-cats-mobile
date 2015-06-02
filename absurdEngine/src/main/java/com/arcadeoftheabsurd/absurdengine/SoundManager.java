/*
 * AbsurdEngine (https://bitbucket.org/smpsnr/absurdengine/) 
 * (c) by Sam Posner (http://www.arcadeoftheabsurd.com/)
 *
 * AbsurdEngine is licensed under a
 * Creative Commons Attribution 4.0 International License
 *
 * You should have received a copy of the license along with this
 * work. If not, see http://creativecommons.org/licenses/by/4.0/ 
 */

package com.arcadeoftheabsurd.absurdengine;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

/**
 * Static class for playing music and sound effects
 * @author sam
 */

public class SoundManager 
{
	private static boolean initialized = false;
	private static int channels;
	private static AssetManager assetManager;
	private static MediaPlayer[] mediaChannels;
	private static boolean[] paused;
	
	public static void initializeSound(AssetManager assetManager, int channels) {
		SoundManager.assetManager = assetManager;
		SoundManager.channels = channels;
		mediaChannels = new MediaPlayer[channels];
		paused = new boolean[channels];
		
		for (int i = 0; i < channels; i++) {
			mediaChannels[i] = new MediaPlayer();
		}
		initialized = true;
	}
	
	public static void loadSound(String assetName, int channel) throws IOException {
		AssetFileDescriptor asset = assetManager.openFd(assetName);
        mediaChannels[channel].setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
        mediaChannels[channel].prepare();
	}
	
	public static int numChannels() {
		return channels;
	}
	
	public static boolean isInitialized() {
		return initialized;
	}
	
	public static void pauseAll() {
		for (int c = 0; c < channels; c++) {
    		if (isPlaying(c)) {
    			pauseSound(c);
    		}
    	}
	}
	
	public static void resumeAll() {
		for (int c = 0; c < channels; c++) {
    		if (isPaused(c)) {
    			playSound(c);
    		}
    	}
	}
	
	public static void releaseAll() {
		for (MediaPlayer m : mediaChannels) {
			m.release();
		}
		initialized = false;
	}
	
	public static void setVolume(int channel, float leftVolume, float rightVolume) {
		mediaChannels[channel].setVolume(leftVolume, rightVolume);
	}
	
	public static void playSound(int channel) {
		mediaChannels[channel].start();
		paused[channel] = false;
	}
	
	public static void loopSound(int channel) {
		mediaChannels[channel].setLooping(true);
		mediaChannels[channel].start();
		paused[channel] = false;
	}
	
	public static void pauseSound(int channel) {
		mediaChannels[channel].pause();
		paused[channel] = true;
	}
	
	public static boolean isPlaying(int channel) {
		return mediaChannels[channel].isPlaying();
	}
	
	public static boolean isPaused(int channel) {
		return paused[channel];
	}
}
