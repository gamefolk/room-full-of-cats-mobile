package org.gamefolk.roomfullofcats;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Environment {
    private static Properties defaultProperties = new Properties();

    static {
        try (InputStream in = Environment.class.getResourceAsStream("/assets/environment.properties")) {
            defaultProperties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Environment() {}

    public static String getProperty(String key) {
        return defaultProperties.getProperty(key);
    }

    public static boolean getBooleanProperty(String key) {
        return "true".equals(defaultProperties.getProperty(key));
    }
}
