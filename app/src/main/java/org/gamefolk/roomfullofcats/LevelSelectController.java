package org.gamefolk.roomfullofcats;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.gamefolk.roomfullofcats.game.Level;
import org.gamefolk.roomfullofcats.utils.FXUtils;
import org.gamefolk.roomfullofcats.utils.JsonUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class LevelSelectController implements Initializable {

    private static final Logger Log = Logger.getLogger(RoomFullOfCatsApp.class.getName());
    List<Level> levels = new ArrayList<>();

    @FXML private ListView<Level> levelsView;
    @FXML private Parent root;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadLevels();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        levelsView.setCellFactory((levelListView) -> new LevelSelectCell());
        levelsView.getItems().addAll(levels);
    }

    @FXML
    private void playLevel(MouseEvent event) {
        Level selectedLevel = levelsView.getSelectionModel().getSelectedItem();
        if (selectedLevel == null) {
            // We didn't select a level that's been loaded.
            return;
        }

        FXUtils.TransitionInfo<GameController> gameTransition = FXUtils.transitionScene(event, "/fxml/game.fxml");

        // Ensure that the canvas is the correct size before starting the game.
        Parent gameView = gameTransition.getParent();
        gameView.layout();

        // Since Android likes to cut off long stack traces, if any Exceptions are thrown in the game we determine
        // their root cause and log it before propagating.
        try {
            gameTransition.getController().startGame(selectedLevel);
        } catch (Exception e) {
            Throwable cause;
            Throwable result = e;

            while ((cause = result.getCause()) != null && result != cause) {
                result = cause;
            }

            Log.log(java.util.logging.Level.SEVERE, "Caught fatal exception:", result);
            throw e;
        }
    }

    private void loadLevels() throws IOException {
        levelsView.getItems().clear();

        JsonObject levelObject;
        try {
            levelObject = JsonUtils.readJsonResource("/assets/levelSelect.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // TODO:
        // For now, we are going to load all levels at once. It might be more efficient to load them on-demand,
        // but we don't have many levels.
        JsonArray levelOrder = levelObject.get("levelOrder").asArray();
        for (int i = 0; i < levelOrder.size(); i++) {
            String levelFilename = levelOrder.get(i).asString();
            Level level = Level.loadLevel("/assets/levels/" + levelFilename, i);
            levelsView.getItems().add(level);
        }

        Log.info("Loaded " + levelOrder.size() + " levels");
    }

    @FXML
    private void clearData() {
        // TODO: Add confirmation, as of now JavaFXports does not support Alert
        Settings.INSTANCE.putJson("progress", new JsonObject());
        try {
            loadLevels();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
