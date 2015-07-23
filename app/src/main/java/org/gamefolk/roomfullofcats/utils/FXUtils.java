package org.gamefolk.roomfullofcats.utils;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class FXUtils {
    public static class TransitionInfo<T> {
        private T controller;
        private Parent parent;

        private TransitionInfo(Parent parent, T controller) {
            this.controller = controller;
            this.parent = parent;
        }

        public T getController() {
            return controller;
        }

        public Parent getParent() {
            return parent;
        }
    }

    /**
     * Transition between JavaFX scenes.
     * @param event The event that triggered the transition.
     * @param sceneFxml The FXML to load for the next scene.
     * @param <T> The type of the controller for the next scene.
     * @return A {@link org.gamefolk.roomfullofcats.utils.FXUtils.TransitionInfo} object containing the {@link
     * Parent} of the next scene and the controller of the next scene.
     */
    public static <T> TransitionInfo<T> transitionScene(Event event, String sceneFxml) {
        FXMLLoader fxmlLoader = new FXMLLoader(FXUtils.class.getResource(sceneFxml));

        Parent newParent;
        try {
            newParent = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(newParent);

        return new TransitionInfo<>(newParent, fxmlLoader.getController());
    }
}
