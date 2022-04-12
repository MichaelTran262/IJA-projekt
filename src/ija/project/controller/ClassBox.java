package ija.project.controller;

import ija.project.model.ClassDiagram;
import ija.project.model.UMLAttribute;
import ija.project.model.UMLClass;
import javafx.event.Event;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class ClassBox extends StackPane {
    private UMLClass cl;
    private ClassDiagram diag;
    private TextField classTitle;
    private ArrayList<TextField> attributes = new ArrayList<TextField>();
    private ArrayList<Connection> connections = new ArrayList<Connection>();
    private Rectangle rectangle = new Rectangle(100, 160, Color.WHITE);

    public ClassBox(UMLClass cl) {
        super();
        this.cl = cl;
        rectangle.setStyle("-fx-fill: #ffffff; -fx-stroke: #000000; -fx-border-color: #000000; -fx-stroke-width: 1;");
        this.setId(cl.getName());
        classTitle = new TextField(this.getId());

        classTitle.setTranslateY(-50);
        classTitle.setFont(Font.font("Verdana", FontWeight.BOLD,11));
        classTitle.setMaxWidth(rectangle.getWidth()-2);
        update();
        this.setContextMenu(this);
    }

    public void appendConnection(Connection connection) {
        connections.add(connection);
    }

    public ArrayList<Connection> getConnections() {
        return connections;
    }


    public void setContextMenu(Node node){
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem menuItem1 = new MenuItem("Přidat atribut");
            menuItem1.setOnAction(actionEvent -> {
                addClassAttribute();
            });
            contextMenu.getItems().add(menuItem1);
            node.setOnContextMenuRequested(contextEvent -> {
                        contextMenu.show(node, contextEvent.getScreenX(), contextEvent.getScreenY());
                }
            );
        });
    }

    public void setContextMenu(TextField tf){
        System.out.println("RIGHT CLICK setContext");
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Přidat atribut");
        menuItem1.setOnAction(actionEvent -> {
            addClassAttribute();
        });
        MenuItem menuItem2 = new MenuItem("Smazat atribut");
        menuItem2.setOnAction(actionEvent -> {
            removeClassAttribute(tf.getText());
        });
        contextMenu.getItems().addAll(menuItem1, menuItem2);
        tf.setContextMenu(contextMenu);
    }

    public void addClassAttribute() {
        this.cl.addAttribute(new UMLAttribute("Nový atribut"));
        this.getChildren().clear();
        update();
    }

    public void removeClassAttribute(String name) {
        this.cl.removeAttribute(name);
        this.getChildren().clear();
        System.out.println(cl.getAttributes().toString());
        update();
    }

    public void update(){
        int posY = -20;
        attributes.clear();
        for ( UMLAttribute attr : cl.getAttributes()) {
            TextField attrText = new TextField(attr.toString());
            attrText.setTranslateY(posY);
            attrText.setMaxWidth(rectangle.getWidth() - 10);
            attributes.add(attrText);
            attrText.setId("Textfield " + 1+attributes.size());
            setContextMenu(attrText);
            System.out.println(rectangle.getWidth() + " TExtfield width" +attrText.getMaxWidth());
            posY += 27;
        }
        rectangle.toBack();
        this.getChildren().addAll(rectangle, classTitle);
        for (TextField attr : attributes) {
            this.getChildren().add(attr);
        }
        System.out.println(this.getHeight());
    }
}
