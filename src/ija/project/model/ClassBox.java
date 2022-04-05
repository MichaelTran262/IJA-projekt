package ija.project.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class ClassBox extends Rectangle {
    private ClassDiagram diagram;
    private List<Connection> spojeniStart;
    private List<Connection> spojeniEnd;

    public ClassBox(double i, double i1) {
        super(i,i1, Color.WHITE);
    }

    public void addStart(Connection newSpojeni){
        spojeniStart.add(newSpojeni);
    }
    public void addEnd(Connection newSpojeni){
        spojeniEnd.add(newSpojeni);
    }
    public void change(double x, double y){
        this.setTranslateX(x);
        this.setTranslateY(y);
        for (Connection connection:spojeniStart) {
            connection.setStartX(x);
            connection.setStartY(y);
        }
        for (Connection connection:spojeniEnd) {
            connection.setEndX(x);
            connection.setEndY(y);
        }
    }
}
