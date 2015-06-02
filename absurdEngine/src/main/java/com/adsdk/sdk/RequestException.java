/*
 * Based on MobFox Android SDK code (https://github.com/mobfox/MobFox-Android-SDK)
 * Modified for AbsurdEngine under the MoPub Client License (/3rdparty-license/adsdk-LICENSE.txt)
 */

package com.adsdk.sdk;

/**
 * Thrown while sending/ parsing MobFox API requests
 */

public class RequestException extends Exception 
{
	private static final long serialVersionUID = 1L;

	public RequestException() {
		super();
	}

	public RequestException(final String detailMessage) {
		super(detailMessage);
	}

	public RequestException(final String detailMessage,
			final Throwable throwable) {
		super(detailMessage, throwable);
	}

	public RequestException(final Throwable throwable) {
		super(throwable);
	}
}
