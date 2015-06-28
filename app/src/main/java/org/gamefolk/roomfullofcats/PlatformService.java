package org.gamefolk.roomfullofcats;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Logger;

public class PlatformService {
    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());

    private static PlatformService instance;
    public static synchronized PlatformService getInstance() {
        if (instance == null) {
            instance = new PlatformService();
        }
        return instance;
    }

    private final ServiceLoader<PlatformProvider> serviceLoader;
    private PlatformProvider provider;

    private PlatformService() {
        serviceLoader = ServiceLoader.load(PlatformProvider.class);

        Iterator<PlatformProvider> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            if (provider == null) {
                provider = iterator.next();
                Log.info("Using PlatformProvider: " + provider.getClass().getName());
            } else {
                Log.info("This PlatformProvider is ignored: " + iterator.next().getClass().getName());
            }
        }

        if (provider == null) {
            Log.severe("No PlatformProvider implementation could be found!");
        }
    }

    public PlatformProvider.Platform getPlatform() {
        return provider.getPlatform();
    }
}
