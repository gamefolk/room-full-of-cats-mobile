package org.gamefolk.roomfullofcats;

import java.io.IOException;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.widget.TextView;

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
	ScoreView scoreView;
	
	static final int NUM_CHANNELS = 3;
	private static final int SONG_CHANNEL = 0;
	private static final int BLIP_CHANNEL = 1;
	private static final int SCORE_CHANNEL = 2;
	
	private final Vector2d mapSize = new Vector2d(2, 5); // in columns, rows
	private final int incSize = 10; // the amount by which to increase the size of things as they collect
	private final Cat[][] map = new Cat[mapSize.x][mapSize.y];
	
	private Vector2d mapLoc; // in pixels, the top left corner of the top left column of things on the screen
	private Vector2d catSize; // in pixels, set according to the size of the screen in onSizeChanged()
	
	private final int fallTime = 1; // interval after which things fall, in seconds
	private final int thingsLimit = 3; // the target number of things of the same type to collect
	private int score = 0;
	private TimerAsync fallTimer;
	
	private final Random rGen = new Random();
	
	class ScoreView extends TextView
	{
		public ScoreView(Context context) {
			super(context);
			setTextSize(DeviceUtility.isIOS() ? 12 : 20);
			printScore();
		}
		
		public void printScore() {
			setText("Score: " + score);
		}
	}
	
	private class Cat
	{
		public int things = 0;
		public CatType type;
		public Sprite sprite;
		
		int curFrame = 0;
				
		public Cat (CatType type, Sprite sprite) {
			this.type = type;
			this.sprite = sprite;
			
			if (type == CatType.GRAYCAT) {
				new TimerUI(.2f, CatsGame.this, new Delegate() {
					public void function(Object... args) {
						CatsGame.this.swapSprite(Cat.this.sprite, Cat.this.type.bitmapFrames[curFrame++]);
						if (curFrame == Cat.this.type.bitmapFrames.length) {
							curFrame = 0;
						}
					}
				}).start();
			}
		}
	}

	private enum CatType
	{
		BLUECAT(R.drawable.bluecat), GRAYCAT(R.drawable.graycat), PINKCAT(R.drawable.pinkcat), STRIPECAT(R.drawable.stripecat);
		
		private int resourceId;
		private int bitmapId = -1;
		
		public int[] bitmapFrames;
		
		private CatType(int resourceId) {
			this.resourceId = resourceId;
		}
		
		public void setBitmap(int bitmapId) {
			this.bitmapId = bitmapId;
		}
	}
	
	public CatsGame(Context context, GameLoadListener loadListener) {
		super(context, loadListener);
		
		scoreView = new ScoreView(context);
		
		fallTimer = new TimerAsync(fallTime, this, new Delegate() {
			public void function(Object... args) {		
				// move bottom row into buckets  
				for (int x = 0; x < mapSize.x; x++) {
					Cat candidate = map[x][mapSize.y-2];

					if (candidate != null) {
						Cat current = map[x][mapSize.y-1];
						
						if (current != null) {	
							if (candidate.type == current.type) {								
								current.sprite.resize(current.sprite.getWidth() + incSize, current.sprite.getHeight() + incSize);
								current.things++;
								
								if (current.things == thingsLimit) {
									score++;
									if (!SoundManager.isPlaying(SCORE_CHANNEL)) {
										SoundManager.playSound(SCORE_CHANNEL);
									}
									map[x][mapSize.y-1] = null;
								}
							} else {
								current = null;
								candidate.sprite.translate(0, catSize.y * 2);
								map[x][mapSize.y-1] = candidate;
							}
						} else {
							candidate.sprite.translate(0, catSize.y * 2);
							map[x][mapSize.y-1] = candidate;
						}
					}
				}
				// descend cats in all other rows 
				for (int y = mapSize.y-2; y > 0; y--) {
					for (int x = 0; x < mapSize.x; x++) {
						if (map[x][y-1] != null) {
							map[x][y-1].sprite.translate(0, catSize.y);
						}
						map[x][y] = map[x][y-1];
					}
				}
				// fill the top row with new cats
				for (int x = 0; x < mapSize.x; x++) {
					CatType type = CatType.values()[rGen.nextInt(4)];					
					map[x][0] = new Cat(type, makeSprite(type.bitmapId, mapLoc.x + (x * catSize.x), mapLoc.y));
				}
			}
		});
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		for (int x = 0; x < mapSize.x; x++) {
			for (int y = 0; y < mapSize.y-1; y++) {
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
		for (int x = 0; x < mapSize.x; x++) {
			for (int y = 0; y < mapSize.y; y++) {
				if (map[x][y] != null) {
					map[x][y].sprite.draw(canvas);
				}
			}
		}
		scoreView.printScore();
	}

	@Override
	protected void setupGame(int screenWidth, int screenHeight) {
		// room for each column of cats + 1 cat-width worth of margin on the sides
		int tempX = screenWidth / (mapSize.x + 1); 
		// room for each row of cats + 2 cat-heights worth of margin on the top and bottom, 
		// plus the gap between the last row of cats and the cat buckets
		int tempY = screenHeight / (mapSize.y + 2);
		
		int catXY = tempX < tempY ? tempX : tempY;
		
		catSize = new Vector2d(catXY, catXY);
		mapLoc = new Vector2d((screenWidth - (mapSize.x * catSize.x)) / 2, (screenHeight - ((mapSize.y + 1) * catSize.y)) / 2);
		
		CatType.BLUECAT.setBitmap  (loadBitmapResource(CatType.BLUECAT.resourceId,   catSize));
		//CatType.GRAYCAT.setBitmap  (loadBitmapResource(CatType.GRAYCAT.resourceId,   catSize));
		CatType.PINKCAT.setBitmap  (loadBitmapResource(CatType.PINKCAT.resourceId,   catSize));
		CatType.STRIPECAT.setBitmap(loadBitmapResource(CatType.STRIPECAT.resourceId, catSize));
		
		int frame1 = loadBitmapResource(R.drawable.graycat1,   catSize);
		int frame2 = loadBitmapResource(R.drawable.graycat2,   catSize);
		int frame3 = loadBitmapResource(R.drawable.graycat3,   catSize);
		
		CatType.GRAYCAT.bitmapFrames = new int[] {frame1, frame2, frame3, frame2};
		CatType.GRAYCAT.setBitmap(frame1);
		
		try {
        	SoundManager.loadSound("song.mp3", SONG_CHANNEL);	
        	SoundManager.loadSound("blip.wav", BLIP_CHANNEL);	
        	SoundManager.loadSound("score.wav", SCORE_CHANNEL);	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void startGame() {
		fallTimer.start();	
		SoundManager.loopSound(SONG_CHANNEL);
	}
	
	@Override
	protected void updateGame() {}
}