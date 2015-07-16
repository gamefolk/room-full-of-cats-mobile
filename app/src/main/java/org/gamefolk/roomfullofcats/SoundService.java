package org.gamefolk.roomfullofcats;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * Service that provides platform-independent interfaces to sound playing.
 *
 * // TODO: Deprecate when https://bitbucket.org/javafxports/android/issue/47/app-crashes-with-media-api is closed.
 */
public class SoundService {
    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());

    private static SoundService instance;
    public static synchronized SoundService getInstance() {
        if (instance == null) {
            instance = new SoundService();
        }
        return instance;
    }

    private final ServiceLoader<SoundProvider> serviceLoader;
    private SoundProvider provider;

    private SoundService() {
        serviceLoader = ServiceLoader.load(SoundProvider.class);

        Iterator<SoundProvider> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            if (provider == null) {
                provider = iterator.next();
                Log.info("Using SoundProvider: " + provider.getClass().getName());
            } else {
                break;
            }
        }
    }

    public Sound loadSound(String filename) {
        return provider.loadSound(filename);
    }

    public MusicPlayer loadMusic(String filename) {
        return provider.loadMusic(filename);
    }
}