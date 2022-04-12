package ija.project.controller;

import ija.project.model.ClassDiagram;
import ija.project.model.UMLAttribute;
import ija.project.model.UMLClass;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class ClassBox extends StackPane {
    private UMLClass cl;
    private ClassDiagram diag;
    private Text classTitle;
    private ArrayList<TextField> attributes = new ArrayList<TextField>();
    private ArrayList<Connection> connections = new ArrayList<Connection>();

    public ClassBox(UMLClass cl) {
        super();
        this.cl = cl;
        Rectangle rectangle = new Rectangle(100, 120, Color.WHITE);
        rectangle.setStyle("-fx-fill: white; -fx-stroke: black; -fx-border-color: black; -fx-stroke-width: 1;");
        this.setId(cl.getName());
        classTitle = new Text(this.getId());
        classTitle.setUnderline(true);
        classTitle.setTranslateY(-50);
        int posY = -20;
        for (UMLAttribute attr : cl.getAttributes()) {
            TextField attrText = new TextField(attr.toString());
            attrText.setTranslateY(posY);
            attrText.setMaxWidth(rectangle.getWidth() - 10);
            attributes.add(attrText);
            System.out.println(rectangle.getWidth() + " TExtfield width" +attrText.getMaxWidth());
            if (rectangle.getWidth() + 3 < attrText.getMaxWidth()) {
                rectangle.setWidth(attrText.getMaxWidth() + 5);
            }
            posY += 27;
        }
        this.getChildren().addAll(rectangle, classTitle);
        for (TextField attr : attributes) {
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
