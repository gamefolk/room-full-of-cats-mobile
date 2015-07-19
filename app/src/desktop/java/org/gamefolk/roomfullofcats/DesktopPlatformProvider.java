package org.gamefolk.roomfullofcats;

import java.io.File;

public class DesktopPlatformProvider implements PlatformProvider {

    @Override
    public File getCacheDir() {
        return new File(System.getProperty("java.io.tmpdir"));
    }
}
