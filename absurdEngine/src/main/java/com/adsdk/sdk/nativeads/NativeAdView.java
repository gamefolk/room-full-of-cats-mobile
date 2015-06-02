/*
 * Based on MobFox Android SDK code (https://github.com/mobfox/MobFox-Android-SDK)
 * Modified for AbsurdEngine under the MoPub Client License (/3rdparty-license/adsdk-LICENSE.txt)
 */

package com.adsdk.sdk.nativeads;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.LinearLayout;

import com.adsdk.sdk.nativeads.NativeAd.Tracker;
import com.arcadeoftheabsurd.absurdengine.WebUtils;

/**
 * Base for Views that display NativeAds
 */

// this should really be a FrameView that hosts any View type,
// but on iOS XMLVM does not lay out the contents of FrameViews correctly
public class NativeAdView extends LinearLayout implements NativeAdListener
{
	private boolean impressionReported = false;
	private List<Tracker> trackers;
	
	private static final String TAG = "AbsurdEngine";

	public NativeAdView(Context context) {
		super(context);
	}
		
	@Override
	public void adLoaded(NativeAd ad) {
		trackers = ad.getTrackers();
		invalidate();
	}

	@Override
	public void adFailedToLoad() {}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (trackers != null) {
			if (!impressionReported) {
				impressionReported = true;
								
				new Thread(new Runnable() {
					@Override
					public void run() {
						for (Tracker t : trackers) {
							if (t.type.equals("impression")) {
								try {
									Log.v(TAG, "tracking impression");
									System.out.println(WebUtils.restRequest(t.url));
								} catch (IOException e) {
									Log.e(TAG, "impression failed");
								}
							}
						}
					}	
				}).start();
			}
		}
		super.dispatchDraw(canvas);
	}
}
