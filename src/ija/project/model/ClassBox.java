package ija.project.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class ClassBox extends Rectangle {
    private ClassDiagram diagram;
    private ArrayList<Connection> spojeniStart;
    private ArrayList<Connection> spojeniEnd;
    private Text classTitle;
    private ArrayList<Text> attributes;

    public ClassBox(double i, double i1) {
        super(i,i1, Color.WHITE);
        classTitle = new Text("Class Title");
    }

    public Text getClassTitle() {
        return classTitle;
    }

    public void addStart(Connection newSpojeni){
        spojeniStart.add(newSpojeni);
    }
    public void addEnd(Connection newSpojeni){
        spojeniEnd.add(newSpojeni);
    }
    public void change(double x, double y){
        //this.setTranslateX(x);
        //this.setTranslateY(y);
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
