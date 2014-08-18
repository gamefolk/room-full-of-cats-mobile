package org.gamefolk.roomfullofcats;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.arcadeoftheabsurd.absurdengine.DeviceUtility;
import com.arcadeoftheabsurd.absurdengine.GameActivity;
import com.arcadeoftheabsurd.absurdengine.SoundManager;

public class CatsGameActivity extends GameActivity
{	
	private LinearLayout contentView;
	private CatsGame gameView;
	private CatsAd adView;
		
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);   
        
        super.onCreate(savedInstanceState); 
        
        SoundManager.initializeSound(getAssets(), CatsGame.NUM_CHANNELS);
        DeviceUtility.setDeviceContext(getApplicationContext());
		
		System.out.println("checking ad services");
		DeviceUtility.requireAdService(this);
		System.out.println("ad services available");
		
		System.out.println("getting device info...");
		
		DeviceUtility.setUserAgent();
		DeviceUtility.setLocalIp();
		
		Thread loaderThread = new Thread(new Runnable() {
			public void run() {
				try {
					DeviceUtility.setAdId();
				} catch (InterruptedException e) {
					System.out.println("error getting ip");
				}
				finishedLoading();
			}
		});
		loaderThread.start();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
    }

    protected CatsGame initializeGame() {
        gameView = new CatsGame(this, this);
    	return gameView;
    }
    
	@SuppressWarnings("deprecation")
	protected LinearLayout initializeContentView() {
    	adView = new CatsAd(this, DeviceUtility.isIOS() ? 12 : 20, 5, 5);
    	
    	contentView = new LinearLayout(this);
    	contentView.setOrientation(LinearLayout.VERTICAL);
    	
    	contentView.addView(gameView.scoreView, new LayoutParams(LayoutParams.FILL_PARENT, 0, .05f));
    	contentView.addView(gameView, new LayoutParams(LayoutParams.FILL_PARENT, 0, .80f));
    	contentView.addView(adView, new LayoutParams(LayoutParams.FILL_PARENT, 0, .15f));
    	
    	return contentView;
    }
    
    private void finishedLoading() {
		System.out.println("finished loading!");
		System.out.println("ip: " + DeviceUtility.getLocalIp());
		System.out.println("ad id: " + DeviceUtility.getAdId());
		System.out.println("user agent: " + DeviceUtility.getUserAgent());
		
		// call initializeGame and initializeContentView on the main thread, starting the game
		loadContent();
	}
}
