package org.gamefolk.roomfullofcats;

import com.arcadeoftheabsurd.absurdengine.Sprite;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CatsMenu extends LinearLayout
{
	private Button startButton;
	private Button tutorialButton;
	
	static class SettingsView extends TableLayout
	{
		// I am truly sorry about the black magic that goes into centering these damn checkboxes
		private TableRow boxRow;
		private CheckBox musicBox;
		private CheckBox soundBox;
		
		private TableRow textRow;
		private TextView musicText;
		private TextView soundText;
		
		private TableLayout.LayoutParams rowParams;
		private TableRow.LayoutParams boxParams;
		private TableRow.LayoutParams textParams;
		
		@SuppressWarnings("deprecation")
		public SettingsView(Context context) {
			super(context);
			setStretchAllColumns(true);
						
			rowParams = new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT); 
			
			boxParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT); 
			boxParams.gravity = Gravity.RIGHT;
			
			textParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT); 
			textParams.gravity = Gravity.CENTER;
			
			boxRow = new TableRow(context);
			
			musicBox = new CheckBox(context);
			musicBox.setChecked(true);
			
			musicBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton arg0, boolean checked) {
					// ...
				}
			});
			
			soundBox = new CheckBox(context);
			soundBox.setChecked(true);
			
			soundBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton arg0, boolean checked) {
					// ...
				}
			});
			
			boxRow.addView(musicBox, boxParams);
			boxRow.addView(soundBox, boxParams);
			
			textRow = new TableRow(context);
			
			musicText = new TextView(context);
			musicText.setText("Music");
			musicText.setTextSize(14);
			musicText.setTextColor(Color.BLACK);
			
			soundText = new TextView(context);
			soundText.setText("Sound");	
			soundText.setTextSize(14);
			soundText.setTextColor(Color.BLACK);
			
			textRow.addView(musicText, textParams);
			textRow.addView(soundText, textParams);
			
			addView(boxRow, rowParams);
			addView(textRow, rowParams);
		}
		
		/*@Override
		protected void onSizeChanged(int newWidth, int newHeight, int oldWidth, int oldHeight) {			
			musicBox.setPadding(newWidth / 8, 0, 0, 0);
			soundBox.setPadding(newWidth / 8, 0, 0, 0);
			super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);
			invalidate();
		}*/
	}
	
	private class TitleView extends View
	{
		private Sprite title;
		
		public TitleView(Context context) {
			super(context);
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
		setBackgroundColor(Color.WHITE);
		
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
