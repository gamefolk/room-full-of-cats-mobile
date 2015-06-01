package org.gamefolk.roomfullofcats;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.arcadeoftheabsurd.absurdengine.BitmapResourceManager;
import com.arcadeoftheabsurd.absurdengine.DeviceUtility;
import com.arcadeoftheabsurd.absurdengine.GameView;
import com.arcadeoftheabsurd.absurdengine.SoundManager;
import com.arcadeoftheabsurd.absurdengine.Sprite;
import com.arcadeoftheabsurd.absurdengine.Timer.TimerAsync;
import com.arcadeoftheabsurd.absurdengine.Timer.TimerUI;
import com.arcadeoftheabsurd.j_utils.Delegate;
import com.arcadeoftheabsurd.j_utils.Vector2d;

public class CatsGame extends GameView
{
	View levelUIView;
	
	private TextView scoreView;
	private TextView titleView;
	private TextView timeView;
    
    static final int NUM_CHANNELS = 4;
    private static final int SONG_CHANNEL = 0;
    private static final int BLIP_CHANNEL = 1;
    private static final int SCORE_CHANNEL = 2;
    private static final int GLITCH_CHANNEL = 3;
    
    private final int incSize = 10; // the amount by which to increase the size of cats as they collect    
    
    private Cat[][] map;
    
    private int mapWidth;
    private int mapHeight;
    
    private Vector2d mapLoc; // in pixels, the top left corner of the top left column of cats on the screen
    private Vector2d catSize; // in pixels, set according to the size of the screen in onSizeChanged()
    
    private int score;
    private int curLevelTime;
    
    private TimerAsync fallTimer;
    private TimerUI animationTimer;
    private CountdownTimer levelTimer;
        
    private BitmapResourceManager bitmapResources;
    
    private final Random rGen = new Random();
        
    private static final String TAG = "RoomFullOfCats";
    
    private class Cat
    {
        public int things = 0;
        public CatType type;
        public Sprite sprite;
        
        private int curFrame = 0;
        private boolean glitched = false;
                
        public Cat (CatType type, Sprite sprite) {
            this.type = type;
            this.sprite = sprite;
        }
        
        /*public void toggleGlitch() {
            if (glitched) {
                glitched = false;
                CatsGame.this.setSpriteBitmap(Cat.this.sprite, Cat.this.type.bitmapFrames[curFrame]);
                animationTimer.resume();
            } else {
                glitched = true;
                animationTimer.pause();
                CatsGame.this.setSpriteBitmap(Cat.this.sprite, Cat.this.type.glitchFrame);
            }
        }*/
    }

    private enum CatType
    {
        BLUECAT, GRAYCAT, PINKCAT, STRIPECAT;
        
        private int bitmapId = -1;
        
        public int[] bitmapFrames;
        public int glitchFrame;
        
        public void setBitmap(int bitmapId) {
            this.bitmapId = bitmapId;
        }
    }
    
    public CatsGame(Context context, GameLoadListener loadListener) {
        super(context, loadListener);
        
        levelUIView = View.inflate(context, R.layout.level_ui, null);
        
        scoreView = (TextView) levelUIView.findViewById(R.id.levelScoreView);
        titleView = (TextView) levelUIView.findViewById(R.id.levelTitleView);
        timeView = (TextView) levelUIView.findViewById(R.id.levelTimeView);
        
        scoreView.setTextSize(DeviceUtility.isIOS() ? 12 : 20);
        titleView.setTextSize(DeviceUtility.isIOS() ? 12 : 20);
        timeView.setTextSize(DeviceUtility.isIOS() ? 12 : 20);
        
        bitmapResources = new BitmapResourceManager(16);
        loadGraphics();
    }
    
    private void drawUI() {
    	scoreView.setText("Score: " + score);
    	timeView.setText("Time: " + curLevelTime);
    }
    
