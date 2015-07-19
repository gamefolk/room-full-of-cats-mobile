/*
 * Based on MobFox Android SDK code (https://github.com/mobfox/MobFox-Android-SDK)
 * Modified for AbsurdEngine under the MoPub Client License (/3rdparty-license/adsdk-LICENSE.txt)
 */

package com.adsdk.sdk.nativeads;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a native ad with text and image assets and impression trackers
 */
public class NativeAd {
    private String clickUrl;
    private final Map<String, Image> imageAssets = new HashMap<>();
    private final Map<String, String> textAssets = new HashMap<>();
    private final List<Tracker> trackers = new ArrayList<>();

    public NativeAd(JsonObject json) throws MissingValueException {
        JsonValue imageAssetsValue = json.get("imageassets");
        if (imageAssetsValue == null) {
            throw new MissingValueException("imageassets not found");
        }

        JsonObject imageAssetsObject = imageAssetsValue.asObject();
        for (String imageType : imageAssetsObject.names()) {
            JsonObject assetObject = imageAssetsObject.get(imageType).asObject();
            String url = assetObject.get("url").asString();
            if (url.isEmpty()) {
                // Sometimes MobFox returns empty URLs. In this case, it doesn't make any sense to parse the rest of
                // the image, because there's nothing to display.
                continue;
            }
            int requestedWidth = Integer.parseInt(assetObject.get("width").asString());
            int requestedHeight = Integer.parseInt(assetObject.get("height").asString());
            Image image = new Image(url, requestedWidth, requestedHeight, true, true, true);
            imageAssets.put(imageType, image);
        }

        JsonValue textAssetsValue = json.get("textassets");
        if (textAssetsValue == null) {
            throw new MissingValueException("textassets not found");
        }

        JsonObject textAssetsObject = textAssetsValue.asObject();
        for (String textType : textAssetsObject.names()) {
            String text = textAssetsObject.get(textType).asString();
            textAssets.put(textType, text);
        }

        clickUrl = json.get("click_url").asString();

        JsonValue trackersValue = json.get("trackers");
        if (trackersValue == null) {
            throw new MissingValueException("trackers not found");
        }

        JsonArray trackersArray = trackersValue.asArray();
        for (JsonValue trackerValue : trackersArray) {
            JsonObject trackerObject = trackerValue.asObject();
            String trackerType = trackerObject.get("type").asString();
            String trackerUrl = trackerObject.get("url").asString();
            trackers.add(new Tracker(trackerType, trackerUrl));
        }
    }

    public static class Tracker {
        final String type;
        final String url;

        private Tracker(String type, String url) {
            this.type = type;
            this.url = url;
        }
    }

    public String getClickUrl() {
        return clickUrl;
    }

    public String getTextAsset(String type) {
        return textAssets.get(type);
    }

    public Image getImageAsset(String type) {
        return imageAssets.get(type);
    }

    public List<Tracker> getTrackers() {
        return trackers;
    }
}
