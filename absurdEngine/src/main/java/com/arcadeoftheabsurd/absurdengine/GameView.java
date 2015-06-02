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

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import java.util.ArrayList;
import java.util.Observable;

import com.arcadeoftheabsurd.absurdengine.Timer.TimerAsync;
import com.arcadeoftheabsurd.absurdengine.Timer.TimerAsyncListener;
import com.arcadeoftheabsurd.absurdengine.Timer.TimerUI;
import com.arcadeoftheabsurd.absurdengine.Timer.TimerUIListener;
import com.arcadeoftheabsurd.j_utils.Pair;
import com.arcadeoftheabsurd.j_utils.Vector2d;

/**
 * Encapsulates game logic and rendering methods
 * @author sam
 */

public abstract class GameView extends View implements TimerAsyncListener, TimerUIListener
{    	
	private ArrayList<TimerAsync> asyncTimers = new ArrayList<TimerAsync>();
	private ArrayList<TimerUI> uiTimers = new ArrayList<TimerUI>();
    
    private GameLoadListener loadListener;
    private ArrayList<BitmapHolder> bitmapStorage = new ArrayList<BitmapHolder>();
    
    protected abstract void startGame();
    protected abstract void setupGame(int width, int height);
    protected abstract void updateGame();
    
    public interface GameLoadListener
    {
    	void gameLoaded();
    }
    
    public GameView(Context context, GameLoadListener loadListener) {
        super(context);
        this.loadListener = loadListener;
    }
    
    @Override
	public void onSizeChanged(int newWidth, int newHeight, int oldWidth, int oldHeight) {
    	setupGame(newWidth, newHeight);
    	loadListener.gameLoaded();
    	startGame();
    }
    
    // called when timers fire
    public void update(Observable timer, Object id) {
    	((Timer)timer).method.function();
    }
     
    // called at RunnerThread.FPS on the update thread 
    void updateAsync() {
        for (Timer t : asyncTimers) {
            t.tic();
        }        
        updateGame();
    }       
    
    // called at RunnerThread.FPS on the UI thread
    void updateUI() {
    	for (Timer t : uiTimers) {
            t.tic();
        }    
    }
    
    public void addAsyncTimer(TimerAsync timer) {
    	asyncTimers.add(timer);
    }
    
    public void removeAsyncTimer(TimerAsync timer) {
    	asyncTimers.remove(timer);
    }
    
    public void addUITimer(TimerUI timer) {
    	uiTimers.add(timer);
    }
    
    public void removeUITimer(TimerUI timer) {
    	uiTimers.remove(timer);
    }
    
    protected Sprite makeSprite(int bitmapId, int x, int y) {
    	return new Sprite(bitmapStorage.get(bitmapId), x, y);
    }
    
    protected void setSpriteBitmap(Sprite sprite, int bitmapId) {
    	sprite.setBitmap(bitmapStorage.get(bitmapId));
    }
    
	protected ArrayList<Integer> loadBitmapResources(Pair<Integer, Vector2d>... args) {
    	ArrayList<Integer> bitmapIds = new ArrayList<Integer>();
		for (int i = 0; i < args.length; i++) {
			bitmapIds.add(bitmapStorage.size());
			bitmapStorage.add(new BitmapHolder(BitmapFactory.decodeResource(getResources(), args[i].a), args[i].b.x, args[i].b.y));
		}
		return bitmapIds;
	}
    
    protected int loadBitmapResource(int resourceId, Vector2d initialSize) {
    	int bitmapId = bitmapStorage.size();
    	bitmapStorage.add(new BitmapHolder(BitmapFactory.decodeResource(getResources(), resourceId), initialSize.x, initialSize.y));
    	return bitmapId;
    }
    
    protected int loadBitmapHolder(BitmapHolder holder) {
    	int bitmapId = bitmapStorage.size();
    	bitmapStorage.add(holder);
    	return bitmapId;
    }
    
    protected int loadTempBitmapFile(String filePath, String fileName, Vector2d initialSize) {
    	int bitmapId = bitmapStorage.size();
    	bitmapStorage.add(new BitmapTempFileHolder(
    			((BitmapDrawable) BitmapDrawable.createFromPath(filePath)).getBitmap(),
    			initialSize.x, initialSize.y, fileName, getContext()));
    	return bitmapId;
    }
}
