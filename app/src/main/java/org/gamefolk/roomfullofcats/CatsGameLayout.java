package org.gamefolk.roomfullofcats;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CatsGameLayout extends StackPane {
    private Canvas gameCanvas;
    private GraphicsContext graphicsContext;

    public CatsGameLayout(double width, double height) {
        gameCanvas = new Canvas(width, height);
        this.graphicsContext = gameCanvas.getGraphicsContext2D();
        this.getChildren().add(gameCanvas);
    }

    public GraphicsContext getGraphicsContext2D() {
        return this.graphicsContext;
    }
}
