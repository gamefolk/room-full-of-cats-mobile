package org.gamefolk.roomfullofcats;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonValue;
import com.gluonhq.charm.down.common.Platform;
import com.gluonhq.charm.down.common.PlatformFactory;
import com.gluonhq.charm.down.common.SettingService;

public enum Settings {
    INSTANCE;

    private final Platform platform = PlatformFactory.getPlatform();
    private final SettingService settingService = platform.getSettingService();

    public boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getString(key, Boolean.toString(defaultValue)));
    }

    public void putBoolean(String key, boolean value) {
        putString(key, Boolean.toString(value));
    }

    public JsonValue getJson(String key, JsonValue defaultValue) {
        return Json.parse(getString(key, defaultValue.toString()));
    }

    public void putJson(String key, JsonValue json) {
        putString(key, json.toString());
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
