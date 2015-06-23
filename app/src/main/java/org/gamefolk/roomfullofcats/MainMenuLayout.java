package org.gamefolk.roomfullofcats;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class MainMenuLayout extends VBox {
    private static final int CHILD_PADDING = 5;

    private HBox catsLogo;
	private Button startButton;
	private Button tutorialButton;

	static class SettingsView extends HBox {
		private CheckBox musicBox;
		private CheckBox soundBox;

		public SettingsView() {
            this.setAlignment(Pos.CENTER);
            this.setStyle("-fx-background-color: #1E1E1E");
            this.setOpacity(.75);

			musicBox = new CheckBox("Music");
            musicBox.setAlignment(Pos.CENTER);
            musicBox.setMaxWidth(Double.MAX_VALUE);
            musicBox.setTextFill(Color.WHITE);
            musicBox.setSelected(true);
            musicBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                    // ...
                }
            });

			soundBox = new CheckBox("Sound");
            soundBox.setAlignment(Pos.CENTER);
            soundBox.setMaxWidth(Double.MAX_VALUE);
            soundBox.setTextFill(Color.WHITE);
            soundBox.setSelected(true);
			soundBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                    // ...
                }
            });

            this.getChildren().addAll(musicBox, soundBox);
            this.setPadding(new Insets(10, 0, 10, 0));
            setHgrow(musicBox, Priority.ALWAYS);
            setHgrow(soundBox, Priority.ALWAYS);
		}
	}

	public MainMenuLayout(EventHandler<ActionEvent> startClick, EventHandler<ActionEvent> tutorialClick) {
        super(CHILD_PADDING);

        String backgroundImageUrl = RoomFullOfCatsApp.class.getResource("/img/menu.png").toExternalForm();
        this.setStyle("-fx-background-image: url('" + backgroundImageUrl + "'); " +
           "-fx-background-position: center center; " +
           "-fx-background-repeat: stretch;");

        catsLogo = new HBox();
        catsLogo.setAlignment(Pos.CENTER);
        Image catsLogoImage = new Image(RoomFullOfCatsApp.class.getResource("/img/catslogo.png").toString());
        catsLogo.getChildren().add(new ImageView(catsLogoImage));

		startButton = new Button("Start");
        startButton.setOpacity(.75);
        startButton.setMaxWidth(Double.MAX_VALUE);
        startButton.setStyle("-fx-base: #aaaaaa;");
		startButton.setTextFill(Color.WHITE);
        startButton.setOnAction(startClick);

		tutorialButton = new Button("Tutorial");
        tutorialButton.setOpacity(.75);
        tutorialButton.setMaxWidth(Double.MAX_VALUE);
        tutorialButton.setStyle("-fx-base: #aaaaaa;");
        tutorialButton.setTextFill(Color.WHITE);
        tutorialButton.setOnAction(tutorialClick);

        this.getChildren().addAll(catsLogo, startButton, tutorialButton, new SettingsView());
	}
}
