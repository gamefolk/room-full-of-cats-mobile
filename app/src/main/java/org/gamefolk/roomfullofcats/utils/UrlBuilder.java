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
    private final StringBuilder builder;
    private final List<Parameter> parameters;

    private static class Parameter {
        final String key;
        final String value;

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

        String delimiter = "";
        for (Parameter param : parameters) {
            builder.append(delimiter);
            builder.append(param.key).append("=").append(param.value);
            delimiter = ",";
        }

        try {
            return new URL(builder.toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
