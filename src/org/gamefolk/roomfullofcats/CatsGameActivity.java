package org.gamefolk.roomfullofcats;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.arcadeoftheabsurd.absurdengine.GameActivity;
import com.arcadeoftheabsurd.absurdengine.GameView;
import com.arcadeoftheabsurd.absurdengine.WebUtils;

public class CatsGameActivity extends GameActivity
{
	private CatsGame world;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);    
        
        System.out.println("user agent: " + WebUtils.getUserAgent(this));
        
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
                
        super.onCreate(savedInstanceState); 
    }
    
    @Override
    public void onStop() {
        super.onStop();
    }

    protected GameView initializeGame() {
        world = new CatsGame(this);
        return world;
    }
}
