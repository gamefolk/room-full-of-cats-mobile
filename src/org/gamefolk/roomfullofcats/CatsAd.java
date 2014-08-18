package org.gamefolk.roomfullofcats;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.arcadeoftheabsurd.absurdengine.BannerAdView;
import com.arcadeoftheabsurd.j_utils.Vector2d;
import com.adsdk.sdk.nativeads.NativeAd;
import com.adsdk.sdk.nativeads.NativeAdManager;

public class CatsAd extends BannerAdView
{
	private NativeAdManager adManager;
	private HashMap<String, Vector2d> imageAssets = new HashMap<String, Vector2d>();
	
	public CatsAd(Context context, int textSize, int textMarginLeft, int textMarginRight) {
		super(context, textSize, textMarginLeft, textMarginRight);
	}
	
	@Override
	public void adLoaded(final NativeAd ad) {
		System.out.println("ad loaded");
		
		this.setAssets(ad.getImageAsset("icon").sprite, ad.getTextAsset("description"));
		
		this.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (ad.getClickUrl() != null && !ad.getClickUrl().equals("")) {
					final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ad.getClickUrl()));
					getContext().startActivity(intent);
				}
			}
		});
		super.adLoaded(ad);
	}

	@Override
	public void adFailedToLoad() {
		System.out.println("ad failed");
	}
	
	@Override
	protected void onSizeChanged(int newWidth, int newHeight, int oldWidth, int oldHeight) {
		super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);

		imageAssets.put("icon", new Vector2d(newHeight, newHeight));
		adManager = new NativeAdManager(getContext(), this, "80187188f458cfde788d961b6882fd53", null, imageAssets);
		adManager.requestAd();
	}
}
