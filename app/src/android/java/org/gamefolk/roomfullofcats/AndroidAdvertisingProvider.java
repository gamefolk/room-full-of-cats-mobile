package org.gamefolk.roomfullofcats;

import android.content.Context;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import javafxports.android.FXActivity;

import java.io.IOException;
import java.util.logging.Logger;

public class AndroidAdvertisingProvider implements AdvertisingProvider {
    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());

    private static final long UDID_TIMEOUT = 20;
    private static final long UDID_WAIT_INC = 20;

    private Context context = FXActivity.getInstance();
    private AdvertisingIdClient.Info adInfo = null;

    @Override
    public void initializeAdService() {
        Log.info("Retrieving advertising data.");
        new Thread(() -> {
            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                Log.info("Successfully retrieved advertising info.");
            } catch (IOException |
                    GooglePlayServicesNotAvailableException |
                    IllegalStateException |
                    GooglePlayServicesRepairableException e) {
                throw new RuntimeException("Google Play Services is required.");
            }
        }).start();
    }

    @Override
    public String getAdvertisingIdentifier() {
        return adInfo.getId();
    }

    @Override
    public String getUserAgent() {
        return System.getProperty("http.agent");
    }

    public boolean getDoNotTrack() {
        return adInfo.isLimitAdTrackingEnabled();
    }
}
