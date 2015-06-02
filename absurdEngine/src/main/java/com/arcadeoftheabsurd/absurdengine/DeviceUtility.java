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

package com.arcadeoftheabsurd.absurdengine;

import android.content.Context;

/*{{ ANDROIDONLY*/
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import android.webkit.WebView;
/*}}*/

/**
 * Provides device specific utilities. 
 * Initialize localIp and userAgent with set() before get()ting them
 * @author sam
 */

public class DeviceUtility 
{	
	private static String localIp;
	private static String userAgent;
	
	public static void setLocalIp() {
		localIp = getLocalIpImpl();
	}
	
	/**
	 * Stores this device's default user agent for later retrieval
	 * This method must be called from the UI thread
	 * @param context
	 */
	public static void setUserAgent(Context context) {
		userAgent = getUserAgentImpl(
			/*{{ ANDROIDONLY*/
		    context		
		    /*}}*/
		);
	}
	
	public static String getLocalIp() {
		return localIp;
	}
	
	public static String getUserAgent() {
		return userAgent;
	}
	
	public static boolean isAndroid() {
		/*{{ ANDROIDONLY*/
		return true;
		/*}}*/
		
		/*{{ IOSONLY
		return false;
		/*}}*/
	}
	
	public static boolean isIOS() {
		/*{{ ANDROIDONLY*/
		return false;
		/*}}*/
		
		/*{{ IOSONLY
		return true;
		/*}}*/
	}
	
	public static void postBlocking(Runnable r) throws InterruptedException {
		Thread thread = new Thread(r);
		thread.start();
		thread.join();
	}
	
	/*{{ ANDROIDONLY*/
	private static String getLocalIpImpl() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {}
		return null;
	}
	/*}}*/
	
	/*{{ IOSONLY
	private static native String getLocalIpImpl();
	/*}}*/
	
	/*{{ ANDROIDONLY*/
	private static String getUserAgentImpl(Context context) {
		return new WebView(context).getSettings().getUserAgentString();
	}
	/*}}*/
	
	/*{{ IOSONLY
	private static native String getUserAgentImpl();
	/*}}*/
}
