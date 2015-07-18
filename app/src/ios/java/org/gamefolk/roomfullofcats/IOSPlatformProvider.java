package org.gamefolk.roomfullofcats;

import org.robovm.apple.foundation.*;

import java.io.File;
import java.util.List;

public class IOSPlatformProvider implements PlatformProvider {

    @Override
    public Platform getPlatform() {
        return Platform.IOS;
    }

    @Override
    public File getCacheDir() {
        List<String> paths = NSPathUtilities.getSearchPathForDirectoriesInDomains(NSSearchPathDirectory
                        .CachesDirectory,
                NSSearchPathDomainMask.UserDomainMask, true);
        String cachePath = paths.get(0);
        if (!NSFileManager.getDefaultManager().fileExists(cachePath)) {
            try {
                NSFileManager.getDefaultManager().createDirectoryAtPath(cachePath, false, null);
            } catch (NSErrorException e){
                throw new RuntimeException(e);
            }
        }
        return new File(cachePath);
    }
}
