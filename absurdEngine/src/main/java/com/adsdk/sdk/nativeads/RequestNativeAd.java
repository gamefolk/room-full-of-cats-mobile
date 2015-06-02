/*
 * Based on MobFox Android SDK code (https://github.com/mobfox/MobFox-Android-SDK)
 * Modified for AbsurdEngine under the MoPub Client License (/3rdparty-license/adsdk-LICENSE.txt)
 */

package com.adsdk.sdk.nativeads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.util.Log;

import com.adsdk.sdk.Const;
import com.adsdk.sdk.RequestException;
import com.adsdk.sdk.nativeads.NativeAd.ImageAsset;
import com.adsdk.sdk.nativeads.NativeAd.Tracker;
import com.arcadeoftheabsurd.absurdengine.Sprite;
import com.arcadeoftheabsurd.j_utils.Vector2d;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;

/**
 * Helper class - sends NativeAdRequests and parses the result
 */

public class RequestNativeAd 
{
	private Context context;
	
	private static final String TAG = "AbsurdEngine";
	
	public RequestNativeAd(Context context) {
		this.context = context;
	}
	
	public NativeAd sendRequest(NativeAdRequest request) throws RequestException {
		try {
			URL url = new URL(request.toString());
			URLConnection conn = url.openConnection();
			conn.setUseCaches(false);
			conn.setRequestProperty("User-Agent", request.getUserAgent());
			Log.v(TAG, "sending ad request");
			return parse(conn.getInputStream(), request);	
		} catch (IOException e) {
			throw new RequestException("Error sending ad request", e);
		}
	}

	protected NativeAd parse(final InputStream inputStream, final NativeAdRequest request) throws RequestException {
		final NativeAd response = new NativeAd();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Const.ENCODING), 8);
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			String result = sb.toString();
			reader.close();
			
			Log.v(TAG, "parsing ad response");
						
			JsonObject mainObject = JsonObject.readFrom(result);
			JsonValue imageAssetsValue = mainObject.get("imageassets");
			
			if (imageAssetsValue != null) {
				JsonObject imageAssetsObject = imageAssetsValue.asObject();

				for (String type : imageAssetsObject.names()) {	
					if (request.getImageAssets().containsKey(type)) {
						ImageAsset asset = new ImageAsset();
						
						JsonObject assetObject = imageAssetsObject.get(type).asObject();
						String url = assetObject.get("url").asString();
						
						asset.url = url;
						
						asset.width = Integer.parseInt(assetObject.get("width").asString());
						asset.height = Integer.parseInt(assetObject.get("height").asString());
						
						if(request.getImageAssets() != null) {
							Vector2d assetSize = request.getImageAssets().get(type);
							asset.sprite = Sprite.fromUrl(context, url, assetSize.x, assetSize.y);
						} else {
							asset.sprite = Sprite.fromUrl(context, url, asset.width, asset.height);
						}
						response.addImageAsset(type, asset);
					}
				}
			}
			JsonValue textAssetsValue = mainObject.get("textassets");
			
			if (textAssetsValue != null) {
				JsonObject textAssetsObject = textAssetsValue.asObject();
				
				for (String type : textAssetsObject.names()) {					
					String text = textAssetsObject.get(type).asString();
					response.addTextAsset(type, text);
				}
			}
			response.setClickUrl(mainObject.get("click_url").asString());

			JsonValue trackersValue = mainObject.get("trackers");
			
			if (trackersValue != null) {
				JsonArray trackersArray = trackersValue.asArray();
				
				for (int i = 0; i < trackersArray.size(); i++) {
					JsonValue trackerValue = trackersArray.get(i);
					
					if(trackerValue != null) {
						JsonObject trackerObject = trackerValue.asObject();
						Tracker tracker = new Tracker();
						tracker.type = trackerObject.get("type").asString();
						tracker.url = trackerObject.get("url").asString();
						response.getTrackers().add(tracker);
					}
				}
			}
		} catch (IOException e) {
			throw new RequestException("Error getting response", e);
		} catch (ParseException e) {
			throw new RequestException("Error parsing response", e);
		}
		return response;
	}
}
