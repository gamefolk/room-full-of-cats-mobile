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

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.arcadeoftheabsurd.j_utils.Vector2d;

/**
 * Holds a Bitmap and information relevant to rendering Sprites, provides convenience methods
 * @author sam
 */

class BitmapHolder
{
	private Bitmap bitmap;
	private boolean initialized = false;
	
	private Vector2d initialSize;
	
	/**
	 * Construct a BitmapHolder that initializes the given Bitmap to the given size
	 * If one of initialWidth and initialHeight is equal to -1, that dimension will be scaled according to the Bitmap's aspect ratio
	 * If both initialWidth and initialHeight are -1, the Bitmap will retain its original size  
	 * @param bitmap
	 * @param initialWidth 
	 * @param initialHeight
	 */
	BitmapHolder(Bitmap bitmap, int initialWidth, int initialHeight) {
		this.bitmap = bitmap;
		
		if (initialWidth == -1 && initialHeight == 1) {
			initialWidth = bitmap.getWidth();
			initialHeight = bitmap.getHeight();
		} else if (initialHeight == -1) {
			initialHeight = (initialWidth * bitmap.getHeight()) / bitmap.getWidth();
		} else if (initialWidth == -1) {
			initialWidth = (initialHeight * bitmap.getWidth()) / bitmap.getHeight();
		}
		initialSize = new Vector2d(initialWidth, initialHeight);
	}
	
	Bitmap getBitmap() {
		return bitmap;
	}
	
	void initialize() {
		scaleInitial();
		initialized = true;
	}
	
	boolean isInitialized() {
		return initialized;
	}
	
	int getInitialWidth() {
		return initialSize.x;
	}
	
	int getInitialHeight() {
		return initialSize.y;
	}
	
	Bitmap scaleCopy(int width, int height) {
		Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

    	Canvas canvas = new Canvas();
    	canvas.setBitmap(newBitmap);
    	canvas.scale((float) width / bitmap.getWidth(), (float) height / bitmap.getHeight());
    	
    	canvas.drawBitmap(bitmap, 0, 0, null);
    	
    	return newBitmap;
	}
	
	void scaleInitial() {    	
    	bitmap = scaleCopy(initialSize.x, initialSize.y);
	}
}
