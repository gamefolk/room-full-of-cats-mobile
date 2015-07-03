package org.gamefolk.roomfullofcats.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple URL builder class to simplify adding many parameters. Mirrors the API of
 * <a href="https://github.com/mikaelhg/urlbuilder">urlbuilder</a>, which cannot be used on iOS due to a core library
 * incompatibiility.
 *
 * This library has no guarantees about properly decoding non-ASCII URLs.
 */
public class UrlBuilder {
    private StringBuilder builder;
    private List<Parameter> parameters;

    private static class Parameter {
        String key;
        String value;

        public Parameter(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private UrlBuilder(String base) {
        builder = new StringBuilder(base);
        parameters = new ArrayList<>();
    }

    public static UrlBuilder fromString(String base) {
        return new UrlBuilder(base);
    }

    public UrlBuilder addParameter(String key, String value) {
        parameters.add(new Parameter(key, value));
        return this;
    }

    public URL toUrl() {
        builder.append("?");

        for (Parameter param : parameters) {
            builder.append(param.key + "=" + param.value);
            builder.append("&");
        }

        try {
            return new URL(builder.toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
