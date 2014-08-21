package org.gamefolk.roomfullofcats;

import com.arcadeoftheabsurd.absurdengine.Sprite;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class CatsMenu extends LinearLayout
{
	private Button startButton;
	private Button tutorialButton;
	
	static class SettingsView extends LinearLayout
	{
		private Button musicButton;
		private Button soundButton;
		
		public SettingsView(Context context) {
			super(context);
			setOrientation(HORIZONTAL);
			
			musicButton = new Button(context);
			musicButton.setText("Music off");
			
			soundButton = new Button(context);
			soundButton.setText("Sound off");
			
			addView(musicButton);
			addView(soundButton);
		}
	}
	
	private class TitleView extends View
	{
		private Sprite title;
		
		public TitleView(Context context) {
			super(context);
			setBackgroundColor(Color.BLACK);
		}
		
		@Override
		protected void onSizeChanged(int newWidth, int newHeight, int oldWidth, int oldHeight) {
			title = Sprite.fromResource(getResources(), R.drawable.catslogo, -1, newHeight);
			title.setLocation((newWidth - title.getWidth()) / 2, 0);
			invalidate();
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			if (title != null) {
				title.draw(canvas);
			}
			super.onDraw(canvas);
		}
	}
	
	@SuppressWarnings("deprecation")
	public CatsMenu(Context context, OnClickListener startClick, OnClickListener tutorialClick) {
		super(context);
		setOrientation(VERTICAL);
		
		startButton = new Button(context);
		startButton.setText("Start");
		startButton.setOnClickListener(startClick);
		
		tutorialButton = new Button(context);
		tutorialButton.setText("Tutorial");
		tutorialButton.setOnClickListener(tutorialClick);
		
		addView(new TitleView(context), new LayoutParams(LayoutParams.FILL_PARENT, 0, .40f));
		addView(startButton, new LayoutParams(LayoutParams.FILL_PARENT, 0, .20f));
		addView(tutorialButton, new LayoutParams(LayoutParams.FILL_PARENT, 0, .20f));
		addView(new SettingsView(context), new LayoutParams(LayoutParams.FILL_PARENT, 0, .20f));
	}
}
