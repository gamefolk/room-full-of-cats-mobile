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

import com.arcadeoftheabsurd.absurdengine.GameView.GameLoadListener;
import com.arcadeoftheabsurd.absurdengine.RunnerThread.UpdateListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

/**
 * Entry point for Absurd Engine games 
 * Maintains a RunnerThread that updates a GameView FPS times per second,
 * executing its update logic asynchronously and invalidating it on the UI thread 
 * @author sam
 */

public abstract class GameActivity extends Activity implements GameLoadListener, UpdateListener
{    
    private RunnerThread gameRunner;
    private Handler gameHandler;
    private GameView game;
    
    protected abstract GameView initializeGame();
    protected abstract View initializeContentView();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        
        // implicitly bind handler to UI thread's message queue (explicit binding does not work on iOS)
        gameHandler = new Handler();
        gameRunner = new RunnerThread(this);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        if (SoundManager.isInitialized()) {
        	SoundManager.resumeAll();
        }
        if (gameRunner.paused()) {
            gameRunner.unpause();
        } else {
            Timer.reset();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        if (SoundManager.isInitialized()) {
        	SoundManager.pauseAll();
        }
        if (isFinishing()) {
        	if (SoundManager.isInitialized()) {
        		SoundManager.releaseAll();
        	}
            gameRunner.finish();
        } else {
            gameRunner.pause();
        }
    }
    
    @Override
    protected void onStop() {
        super.onStop();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }
    
    protected final void loadContent() {
    	gameHandler.post (
            new Runnable() {
                public void run() {
                	// post to UI thread
                 	game = initializeGame();
                 	setContentView(initializeContentView());
                }
            }
        );
    }
    
    @Override
    public void gameLoaded() {
    	gameRunner.start();
    }
    
    @Override
    public void update() 
    {
        // run in update thread
        game.updateAsync();
        gameHandler.post (
            new Runnable() {
                public void run() {
                    // post to UI thread
                	game.updateUI();
                    game.invalidate();  
                }
            }
        );   
    }  
}
