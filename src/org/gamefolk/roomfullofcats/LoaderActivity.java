package org.gamefolk.roomfullofcats;

import com.arcadeoftheabsurd.absurdengine.DeviceUtility;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LoaderActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		DeviceUtility.setDeviceContext(getApplicationContext());
		
		System.out.println("checking ad services");
		DeviceUtility.requireAdService(this);
		System.out.println("ad services available");
		
		System.out.println("getting device info...");
		
		DeviceUtility.setUserAgent();
		
		Thread loaderThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					DeviceUtility.setLocalIp();
					DeviceUtility.setAdId();
				} catch (InterruptedException e) {
					System.out.println("error getting ip");
				}
				
				finishedLoading();
			}
		});
		loaderThread.start();
	}
	
	private void finishedLoading() {
		System.out.println("finished loading!");
		System.out.println("ip: " + DeviceUtility.getLocalIp());
		System.out.println("ad id: " + DeviceUtility.getAdId());
		System.out.println("user agent: " + DeviceUtility.getUserAgent());
		
		Intent gameIntent = new Intent(this, CatsGameActivity.class);
		startActivity(gameIntent);
		
		this.finish();
	}
}
