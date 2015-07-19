/*
 * Based on MobFox Android SDK code (https://github.com/mobfox/MobFox-Android-SDK)
 * Modified for AbsurdEngine under the MoPub Client License (/3rdparty-license/adsdk-LICENSE.txt)
 */

package com.adsdk.sdk.nativeads;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.ParseException;
import com.gluonhq.charm.down.common.PlatformFactory;
import org.gamefolk.roomfullofcats.*;
import org.gamefolk.roomfullofcats.utils.UrlBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Represents a MobFox API request.
 *
 * @see <a href="http://dev.mobfox.com/index.php?title=Ad_Request_API_-_Native">MobFox Native Ad Request API</a>
 */
public class NativeAdRequest {

    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());
    private static final String REQUEST_URL = "http://my.mobfox.com/request.php";
    private static final String REQUEST_TYPE = "native";
    private static final String RESPONSE_TYPE = "json";
    private static final String REQUEST_TYPE_ANDROID = "android_app";
    private static final String REQUEST_TYPE_IPHONE = "iphone_app";
    private static final Random RAND = new Random();
    private static final int RANDOM_RANGE = 50_000;
    private static final String URL_ENCODING = "UTF-8";
    private static final String RESPONSE_ENCODING = "ISO-8859-1";
    private static final String REQUEST_API_VERSION = "3.0";
    private URL requestUrl;

    private NativeAdRequest(Builder b) throws UnsupportedEncodingException {
        PlatformService platformService = PlatformService.getInstance();
        AdvertisingService advertisingService = AdvertisingService.getInstance();

        // Add required fields
        UrlBuilder urlBuilder = UrlBuilder.fromString(REQUEST_URL)
                .addParameter("r_type", REQUEST_TYPE)
                .addParameter("r_resp", RESPONSE_TYPE)
                .addParameter("s", b.publisherId)
                .addParameter("u", URLEncoder.encode(advertisingService.getUserAgent(), URL_ENCODING))
                .addParameter("i", URLEncoder.encode(getDeviceIp(), URL_ENCODING))
                .addParameter("r_random", Integer.toString(RAND.nextInt(RANDOM_RANGE)))
                .addParameter("v", REQUEST_API_VERSION);

        // TODO: Send any device headers

        // Set Platform-specific request parameters
        switch (PlatformFactory.getPlatform().getName()) {
            case PlatformFactory.ANDROID:
                urlBuilder = urlBuilder.addParameter("rt", REQUEST_TYPE_ANDROID)
                        .addParameter("o_andadvid", advertisingService.getAdvertisingIdentifier())
                        .addParameter("o_andadvdnt", Integer.toString(advertisingService.getDoNotTrack() ? 1 : 0));
                break;
            case PlatformFactory.IOS:
                urlBuilder = urlBuilder.addParameter("rt", REQUEST_TYPE_IPHONE)
                        .addParameter("o_iosadvid", advertisingService.getAdvertisingIdentifier())
                        .addParameter("o_iosadlimit", Integer.toString(advertisingService.getDoNotTrack() ? 1 : 0));
                break;
            default:
                throw new RuntimeException("Invalid advertising platform: " + PlatformFactory.getPlatform().getName());
        }

        // Set optional headers
        if (!b.imageTypes.isEmpty()) {
            urlBuilder = urlBuilder.addParameter("n_img", joinString(",", b.imageTypes));
        }

        if (!b.textTypes.isEmpty()) {
            urlBuilder = urlBuilder.addParameter("n_text", joinString(",", b.textTypes));
        }

        if (!b.adTypes.isEmpty()) {
             urlBuilder = urlBuilder.addParameter("n_type", joinString(",", b.adTypes));
        }

        if (b.userAge.isPresent()) {
            int userAge = b.userAge.get();
            urlBuilder = urlBuilder.addParameter("demo_age", Integer.toString(userAge));
        }

         if (!b.keywords.isEmpty()) {
             urlBuilder = urlBuilder.addParameter("demo_keywords", joinString(",", b.keywords));
         }

        if (b.latLong.isPresent()) {
            double latitude = b.latLong.get().latitude;
            double longitude = b.latLong.get().longitude;

            urlBuilder = urlBuilder.addParameter("latitude", Double.toString(latitude))
                    .addParameter("longitude", Double.toString(longitude));
        }

        this.requestUrl = urlBuilder.toUrl();
    }

    private static String getDeviceIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        // TODO: Split off interface if it exists.

                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.warning("Socket exception caught: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Drop in replacement for Java 8's {@link String#join(CharSequence, Iterable)}.
     */
    private static String joinString(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
        boolean first = true;
        StringBuilder builder = new StringBuilder();
        for (CharSequence sequence : elements) {
            if (!first) {
                builder.append(delimiter);
            }
            builder.append(sequence);
            first = false;
        }

        return builder.toString();
    }

    private static JsonObject parseResponse(InputStream responseStream) throws IOException {
        String response;
        try (Scanner s = new Scanner(responseStream, RESPONSE_ENCODING)) {
            // Get the entire stream
            response = s.useDelimiter("\\A").next();
        } catch (NoSuchElementException e) {
            throw new IOException("The response was empty", e);
        }

        JsonObject responseJson;
        try {
            responseJson = JsonObject.readFrom(response);
        } catch (ParseException e) {
            throw new IOException("Could not parse the JSON response", e);
        }

        return responseJson;
    }

    public void sendRequestAsync(Callback<NativeAd> onFinish) {
        new Thread(() -> {
            try {
                Log.info("Sending request: " + requestUrl.toExternalForm());

                HttpURLConnection connection = (HttpURLConnection)requestUrl.openConnection();
                connection.setRequestProperty("Accept-Charset", URL_ENCODING);

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream responseStream = connection.getInputStream();
                    JsonObject responseJson = parseResponse(responseStream);
                    Log.info("Got ad response: " + responseJson);

                    NativeAd ad;
                    try {
                       ad = new NativeAd(responseJson);
                    } catch (MissingValueException e) {
                        throw new IOException("The response was missing required values.", e);
                    }
                    onFinish.completed(ad);
                } else {
                    throw new IOException("Got bad response code from the server: " + connection.getResponseCode());
                }
            } catch (IOException e) {
                Log.warning("Something went wrong while parsing the ad response: " + e.getMessage());
                onFinish.failed();
            }
        }).start();
    }

    public interface Callback<T> {
        void completed(T result);
        void failed();
    }

    private static class LatLong {
        private double latitude;
        private double longitude;

        private LatLong(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    public static class Builder {
        private String publisherId;
        private List<String> imageTypes = Collections.emptyList();
        private List<String> textTypes = Collections.emptyList();
        private List<String> adTypes = Collections.emptyList();
        private Optional<Integer> userAge = Optional.empty();
        private List<String> keywords = Collections.emptyList();
        private Optional<LatLong> latLong = Optional.empty();

        // TODO: Use specific types?
        public Builder(String publisherId) {
            this.publisherId = publisherId;
        }

        public Builder setImageTypes(List<String> imageTypes) {
            this.imageTypes = imageTypes;
            return this;
        }

        public Builder setTextTypes(List<String> textTypes) {
            this.textTypes = textTypes;
            return this;
        }

        public Builder setAdTypes(List<String> adTypes) {
            this.adTypes = adTypes;
            return this;
        }

        public Builder setUserAge(int age) {
            this.userAge = Optional.of(age);
            return this;
        }

        public Builder setKeywords(List<String> keywords) {
            this.keywords = keywords;
            return this;
        }

        public Builder setLatLong(double latitude, double longitude) {
            this.latLong = Optional.of(new LatLong(latitude, longitude));
            return this;
        }

        public NativeAdRequest build() {
            try {
                return new NativeAdRequest(this);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
