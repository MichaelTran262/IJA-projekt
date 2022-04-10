package ija.project.model;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class ClassBox extends StackPane {
    private ClassDiagram diagram;
    private ArrayList<Connection> spojeniStart;
    private ArrayList<Connection> spojeniEnd;
    private Text classTitle;
    private ArrayList<Text> attributes;

    public ClassBox() {
        super();
        Rectangle rectangle = new Rectangle(40,80, Color.WHITE);
        rectangle.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;");
        classTitle = new Text("Title");
        this.getChildren().addAll(rectangle, classTitle);
    }
}
