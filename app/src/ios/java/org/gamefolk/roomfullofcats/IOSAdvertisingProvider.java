package org.gamefolk.roomfullofcats;

import org.robovm.apple.adsupport.ASIdentifierManager;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.uikit.UIWebView;

public class IOSAdvertisingProvider implements AdvertisingProvider {

    @Override
    public void initializeAdService() {
        // No need for initialization
    }

    @Override
    public String getAdvertisingIdentifier() {
        return ASIdentifierManager.getSharedManager().getAdvertisingIdentifier().asString();
    }

    @Override
    public String getUserAgent() {
        UIWebView webView = new UIWebView(CGRect.Zero());
        return webView.evaluateJavaScript("navigator.userAgent");
    }

    @Override
    public boolean getDoNotTrack() {
        return !ASIdentifierManager.getSharedManager().isAdvertisingTrackingEnabled();
    }
}
