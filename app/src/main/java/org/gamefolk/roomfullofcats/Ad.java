package org.gamefolk.roomfullofcats;

import com.adsdk.sdk.nativeads.NativeAd;
import com.adsdk.sdk.nativeads.NativeAdRequest;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Ad extends BorderPane implements Initializable {

    @FXML private VBox descriptionContainer;
    @FXML private Text description;
    @FXML private ImageView icon;

    private final List<String> imageTypes = Arrays.asList("icon", "main");
    private final List<String> textTypes = Arrays.asList("headline", "description", "cta", "advertiser", "rating");
    private final List<String> keywords = new ArrayList<>();

    public Ad() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ad.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        description.wrappingWidthProperty().bind(descriptionContainer.widthProperty());

        if (PlatformFeatures.ADS_SUPPORTED) {
            requestAd();
        }
    }

    private void requestAd() {
         NativeAdRequest adRequest = getRequest();

         adRequest.sendRequestAsync(new NativeAdRequest.Callback<NativeAd>() {
             @Override
             public void completed(NativeAd ad) {
                 adLoaded(ad);
             }

             @Override
             public void failed() {
                 adFailedToLoad();
             }
         });
    }

    private void adLoaded(NativeAd nativeAd) {
        this.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        description.setText(nativeAd.getTextAsset("description"));
        icon.setImage(nativeAd.getImageAsset("icon"));
    }

    private void adFailedToLoad() {
        description.setText("ad failed to load");
    }

    private NativeAdRequest getRequest() {
        AdvertisingService adService = AdvertisingService.getInstance();

        return new NativeAdRequest.Builder(adService.getMobFoxPublisherId())
                .setTextTypes(textTypes)
                .setImageTypes(imageTypes)
                .setKeywords(keywords)
                .build();
    }
}
