package ija.project.controller;

import ija.project.model.ClassDiagram;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class ClassBox extends StackPane {
    private ClassDiagram diag;
    private Text classTitle;
    private ArrayList<Text> attributes = new ArrayList<Text>();
    private ArrayList<Connection> connections = new ArrayList<Connection>();

    public ClassBox(int number) {
        super();
        Rectangle rectangle = new Rectangle(40,80, Color.WHITE);
        rectangle.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;");
        this.setId(Integer.toString(number));
        classTitle = new Text("Title " + this.getId());
        Text attr1 = new Text("- Jmeno");
        Text attr2 = new Text("- Email");
        attr1.setTranslateY(10);
        attr2.setTranslateY(-10);
        this.getChildren().addAll(rectangle, classTitle, attr1, attr2);
    }

    public void appendConnection(Connection connection) {
        connections.add(connection);
    }

    public ArrayList<Connection> getConnections() {
        return connections;
    }
}
