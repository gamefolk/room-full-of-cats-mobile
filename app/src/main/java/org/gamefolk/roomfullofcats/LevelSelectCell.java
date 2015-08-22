package org.gamefolk.roomfullofcats;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.text.Text;
import org.gamefolk.roomfullofcats.game.Level;

import java.io.IOException;

public class LevelSelectCell extends ListCell<Level> {

    @FXML private Parent root;
    @FXML private Text title;
    @FXML private Text status;

    public LevelSelectCell() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/levelSelectCell.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void updateItem(Level level, boolean empty) {
        super.updateItem(level, empty);

        setText(null);      // Otherwise it's set to root's toString()

        if (level != null) {
            title.setText(level.title);

            switch (level.getStatus()) {
                case UNPLAYED:
                    status.setText("NEW");
                    status.getStyleClass().setAll("new");
                    break;
                case WON:
                    status.setText("✓");
                    status.getStyleClass().setAll("won");
                    break;
                case LOST:
                    status.setText("");
                    break;
                default:
                    throw new RuntimeException("Level had unknown status.");
            }
        } else {
            title.setText("");
            status.setText("");
        }

        setGraphic(root);
    }
}
