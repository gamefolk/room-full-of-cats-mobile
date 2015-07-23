package org.gamefolk.roomfullofcats.utils;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import org.gamefolk.roomfullofcats.RoomFullOfCatsApp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class JsonUtils {

    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());

    /**
     * Read an input stream into a JSON object.
     * @param resource A JAR resource path
     * @return A JSON object containing the JSON data from the InputStream.
     * @throws FileNotFoundException The JAR resource path does not contain a file.
     * @throws java.io.IOException The input stream is invalid
     */
    public static JsonObject readJsonResource(String resource) throws IOException {
        InputStream input = JsonUtils.class.getResourceAsStream(resource);
        if (input == null) {
            throw new FileNotFoundException("Could not find resource: " + resource);
        }

        JsonObject object = Json.parse(new InputStreamReader(input)).asObject();
        Log.info("Read json: " + object.toString());
        return object;
    }
}
