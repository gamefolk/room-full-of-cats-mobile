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

import java.util.Observable;
import java.util.Observer;

import com.arcadeoftheabsurd.j_utils.Delegate;

/**
 * Schedules a delegate in a GameView to be executed on a timer
 * @author sam
 */

public abstract class Timer extends Observable
{    
	public static int curId = 0;
	
	public Delegate method;
	
	protected TimerListener listener;
    private boolean running;
    private float delay;
        
    private final int id;
    private final float time;
    private final float delayInc = (float) 1 / RunnerThread.FPS;
    
    protected abstract void addToContext();
    protected abstract void removeFromContext();
    
    abstract interface TimerListener extends Observer {}
    
    interface TimerAsyncListener extends TimerListener
	{
		public void addAsyncTimer(TimerAsync timer);
		public void removeAsyncTimer(TimerAsync timer);
	}

    interface TimerUIListener extends TimerListener
	{
		public void addUITimer(TimerUI timer);
		public void removeUITimer(TimerUI timer);
	}
    
    public static class TimerAsync extends Timer
    {
    	public TimerAsync(float time, TimerAsyncListener listener, Delegate method) {
    		super(time, listener, method);
    	}

    	@Override
    	protected void addToContext() {
    		((TimerAsyncListener)listener).addAsyncTimer(this);
    	}

    	@Override
    	protected void removeFromContext() {
    		((TimerAsyncListener)listener).removeAsyncTimer(this);
    	}
    }
    
    public static class TimerUI extends Timer
    {    	
    	public TimerUI(float time, TimerUIListener listener, Delegate method) {
    		super(time, listener, method);
    	}
    	
    	@Override
    	protected void addToContext() {
    		((TimerUIListener)listener).addUITimer(this); 
    	}

    	@Override
    	protected void removeFromContext() {
    		((TimerUIListener)listener).removeUITimer(this);
    	}
    }
    
    public Timer(float time, TimerListener listener, Delegate method) {
        this.time = time;
        this.listener = listener;
        this.method = method;

        id = curId++;
        delay = time;  
    }
    
    public void tic() {
        if (running) {
            delay -= delayInc;
            
            if (delay <= 0) {
                setChanged();
                notifyObservers(id);
                delay = time;
            }
        }
    }
   
    public void start() {
        running = true;
        addObserver(listener);
        addToContext();  
    }
    
    public void end() {
        running = false;
        this.deleteObservers();
        removeFromContext();
    }
    
    public void resume() {
        running = true;
    }
    
    public void pause() {
        running = false;
    }
    
    public static void reset() {
        curId = 0;
    }
    
    public boolean isRunning() {
    	return running;
    }
}
