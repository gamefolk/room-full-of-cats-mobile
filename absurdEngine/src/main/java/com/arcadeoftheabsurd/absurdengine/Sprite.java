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
import java.util.Random;

import com.arcadeoftheabsurd.j_utils.Vector2d;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

/**
 * Represents a bitmap on the screen
 * @author sam
 */

public class Sprite
{   
	BitmapHolder bitmapHolder;
	
    private boolean resized = false;
    private Rect bounds;
    private Vector2d pos;   
    
	Sprite(BitmapHolder bitmapHolder, int x, int y) {    
		this.bitmapHolder = bitmapHolder;
		pos = new Vector2d(x, y);
    	bounds = new Rect();
    	bounds.set(x, y, x + bitmapHolder.getInitialWidth(), y + bitmapHolder.getInitialHeight());
    }
	
	void setBitmap(BitmapHolder bitmapHolder) {
		this.bitmapHolder = bitmapHolder;
	}
	
	public static Sprite fromUrl(Context context, String url, int width, int height) throws IOException {
		Random r = new Random();
		String tempFileName = "temp" + r.nextInt();
		String filePath = WebUtils.downloadFile(context, url, tempFileName);
		return new Sprite (new BitmapTempFileHolder(
    			((BitmapDrawable) BitmapDrawable.createFromPath(filePath)).getBitmap(),
    			width, height, tempFileName, context), 0, 0);
	}
	
	public static Sprite fromResource(Resources resources, int resourceId, int width, int height) {
		return new Sprite(new BitmapHolder(BitmapFactory.decodeResource(resources, resourceId), width, height), 0, 0);
	}
    
	public void draw(Canvas canvas) {
		if (!bitmapHolder.isInitialized()) {
			bitmapHolder.initialize();
    	}
	    canvas.drawBitmap(isResized() ? 
	        bitmapHolder.scaleCopy(getWidth(), getHeight()) : 
		    bitmapHolder.getBitmap(), getX(), getY(), null);
	}
	
    public void setLocation(int x, int y) {
    	pos.set(x, y);
    	bounds.set(pos.x, pos.y, pos.x + bounds.width(), pos.y + bounds.height());
    }
    
    public void translate(int dx, int dy) {
    	pos.offset(dx, dy);
    	bounds.set(pos.x, pos.y, pos.x + bounds.width(), pos.y + bounds.height());
    }
    
    public void resize(int width, int height) {
    	if (width == bitmapHolder.getInitialWidth() && height == bitmapHolder.getInitialHeight()) {
    		resized = false;
    	} else {
    		resized = true;
    	}
    	bounds.set(pos.x, pos.y, pos.x + width, pos.y + height);
    }
    
    public boolean isResized() {
    	return resized;
    }
    
    public int getX() {
    	return pos.x;
    }
    
    public int getY() {
    	return pos.y;
    }
    
    public int getWidth() {
    	return bounds.width();
    }
    
    public int getHeight() {
    	return bounds.height();
    }
    
    public Rect getBounds() {
		return bounds;
	}
}
