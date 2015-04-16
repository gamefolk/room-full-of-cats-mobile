package org.gamefolk.roomfullofcats;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.adsdk.sdk.IdentifierUtility;
import com.arcadeoftheabsurd.absurdengine.DeviceUtility;
import com.arcadeoftheabsurd.absurdengine.GameActivity;
import com.arcadeoftheabsurd.absurdengine.SoundManager;

public class CatsGameActivity extends GameActivity
{	
	private LinearLayout contentView;
	private CatsGame gameView;
	private CatsAd adView;
	
	private Thread deviceLoaderThread;
	private Thread gameLoaderThread;
		
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);   
        
        super.onCreate(savedInstanceState); 
        
        final CatsMenu catsMenu = new CatsMenu(this, new OnClickListener() {
        	public void onClick(View arg0) {
        		try {
					deviceLoaderThread.join();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
    			startGame();
    		}
    	}, null);     
        
        setContentView(catsMenu);        
		loadGame();     
    }
    
    private void loadGame() {
		System.out.println("checking ad services");
		IdentifierUtility.requireAdService(this);
		System.out.println("ad services available");
		
		System.out.println("getting device info...");
		
		DeviceUtility.setUserAgent(this);
		
		deviceLoaderThread = new Thread(new Runnable() {
			public void run() {
				SoundManager.initializeSound(getAssets(), CatsGame.NUM_CHANNELS);
				DeviceUtility.setLocalIp();
				try {
					IdentifierUtility.prepareAdId();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});
		deviceLoaderThread.start();
    }

    protected CatsGame initializeGame() {
		try {
			gameLoaderThread.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
    	return gameView;
    }
    
	@SuppressWarnings("deprecation")
	protected LinearLayout initializeContentView() {
    	/*adView = new CatsAd(this);
    	
    	contentView = new LinearLayout(this);
    	contentView.setOrientation(LinearLayout.VERTICAL);
    	
    	contentView.addView(gameView.scoreView, new LayoutParams(LayoutParams.FILL_PARENT, 0, .05f));
    	contentView.addView(gameView, new LayoutParams(LayoutParams.FILL_PARENT, 0, .80f));
    	contentView.addView(adView, new LayoutParams(LayoutParams.FILL_PARENT, 0, .15f));
    	*/
    	return contentView;
    }
	
	class ToastView extends TextView
	{
		public ToastView(Context context, String message) {
			super(context);
			setTextSize(DeviceUtility.isIOS() ? 12 : 20);
			setText(message);
		}
		
		/*@Override
		protected void onVisibilityChanged (View changedView, int visibility) {
			if (changedView == this && visibility == GONE) {
				loadContent();
			}
		}*/
	}
    
    private void startGame() {
		System.out.println("finished loading!");
		System.out.println("ip: " + DeviceUtility.getLocalIp());
		System.out.println("ad id: " + IdentifierUtility.getAdId());
		System.out.println("do not track: " + IdentifierUtility.getAdDoNotTrack());
		System.out.println("user agent: " + DeviceUtility.getUserAgent());

		runOnUiThread(new Runnable() {
	        public void run() {
	        	Toast toast = new Toast(getApplicationContext());
	    		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
	    		toast.setDuration(Toast.LENGTH_LONG);
	    		toast.setView(new ToastView(getApplicationContext(), "Level " + CatsGameManager.curLevel));
	    		toast.show();
	        }
	    });
		
		gameView = new CatsGame(this, this);
		
		adView = new CatsAd(this);
    	
    	contentView = new LinearLayout(this);
    	contentView.setOrientation(LinearLayout.VERTICAL);
		
		gameLoaderThread = new Thread(new Runnable() {
			public void run() {
				gameView.makeLevel(CatsGameManager.loadLevel());
				
				contentView.addView(gameView.scoreView, new LayoutParams(LayoutParams.FILL_PARENT, 0, .05f));
		    	contentView.addView(gameView, new LayoutParams(LayoutParams.FILL_PARENT, 0, .80f));
		    	contentView.addView(adView, new LayoutParams(LayoutParams.FILL_PARENT, 0, .15f));
			}
		});
		gameLoaderThread.start();
		
		// call initializeGame and initializeContentView on the main thread, starting the game
		loadContent();
	}
}
