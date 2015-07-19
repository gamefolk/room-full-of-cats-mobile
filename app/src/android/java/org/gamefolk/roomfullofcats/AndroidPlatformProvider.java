package org.gamefolk.roomfullofcats;

import android.content.Context;
import java.io.File;
import javafxports.android.FXActivity;

public class AndroidPlatformProvider implements PlatformProvider {

    @Override
    public File getCacheDir() {
        Context context = FXActivity.getInstance();
        return context.getCacheDir();
    }
}
