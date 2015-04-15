package org.gamefolk.roomfullofcats;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class LevelInterimView extends View {
	private Paint paint = new Paint();
	
	public LevelInterimView(Context context) {
		super(context);
		this.invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawText("GAME OVER", 0, 0, paint);
	}
}
