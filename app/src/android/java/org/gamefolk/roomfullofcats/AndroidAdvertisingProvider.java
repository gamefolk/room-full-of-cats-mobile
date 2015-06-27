package org.gamefolk.roomfullofcats;

import android.content.Context;
import android.webkit.WebView;
import org.OpenUDID.OpenUDID_manager;
import javafxports.android.FXActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class AndroidAdvertisingProvider implements AdvertisingProvider {

    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());

    private static final long UDID_TIMEOUT = 20;
    private static final long UDID_WAIT_INC = 20;

    private Context context = FXActivity.getInstance();

    @Override
    public void initializeAdService() {
        Log.info("Loading UDID");
        OpenUDID_manager.sync(context);
        new Thread(() -> {
            long waitTimer = UDID_TIMEOUT;

            while (!OpenUDID_manager.isInitialized() && waitTimer > 0) {
                try {
                    Thread.sleep(UDID_WAIT_INC);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                waitTimer -= UDID_WAIT_INC;
            }
        }).start();
    }

    @Override
    public String getAdvertisingIdentifier() {
        return OpenUDID_manager.getOpenUDID();
    }

    @Override
    public String getUserAgent() {
        return System.getProperty("http.agent");
    }
}
