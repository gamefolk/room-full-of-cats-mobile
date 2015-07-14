package org.gamefolk.roomfullofcats;

public interface AdvertisingProvider {
    void initializeAdService();
    String getAdvertisingIdentifier();
    String getUserAgent();
    boolean getDoNotTrack();
}
