package org.gamefolk.roomfullofcats;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;

import com.arcadeoftheabsurd.absurdengine.GameView;
import com.arcadeoftheabsurd.absurdengine.Sprite;
import com.arcadeoftheabsurd.absurdengine.Timer;
import com.arcadeoftheabsurd.j_utils.Delegate;
import com.arcadeoftheabsurd.j_utils.Vector2d;

public class CatsGame extends GameView
{
	private final Vector2d mapSize = new Vector2d(6, 10); // in columns, rows
	//private final int bucketSpace = 60; // vertical pixels between the bottom of the columns and the buckets
	private final int incSize = 10; // the amount by which to increase the size of things as they collect
	private final Thing[][] map = new Thing[mapSize.x][mapSize.y];
	
	private Vector2d mapLoc; // in pixels, the top left corner of the top left column of things on the screen
	private Vector2d thingSize; // in pixels, set according to the size of the screen in onSizeChanged()
	
	private final int fallTime = 1; // interval after which things fall, in seconds
	private final int thingsLimit = 3; // the target number of things of the same type to collect
	private int score = 0;
	private Timer fallTimer;
	
	private final Random rGen = new Random();
	
	public CatsGame(Context context, GameLoadListener loadListener) {
		super(context, loadListener);
		
		fallTimer = new Timer(fallTime, this, new Delegate() {
			public void function(Object... args) {		
				// move bottommost row into buckets  
				for (int x = 0; x < mapSize.x; x++) {
					Thing candidate = map[x][mapSize.y-2];

					if (candidate != null) {
						Thing current = map[x][mapSize.y-1];
						
						if (current != null) {	
							if (candidate.type == current.type) {								
								current.sprite.resize(current.sprite.getWidth() + incSize, current.sprite.getHeight() + incSize);
								current.things++;
								
								if (current.things == thingsLimit) {
									score++;
									map[x][mapSize.y-1] = null;
								}
							} else {
								current = null;
								candidate.sprite.translate(0, thingSize.y * 2);
								map[x][mapSize.y-1] = candidate;
							}
						} else {
							candidate.sprite.translate(0, thingSize.y * 2);
							map[x][mapSize.y-1] = candidate;
						}
					}
				}
				// descend things in all other rows 
				for (int y = mapSize.y-2; y > 0; y--) {
					for (int x = 0; x < mapSize.x; x++) {
						if (map[x][y-1] != null) {
							map[x][y-1].sprite.translate(0, thingSize.y);
						}
						map[x][y] = map[x][y-1];
					}
				}
				// fill the top row with new things
				for (int x = 0; x < mapSize.x; x++) {
					ThingType type = ThingType.values()[rGen.nextInt(4)];					
					map[x][0] = new Thing(type, makeSprite(type.bitmapId, mapLoc.x + (x * thingSize.x), mapLoc.y));
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
	public void onDraw(Canvas canvas) {				
		for (int x = 0; x < mapSize.x; x++) {
			for (int y = 0; y < mapSize.y; y++) {
				if (map[x][y] != null) {
					map[x][y].sprite.draw(canvas);
				}
			}
		}
	}

	@Override
	protected void setupGame(int screenWidth, int screenHeight) {
		thingSize = new Vector2d(screenWidth / (mapSize.x + 1), screenHeight / (mapSize.y + 2));
		mapLoc = new Vector2d((screenWidth - (mapSize.x * thingSize.x)) / 2, (screenHeight - ((mapSize.y + 1) * thingSize.y)) / 2);
		
		ThingType.GEAR.setBitmap   (loadBitmapResource(ThingType.GEAR.resourceId,    thingSize));
		ThingType.SHROOM.setBitmap (loadBitmapResource(ThingType.SHROOM.resourceId,  thingSize));
		ThingType.CRYSTAL.setBitmap(loadBitmapResource(ThingType.CRYSTAL.resourceId, thingSize));
		ThingType.ROCK.setBitmap   (loadBitmapResource(ThingType.ROCK.resourceId,    thingSize));
	}
	
	@Override
	protected void startGame() {
		fallTimer.start();	
	}
	
	@Override
	protected void updateGame() {}

	private class Thing
	{
		public int things = 0;
		public ThingType type;
		public Sprite sprite;
		
		public Thing (ThingType type, Sprite sprite) {
			this.type = type;
			this.sprite = sprite;
		}
	}

	private enum ThingType
	{
		GEAR(R.drawable.gear), SHROOM(R.drawable.shroom), CRYSTAL(R.drawable.crystal), ROCK(R.drawable.rock);
		
		private int resourceId;
		private int bitmapId = -1;
		
		private ThingType(int resourceId) {
			this.resourceId = resourceId;
		}
		
		public void setBitmap(int bitmapId) {
			this.bitmapId = bitmapId;
		}
	}
}