package org.gamefolk.roomfullofcats;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Logger;

public class AdvertisingService {
    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());

    private static AdvertisingService instance;
    public static synchronized AdvertisingService getInstance() {
        if (instance == null) {
            instance = new AdvertisingService();
        }
        return instance;
    }

    private final ServiceLoader<AdvertisingProvider> serviceLoader;
    private AdvertisingProvider provider;

    private AdvertisingService() {
        serviceLoader = ServiceLoader.load(AdvertisingProvider.class);

        Iterator<AdvertisingProvider> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            if (provider == null) {
                provider = iterator.next();
                Log.info("Using AdvertisingProvider: " + provider.getClass().getName());
            } else {
                break;
            }
        }

        if (provider == null) {
            Log.info("No AdvertisingProvider implementation found.");
        }
    }

    public void initializeAdService() {
        provider.initializeAdService();
    }

    public String getAdvertisingIdentifier() {
        return provider.getAdvertisingIdentifier();
    }

    public String getUserAgent() {
        return provider.getUserAgent();
    }
}