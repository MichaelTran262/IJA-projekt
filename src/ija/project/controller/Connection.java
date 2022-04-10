package ija.project.controller;

import ija.project.controller.ClassBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Connection extends Line {
    private ClassBox start;
    private ClassBox end;

    public Connection(ClassBox start, ClassBox end){
        super();
        this.startXProperty().bind(start.layoutXProperty().add(start.widthProperty().divide(2)));
        this.startYProperty().bind(start.layoutYProperty().add(start.heightProperty().divide(2)));
        this.endXProperty().bind(end.layoutXProperty().add(end.widthProperty().divide(2)));
        this.endYProperty().bind(end.layoutYProperty().add(end.heightProperty().divide(2)));
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(2);
        this.start = start;
        this.end = end;
    }

    public ClassBox getStart() {
        return start;
    }

    public ClassBox getEnd() {
        return end;
    }
}
