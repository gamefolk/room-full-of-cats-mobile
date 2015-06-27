package com.adsdk.sdk.nativeads;

/**
 * Thrown when the JSON response from the server is missing required values.
 */
public class MissingValueException extends Exception {
    public MissingValueException(String message) {
        super(message);
    }
}
