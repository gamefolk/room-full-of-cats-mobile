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

package com.adsdk.sdk.nativeads;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;

import com.arcadeoftheabsurd.absurdengine.Sprite;

/**
 * View that displays a NativeAd as a horizontal banner: [ad icon] [textMarginLeft] [ad description] [textMarginRight]
 * @author sam
 */

public class BannerAdView extends NativeAdView
{
	private Sprite sprite;
	private TextView spacerView;
	private TextView descriptionView;
	
	private int textSize;
	private int textMarginLeft;
	private int textMarginRight;
	private int textColor;
		
	private boolean readyToWrite = false;
	private int descriptionWidth;
	private char[] descriptionChars;
	private StringBuilder descriptionBuffer;
	private int curChar;
	
	public BannerAdView(Context context, int textSize, int textMarginLeft, int textMarginRight, int textColor) {
		super(context);
		this.textSize = textSize;
		this.textMarginLeft = textMarginLeft;
		this.textMarginRight = textMarginRight;
		this.textColor = textColor;
		
		setOrientation(HORIZONTAL);
		spacerView = new TextView(context);
		descriptionView = new TextView(context);
	}
	
	protected void setAssets(Sprite sprite, String description) {
		this.sprite = sprite;
		this.sprite.setLocation(0, 0);
		descriptionChars = description.toCharArray();
		
		descriptionWidth = (this.getWidth() - sprite.getWidth()) - (textMarginLeft + textMarginRight);
		descriptionBuffer = new StringBuilder();
		curChar = 0;
		
		descriptionBuffer.append(descriptionChars[curChar++]);
		
		descriptionView.setText(descriptionBuffer.toString());
		descriptionView.setTextColor(textColor);
		descriptionView.setTextSize(textSize);
		
		readyToWrite = true;
		
		addView(spacerView, new LayoutParams(sprite.getWidth() + textMarginLeft, sprite.getHeight()));
		addView(descriptionView, new LayoutParams(descriptionWidth, sprite.getHeight()));		
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (sprite != null) {
			sprite.draw(canvas);
		}		
		super.dispatchDraw(canvas);
		
		if (readyToWrite) {
			if (curChar < descriptionChars.length) {
				descriptionBuffer.append(descriptionChars[curChar++]);
				
				if (descriptionView.getWidth() > descriptionWidth) {
					descriptionBuffer.insert(curChar, " ");
				}
				descriptionView.setText(descriptionBuffer.toString());
				dispatchDraw(canvas);
			} else {
				readyToWrite = false;
			}
		}
	}
}
