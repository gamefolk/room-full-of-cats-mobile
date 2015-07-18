package org.gamefolk.roomfullofcats;

import java.io.File;

public interface PlatformProvider {
    enum Platform {
        ANDROID ("Android"),
        IOS ("iOS"),
        DESKTOP ("Desktop");

        private final String name;

        Platform(String s) {
            name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    Platform getPlatform();
    File getCacheDir();
}
