package org.gamefolk.roomfullofcats;

import com.gluonhq.charm.down.common.Platform;
import com.gluonhq.charm.down.common.PlatformFactory;
import com.gluonhq.charm.down.common.SettingService;

public enum Settings {
    INSTANCE;

    private Platform platform = PlatformFactory.getPlatform();
    private SettingService settingService = platform.getSettingService();

    public boolean getBoolean(String key, boolean defaultValue) {
        String result = settingService.retrieve(key);

        if (result == null) {
            return defaultValue;
        }

        return Boolean.parseBoolean(result);
    }

    public void putBoolean(String key, boolean value) {
        settingService.store(key, Boolean.toString(value));
    }

    public String getString(String key, String defaultValue) {
        String result = settingService.retrieve(key);

        if (result == null) {
            return defaultValue;
        }

        return result;
    }

    public void putString(String key, String value) {
        settingService.store(key, value);
    }
}