    public void makeLevel(final Level level) {    	
    	this.mapWidth = level.mapWidth;
    	this.mapHeight = level.mapHeight;
    	
    	titleView.setText(level.title);
    	
    	map = new Cat[mapWidth][mapHeight];
    	
    	levelTimer = new CountdownTimer(level.levelTime, 1f, CatsGame.this) {
            @Override
            public void onFinish() {
                Log.v(TAG, "game over");
                animationTimer.pause();
                fallTimer.pause();
                for (Cat[] row : map) {
                    Arrays.fill(row, null);
                }
                CatsGameManager.curLevel++;
                CatsGameManager.startLevel();
            }

            @Override
            public void onTic(int remaining) {
            	curLevelTime = remaining;
            }
        };
        
        animationTimer = new TimerUI(.2f, CatsGame.this, new Delegate() {
            public void function(Object... args) {
                for (int x = 0; x < mapWidth; x++) {
                    for (int y = 0; y < mapHeight; y++) {
                        Cat cat = map[x][y];
                        if (cat != null) {
                            CatsGame.this.setSpriteBitmap(cat.sprite, cat.type.bitmapFrames[cat.curFrame++]);
                            if (cat.curFrame == cat.type.bitmapFrames.length) {
                                cat.curFrame = 0;
                            }
                        }
                    }
                }
            }
        });
        
        fallTimer = new TimerAsync(level.fallTime, this, new Delegate() {
            public void function(Object... args) {      
                // move bottom row into buckets  
                for (int x = 0; x < mapWidth; x++) {
                    Cat candidate = map[x][mapHeight-2];

                    if (candidate != null) {
                        Cat current = map[x][mapHeight-1];
                        
                        if (current != null) {  
                            if (candidate.type == current.type) {                               
                                current.sprite.resize(current.sprite.getWidth() + incSize, current.sprite.getHeight() + incSize);
                                current.things++;
                                
                                if (current.things == level.catsLimit) {
                                    score++;
                                    if (!SoundManager.isPlaying(SCORE_CHANNEL)) {
                                        SoundManager.playSound(SCORE_CHANNEL);
                                    }
                                    map[x][mapHeight-1] = null;
                                }
                            } else {
                                current = null;
                                candidate.sprite.translate(0, catSize.y);
                                map[x][mapHeight-1] = candidate;
                            }
                        } else {
                            candidate.sprite.translate(0, catSize.y);
                            map[x][mapHeight-1] = candidate;
                        }
                    }
                }
                // descend cats in all other rows 
                for (int y = mapHeight-2; y > 0; y--) {
                    for (int x = 0; x < mapWidth; x++) {
                        if (map[x][y-1] != null) {
                            map[x][y-1].sprite.translate(0, catSize.y);
                        }
                        map[x][y] = map[x][y-1];
                    }
                }
                // fill the top row with new cats
                for (int x = 0; x < mapWidth; x++) {
                    CatType type = CatType.values()[rGen.nextInt(4)];                   
                    map[x][0] = new Cat(type, makeSprite(type.bitmapId, mapLoc.x + (x * catSize.x), mapLoc.y));
                }
            }
        });
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight-1; y++) {
                if (map[x][y] != null) {
                    if (map[x][y].sprite.getBounds().contains((int)event.getX(), (int)event.getY())) {
                        map[x][y] = null;
                    }
                }
            }
        }
        return true;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {              
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                if (map[x][y] != null) {
                    map[x][y].sprite.draw(canvas);
                }
            }
        }
        drawUI();
    }

    private void loadGraphics() {
    	bitmapResources.loadBitmap(getResources(), R.drawable.bluecat1);
    	bitmapResources.loadBitmap(getResources(), R.drawable.bluecat2);
    	bitmapResources.loadBitmap(getResources(), R.drawable.bluecat3);
    	bitmapResources.loadBitmap(getResources(), R.drawable.bluecatgb);
    	bitmapResources.loadBitmap(getResources(), R.drawable.graycat1);
    	bitmapResources.loadBitmap(getResources(), R.drawable.graycat2);
    	bitmapResources.loadBitmap(getResources(), R.drawable.graycat3);
    	bitmapResources.loadBitmap(getResources(), R.drawable.graycatgb);
    	bitmapResources.loadBitmap(getResources(), R.drawable.pinkcat1);
    	bitmapResources.loadBitmap(getResources(), R.drawable.pinkcat2);
    	bitmapResources.loadBitmap(getResources(), R.drawable.pinkcat3);
    	bitmapResources.loadBitmap(getResources(), R.drawable.pinkcatgb);
    	bitmapResources.loadBitmap(getResources(), R.drawable.stripecat1);
    	bitmapResources.loadBitmap(getResources(), R.drawable.stripecat2);
    	bitmapResources.loadBitmap(getResources(), R.drawable.stripecat3);
    	bitmapResources.loadBitmap(getResources(), R.drawable.stripecatgb);
    }
    
    @Override
    protected void setupGame(int screenWidth, int screenHeight) {    	
        // room for each row/column of cats + 1 cat worth of margin on the sides
        int tempX = screenWidth / (mapWidth + 1); 
        int tempY = screenHeight / (mapHeight + 1);
        
        int catXY = tempX < tempY ? tempX : tempY;
        
        catSize = new Vector2d(catXY, catXY);
        mapLoc = new Vector2d((screenWidth - (mapWidth * catSize.x)) / 2, (screenHeight - (mapHeight * catSize.y)) / 2);
        
        int frame1, frame2, frame3, glitchFrame;
        
        Resources res = getResources();
        
        frame1 = loadBitmapResource(bitmapResources.getBitmap(res.getString(R.drawable.bluecat1)),   catSize);
        frame2 = loadBitmapResource(bitmapResources.getBitmap(res.getString(R.drawable.bluecat2)),   catSize);
        frame3 = loadBitmapResource(bitmapResources.getBitmap(res.getString(R.drawable.bluecat3)),   catSize);
        
        glitchFrame = loadBitmapResource(bitmapResources.getBitmap(res.getString(R.drawable.bluecatgb)), catSize);
        
        CatType.BLUECAT.bitmapFrames = new int[] {frame1, frame2, frame3, frame2};
        CatType.BLUECAT.glitchFrame = glitchFrame;
        CatType.BLUECAT.setBitmap(frame1);
        
        frame1 = loadBitmapResource(bitmapResources.getBitmap(res.getString(R.drawable.graycat1)),   catSize);
        frame2 = loadBitmapResource(bitmapResources.getBitmap(res.getString(R.drawable.graycat2)),   catSize);
        frame3 = loadBitmapResource(bitmapResources.getBitmap(res.getString(R.drawable.graycat3)),   catSize);
        
        glitchFrame = loadBitmapResource(bitmapResources.getBitmap(res.getString(R.drawable.graycatgb)), catSize);
        
        CatType.GRAYCAT.bitmapFrames = new int[] {frame1, frame2, frame3, frame2};
        CatType.GRAYCAT.glitchFrame = glitchFrame;
        CatType.GRAYCAT.setBitmap(frame1);
        
        frame1 = loadBitmapResource(bitmapResources.getBitmap(res.getString(R.drawable.pinkcat1)),   catSize);
        frame2 = loadBitmapResource(bitmapResources.getBitmap(res.getString(R.drawable.pinkcat2)),   catSize);
        frame3 = loadBitmapResource(bitmapResources.getBitmap(res.getString(R.drawable.pinkcat3)),   catSize);
        
        glitchFrame = loadBitmapResource(bitmapResources.getBitmap(res.getString(R.drawable.pinkcatgb)), catSize);
        
        CatType.PINKCAT.bitmapFrames = new int[] {frame1, frame2, frame3, frame2};
        CatType.PINKCAT.glitchFrame = glitchFrame;
        CatType.PINKCAT.setBitmap(frame1);
        
        frame1 = loadBitmapResource(bitmapResources.getBitmap(res.getString(R.drawable.stripecat1)), catSize);
        frame2 = loadBitmapResource(bitmapResources.getBitmap(res.getString(R.drawable.stripecat2)), catSize);
        frame3 = loadBitmapResource(bitmapResources.getBitmap(res.getString(R.drawable.stripecat3)), catSize);
        
        glitchFrame = loadBitmapResource(bitmapResources.getBitmap(res.getString(R.drawable.stripecatgb)), catSize);
        
        CatType.STRIPECAT.bitmapFrames = new int[] {frame1, frame2, frame3, frame2};
        CatType.STRIPECAT.glitchFrame = glitchFrame;
        CatType.STRIPECAT.setBitmap(frame1);
        
        try {
            SoundManager.loadSound(  "catsphone.mp3", SONG_CHANNEL);    
            SoundManager.loadSound("catsgbphone.mp3", GLITCH_CHANNEL);
            SoundManager.loadSound(       "blip.wav", BLIP_CHANNEL);    
            SoundManager.loadSound(      "score.wav", SCORE_CHANNEL);   
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void startGame() {
        fallTimer.start();  
        animationTimer.start();
        levelTimer.start();
        //SoundManager.setVolume(GLITCH_CHANNEL, 0, 0);
        SoundManager.loopSound(SONG_CHANNEL);
        //SoundManager.loopSound(GLITCH_CHANNEL);
    }
    
    @Override
    protected void updateGame() {
        /*if (soundGlitching && rGen.nextFloat() > .95) {
            soundGlitching = false;
            SoundManager.setVolume(GLITCH_CHANNEL, 0, 0);
            SoundManager.setVolume(SONG_CHANNEL, 1, 1);
            
            for (int x = 0; x < mapSize.x; x++) {
                for (int y = 0; y < mapSize.y; y++) {
                    if (map[x][y] != null) {
                        map[x][y].toggleGlitch();
                    }
                }
            }
        } else if (rGen.nextFloat() > .99) {
            soundGlitching = true;
            SoundManager.setVolume(GLITCH_CHANNEL, 1, 1);
            SoundManager.setVolume(SONG_CHANNEL, 0, 0);
            
            for (int x = 0; x < mapSize.x; x++) {
                for (int y = 0; y < mapSize.y; y++) {
                    if (map[x][y] != null) {
                        map[x][y].toggleGlitch();
                    }
                }
            }
        }*/
    }
}
