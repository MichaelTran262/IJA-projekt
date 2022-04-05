package ija.project.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Connection extends Line {
    private ClassBox start;
    private ClassBox end;

    public Connection(ClassBox start, ClassBox end){
        super(start.getX(),start.getY(),end.getX(),end.getY());
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(1);
        this.start = start;
        this.end = end;
    }
}
