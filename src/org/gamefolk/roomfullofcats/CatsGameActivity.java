package org.gamefolk.roomfullofcats;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.adsdk.sdk.IdentifierUtility;
import com.arcadeoftheabsurd.absurdengine.DeviceUtility;
import com.arcadeoftheabsurd.absurdengine.GameActivity;
import com.arcadeoftheabsurd.absurdengine.SoundManager;
import com.arcadeoftheabsurd.absurdengine.Sprite;

public class CatsGameActivity extends GameActivity
{	
	private LinearLayout contentView;
	private CatsGame gameView;
	private CatsAd adView;
	
	private boolean finishedLoading = false;
	private boolean gameStartRequested = false;
	
	private class SplashView extends View
	{
		private Sprite logo;
		
		public SplashView(Context context) {
			super(context);
			setBackgroundColor(Color.BLACK);
		}
		
		@Override
		protected void onSizeChanged(int newWidth, int newHeight, int oldWidth, int oldHeight) {
			logo = Sprite.fromResource(getResources(), R.drawable.gamefolklogo, newWidth, newWidth);
			logo.setLocation(0, (newHeight - logo.getHeight()) / 2);
			invalidate();
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			if (logo != null) {
				logo.draw(canvas);
			}
			super.onDraw(canvas);
		}
	}
		
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);   
        
        super.onCreate(savedInstanceState); 
        
        final CatsMenu catsMenu = new CatsMenu(this, 
            	new OnClickListener() {
    				public void onClick(View arg0) {
    					if (finishedLoading) {
    						startGame();
    					} else {
    						gameStartRequested = true;
    					}
    				}
            	}, null);        
        
        SplashView splashView = new SplashView(this);
        splashView.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				setContentView(catsMenu);        
				loadGame();
			}
		});
        setContentView(splashView);        
    }
    
    private void loadGame() {
		System.out.println("checking ad services");
		IdentifierUtility.requireAdService(this);
		System.out.println("ad services available");
		
		System.out.println("getting device info...");
		
		DeviceUtility.setUserAgent(this);
		
		Thread loaderThread = new Thread(new Runnable() {
			public void run() {
				SoundManager.initializeSound(getAssets(), CatsGame.NUM_CHANNELS);
				DeviceUtility.setLocalIp();
				try {
					IdentifierUtility.setAdId();
				} catch (InterruptedException e) {
					System.out.println("error getting ip");
				}
				if (gameStartRequested) {
					startGame();
				} else {
					finishedLoading = true;
				}
			}
		});
		loaderThread.start();
    }

    protected CatsGame initializeGame() {
        gameView = new CatsGame(this, this);
    	return gameView;
    }
    
	@SuppressWarnings("deprecation")
	protected LinearLayout initializeContentView() {
    	adView = new CatsAd(this);
    	
    	contentView = new LinearLayout(this);
    	contentView.setOrientation(LinearLayout.VERTICAL);
    	
    	contentView.addView(gameView.scoreView, new LayoutParams(LayoutParams.FILL_PARENT, 0, .05f));
    	contentView.addView(gameView, new LayoutParams(LayoutParams.FILL_PARENT, 0, .80f));
    	contentView.addView(adView, new LayoutParams(LayoutParams.FILL_PARENT, 0, .15f));
    	
    	return contentView;
    }
    
    private void startGame() {
		System.out.println("finished loading!");
		System.out.println("ip: " + DeviceUtility.getLocalIp());
		System.out.println("ad id: " + IdentifierUtility.getAdId());
		System.out.println("user agent: " + DeviceUtility.getUserAgent());
		
		// call initializeGame and initializeContentView on the main thread, starting the game
		loadContent();
	}
}
