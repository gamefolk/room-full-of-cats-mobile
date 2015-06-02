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
import android.graphics.Bitmap;

/**
 * Holds a Bitmap with data in a temporary file; deletes that file once the bitmap is initialized
 * @author sam
 */

class BitmapTempFileHolder extends BitmapHolder
{
	private String tempFileName;
	private Context context;
	
	BitmapTempFileHolder(Bitmap bitmap, int initialWidth, int initialHeight, String tempFileName, Context context) {
		super(bitmap, initialWidth, initialHeight);

		this.tempFileName = tempFileName;
		this.context = context;
	}

	@Override
	void initialize() {
		super.initialize();
		context.deleteFile(tempFileName);
	}
}
