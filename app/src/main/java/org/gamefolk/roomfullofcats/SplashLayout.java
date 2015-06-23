package org.gamefolk.roomfullofcats;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class SplashLayout extends VBox {
    private Image splashImage;

    public SplashLayout(EventHandler<Event> onClickHandler) {
        this.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setAlignment(Pos.CENTER);
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, onClickHandler);
        splashImage = new Image(RoomFullOfCatsApp.class.getResource("/img/gamefolklogo.png").toString());
        this.getChildren().add(new ImageView(splashImage));
    }
}
