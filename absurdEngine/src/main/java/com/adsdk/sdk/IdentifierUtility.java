/*
 * AbsurdEngine (https://bitbucket.org/smpsnr/absurdengine/) 
 * (c) by Sam Posner (http://www.arcadeoftheabsurd.com/)
 *
 * AbsurdEngine is licensed under a
 * Creative Commons Attribution 4.0 International License
 *
 * You should have received a copy of the license along with this
 * work. If not, see http://creativecommons.org/licenses/by/4.0/ 
 */

package com.adsdk.sdk;

import android.app.Activity;
import android.content.Context;

/*{{ ANDROIDONLY*/
import android.content.DialogInterface;
import com.arcadeoftheabsurd.absurdengine.DeviceUtility;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import org.OpenUDID.OpenUDID_manager;
/*}}*/

/**
 * Provides access to device advertising identifier
 * Gets the Google Play Advertising ID on Android and the ASIdentifier on iOS, with an OpenUDID backup for each
 * Call requireAdService, then setAdId, then getAdId
 * @author sam
 */

public class IdentifierUtility 
{
	private static String adId;
	private static boolean adDoNotTrack = true;
	private static final String DEFAULT_AD_ID = "00000000-0000-0000-0000-000000000000";
	
	/*{{ ANDROIDONLY*/
	private static final int PLAY_DIALOG_CODE = 1;
	private static final long UDID_TIMEOUT = 5000;
	private static final long UDID_WAIT_INC = 20;
	
	private static Context context;
	private static boolean playServiceFailed = false;
	private static boolean openUDIDServiceFailed = false;
	/*}}*/
	
	/**
	 * Accesses the device's ad id and tracking setting for later retrieval 
	 * This method must NOT be called from the UI thread
	 * @throws InterruptedException
	 */
	public static void prepareAdId() throws InterruptedException {
		prepareAdIdImpl();
	}
	
	public static String getAdId() {
		return adId;
	}
	
	public static boolean getAdDoNotTrack() {
		return adDoNotTrack;
	}
	
	/**
	 * Accesses Google Play Services on Android - on iOS, does nothing
	 * This method must be called from the UI thread
	 * @param activity
	 */
	public static void requireAdService(final Activity activity) {
		/*{{ ANDROIDONLY*/
		IdentifierUtility.context = activity;
		int error = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
    	if (error != ConnectionResult.SUCCESS) {
    		if (GooglePlayServicesUtil.isUserRecoverableError(error)) {
    			GooglePlayServicesUtil.getErrorDialog(error, activity, PLAY_DIALOG_CODE, new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						playServiceFailed = true;
					}
				}).show();
    		} else {
    			playServiceFailed = true;
    		}
    	}
    	/*}}*/
    	/*{{ IOSONLY
    	//nop
    	return;
    	/*}}*/
	}
	
	/*{{ ANDROIDONLY*/	
	private static void initOpenUDID() {
		OpenUDID_manager.sync(context);
		try {
			DeviceUtility.postBlocking(new Runnable() {
				@Override
				public void run() {
					long waitTimer = UDID_TIMEOUT;
					while (!OpenUDID_manager.isInitialized() && waitTimer > 0) {
						try {
							Thread.sleep(UDID_WAIT_INC);
							waitTimer -= UDID_WAIT_INC;
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
				}
			});
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		if (!OpenUDID_manager.isInitialized()) {
			openUDIDServiceFailed = true;
		}
	}
	/*}}*/
	
	/*{{ ANDROIDONLY*/
	private static void prepareAdIdImpl() {
		if (!playServiceFailed) {
			Info adInfo = null;		
			try {
				adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
			} catch (Exception e) {
				initOpenUDID();
			}
			if (adInfo != null && adInfo.getId() != null) {
				adId = adInfo.getId();
				adDoNotTrack = adInfo.isLimitAdTrackingEnabled();
				return;
			} else {
				initOpenUDID();
			}
		}
		if (!openUDIDServiceFailed) {
			if (!(OpenUDID_manager.getOpenUDID() == null)) {
				adId = OpenUDID_manager.getOpenUDID();
			} else {
				adId = DEFAULT_AD_ID;
			}
		}
	}
	/*}}*/
	
	/*{{ IOSONLY
	private static native void prepareAdIdImpl();
	/*}}*/
}
