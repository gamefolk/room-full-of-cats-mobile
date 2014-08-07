package org.gamefolk.roomfullofcats;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.arcadeoftheabsurd.absurdengine.DeviceUtility;
import com.arcadeoftheabsurd.absurdengine.GameActivity;

public class CatsGameActivity extends GameActivity
{	
	private LinearLayout contentView;
	private CatsGame gameView;
	private FrameLayout adView;
	
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);   
        
        super.onCreate(savedInstanceState); 
        
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
		
        //System.out.println("user agent: " + WebUtils.getUserAgent(this));
        
        /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
        startActivity(browserIntent);
        
        try {
        	AssetFileDescriptor asset = getAssets().openFd("coin.wav");
            MediaPlayer player = new MediaPlayer();			
        	player.setDataSource(asset.getFileDescriptor(),asset.getStartOffset(),asset.getLength());
        	player.prepare();
        	player.start();			
		} catch (IOException e) {
			e.printStackTrace();
		}*/
    }
    
    @Override
    protected void onStop() {
        super.onStop();
    }

    protected CatsGame initializeGame() {
        gameView = new CatsGame(this, this);
    	return gameView;
    }
    
    protected LinearLayout initializeContentView() {
    	adView = new FrameLayout(this);
    	
    	contentView = new LinearLayout(this);
    	contentView.setOrientation(LinearLayout.VERTICAL);
    	
    	contentView.addView(gameView, new LayoutParams(LayoutParams.FILL_PARENT, 0, .9f));
    	contentView.addView(adView, new LayoutParams(LayoutParams.FILL_PARENT, 0, .1f));
    	
    	return contentView;
    }
    
    private void finishedLoading() {
		System.out.println("finished loading!");
		System.out.println("ip: " + DeviceUtility.getLocalIp());
		System.out.println("ad id: " + DeviceUtility.getAdId());
		System.out.println("user agent: " + DeviceUtility.getUserAgent());
		
		loadContent();
	}
}
