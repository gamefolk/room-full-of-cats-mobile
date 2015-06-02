/*
 * Based on MobFox Android SDK code (https://github.com/mobfox/MobFox-Android-SDK)
 * Modified for AbsurdEngine under the MoPub Client License (/3rdparty-license/adsdk-LICENSE.txt)
 */

package com.adsdk.sdk.nativeads;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Random;


import com.adsdk.sdk.Const;
import com.arcadeoftheabsurd.absurdengine.DeviceUtility;
import com.arcadeoftheabsurd.j_utils.Vector2d;

/**
 * Represents a MobFox API request
 */

public class NativeAdRequest 
{
	private static final String REQUEST_URL = "http://my.mobfox.com/request.php";
	private static final String REQUEST_TYPE = "native";
	private static final String RESPONSE_TYPE = "json";
	private static final String IMAGE_TYPES = "icon,main";
	private static final String TEXT_TYPES = "headline,description,cta,advertiser,rating";
	private static final String REQUEST_TYPE_ANDROID = "android_app";
	private static final String REQUEST_TYPE_IPHONE = "iphone_app";
	
	private List<String> adTypes;
	private List<String> textTypes;
	private List<String> keywords;
	private Map<String, Vector2d> imageAssets;
	private String publisherId;
	private String userAgent;
	private String adId;
	private String deviceIP;
	private double longitude = 0.0;
	private double latitude = 0.0;
	private int userAge;
	private boolean adDoNotTrack = true;
	
	@SuppressWarnings("deprecation")
	public String toString() {
		final StringBuilder b = new StringBuilder(REQUEST_URL);
		
		Random r = new Random();
		int random = r.nextInt(50000);
		
		if (DeviceUtility.isIOS()) {
			b.append("?rt=" + REQUEST_TYPE_IPHONE);
		} else {
			b.append("?rt=" + REQUEST_TYPE_ANDROID);
		}
		b.append("&r_type=" + REQUEST_TYPE);
		b.append("&r_resp=" + RESPONSE_TYPE);
		
		if (imageAssets == null) {
			b.append("&n_img=" + IMAGE_TYPES);
		}
		else {
			Object[] imageTypes = imageAssets.keySet().toArray();
			b.append("&n_img=");
			for(int i = 0; i < imageTypes.length; i++) {
				b.append(i < imageTypes.length - 1 ? imageTypes[i] + "," : imageTypes[i]);
			}
		}
		if (textTypes == null) {
			b.append("&n_txt=" + TEXT_TYPES);
		} else {
			b.append("&n_txt=");
			for (int i = 0; i < textTypes.size(); i++) {
				b.append(i < textTypes.size() - 1 ? textTypes.get(i) + "," : textTypes.get(i));
			}
		}
		if (adTypes != null) {
			b.append("&n_type=");
			for (int i = 0; i < adTypes.size(); i++) {
				b.append(i < adTypes.size() - 1 ? adTypes.get(i) + "," : adTypes.get(i));
			}
		}
		b.append("&s=" + this.getPublisherId());
		
		b.append("&i=" + this.getDeviceIP());
		
		try {
			b.append("&u=" + URLEncoder.encode(this.getUserAgent(), Const.ENCODING));
		} catch (UnsupportedEncodingException e) {
			b.append("&u=" + URLEncoder.encode(this.getUserAgent()));
		}
		b.append("&r_random=" + Integer.toString(random));

		if (DeviceUtility.isIOS()) {
			b.append("&o_iosadvid=" + adId);
			b.append("&o_iosadvidlimit=" + (adDoNotTrack ? "1" : "0"));
		} else {
			b.append("&o_andadvid=" + adId);
			b.append("&o_andadvdnt=" + (adDoNotTrack ? "1" : "0"));
		}
		b.append("&v=" + Const.PROTOCOL_VERSION);

		if (userAge != 0) {
			b.append("&demo.age=" + Integer.toString(userAge));
		}
		if (keywords != null) {
			b.append("&demo.keywords=");
			for (int i = 0; i < keywords.size(); i++) {
				b.append(i < keywords.size() - 1 ? keywords.get(i) + "," : keywords.get(i));
			}
		}
		if (longitude != 0 && latitude != 0) {
			b.append("&longitude=" + Double.toString(longitude));
			b.append("&latitude=" + Double.toString(latitude));
		}
		return b.toString();
	}

	public List<String> getAdTypes() {
		return adTypes;
	}

	public void setAdTypes(List<String> adTypes) {
		this.adTypes = adTypes;
	}
	
	public List<String> getTextTypes() {
		return textTypes;
	}
	
	public void setTextTypes(List<String> textTypes) {
		this.textTypes = textTypes;
	}
	
	public Map<String, Vector2d> getImageAssets() {
		return imageAssets;
	}
	
	public void setImageAssets(Map<String, Vector2d> imageAssets) {
		this.imageAssets = imageAssets;
	}

	public String getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(String publisherId) {
		this.publisherId = publisherId;
	}

	public String getUserAgent() {
		if (this.userAgent == null) {
			return "";
		}
		return this.userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	public String getAdId() {
		return adId;
	}
	
	public void setAdId(String adId) {
		this.adId = adId;
	}
	
	public void setDeviceIP(String deviceIP) {
		this.deviceIP = deviceIP;
	}
	
	public String getDeviceIP() {
		return deviceIP;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public int getUserAge() {
		return userAge;
	}

	public void setUserAge(int userAge) {
		this.userAge = userAge;
	}

	public List<String> getKeywords() {
		return keywords;
	}
	
	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}
	
	public boolean getAdDoNotTrack() {
		return adDoNotTrack;
	}
	
	public void setAdDoNotTrack(boolean adDoNotTrack) {
		this.adDoNotTrack = adDoNotTrack;
	}
}
