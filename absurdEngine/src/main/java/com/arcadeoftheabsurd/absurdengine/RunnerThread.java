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

/**
 * Thread that calls its UpdateListener's update method FPS times per second 
 * @author sam
 */

public class RunnerThread extends Thread
{    
	public static final int FPS = 30;
	
	private final Object pauseLock = new Object();
    private final int SKIP_MILLISECONDS = 1000 / FPS;
	
	private boolean threadRunning = false;
    private boolean threadPaused = false;
    
    private UpdateListener listener;
    
    private long sleepTime = 0;
    private long gameTime;
    
    public interface UpdateListener
    {
    	public void update();
    }
    
    public RunnerThread(UpdateListener listener) {
        this.listener = listener;
        this.setName("update thread");
    }
    
    public void finish() {
        boolean retry = true;

        threadRunning = false;

        // wait for this thread to close before completing activity destruction
        while (retry) {
            try {
                join();
                retry = false;
            } catch (InterruptedException e) {
            	Thread.currentThread().interrupt();
            }
        }
    }
    
    public void pause() {
        threadPaused = true;
    }
    
    public boolean paused() {
        return threadPaused;
    }
    
    public void unpause() {
        // monitor pauseLock
        synchronized (pauseLock) {
            threadPaused = false;
            gameTime = System.currentTimeMillis();
            // wake up the thread waiting on pauseLock (this one)
            pauseLock.notifyAll();
        }
    }
    
    @Override
    public void run() {    
        while (threadRunning) {
            synchronized (pauseLock) {
                while (threadPaused) {
                    try {
                        // force this thread to wait on pauseLock's monitor
                        pauseLock.wait();
                    } catch (InterruptedException e) {
                    	Thread.currentThread().interrupt();
                    }               
                }
            }                
            listener.update();
            
            // if the thread is running faster than the specified FPS, sleep for the extra time
            gameTime += SKIP_MILLISECONDS;
            sleepTime = gameTime - System.currentTimeMillis();
            
            if (sleepTime >= 0) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                	Thread.currentThread().interrupt();
                }          
            }  
        }
    }
    
    @Override
    public void start() {
        threadRunning = true;
        gameTime = System.currentTimeMillis();
        super.start();
    }
}
