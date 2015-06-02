/*
 * Based on MobFox Android SDK code (https://github.com/mobfox/MobFox-Android-SDK)
 * Modified for AbsurdEngine under the MoPub Client License (/3rdparty-license/adsdk-LICENSE.txt)
 */

package com.adsdk.sdk.nativeads;

/**
 * To be implemented by classes that receive ads from a NativeAdManager 
 */

public interface NativeAdListener
{
	public void adLoaded(NativeAd ad);

	public void adFailedToLoad();
}
