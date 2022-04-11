package ija.project.controller;

import ija.project.model.ClassDiagram;
import ija.project.model.UMLAttribute;
import ija.project.model.UMLClass;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class ClassBox extends StackPane {
    private UMLClass cl;
    private ClassDiagram diag;
    private Text classTitle;
    private ArrayList<Text> attributes = new ArrayList<Text>();
    private ArrayList<Connection> connections = new ArrayList<Connection>();

    public ClassBox(UMLClass cl) {
        super();
        this.cl = cl;
        Rectangle rectangle = new Rectangle(60, 100, Color.WHITE);
        rectangle.setStyle("-fx-fill: white; -fx-stroke: black; -fx-border-color: black; -fx-stroke-width: 1;");
        this.setId(cl.getName());
        classTitle = new Text(this.getId());
        classTitle.setUnderline(true);
        classTitle.setTranslateY(-35);
        int posY = -20;
        for (UMLAttribute attr : cl.getAttributes()) {
            Text attrText = new Text(attr.toString());
            attrText.setTranslateY(posY);
            attributes.add(attrText);
            if (rectangle.getWidth() + 3 < attrText.getLayoutBounds().getWidth()) {
                rectangle.setWidth(attrText.getLayoutBounds().getWidth() + 5);
            }
            posY += attrText.getLayoutBounds().getHeight();
        }
        this.getChildren().addAll(rectangle, classTitle);
        for (Text attr : attributes) {
            this.getChildren().add(attr);
        }
    }

    public void appendConnection(Connection connection) {
        connections.add(connection);
    }

    public ArrayList<Connection> getConnections() {
        return connections;
    }

    public UMLClass getUMLClass() {
        return cl;
    }
}
