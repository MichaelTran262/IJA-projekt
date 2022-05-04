package ija.project.controller;

import ija.project.model.ClassDiagram;
import ija.project.model.UMLAttribute;
import ija.project.model.UMLClass;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;


/**
 * @author      Thanh Q. Tran     <xtrant02 @ stud.fit.vutbr.cz>
 * @author      Lukáš Fuis      <xfuisl00 @ stud.fit.vutbr.cz>
 * @version     0.5
 */

/**
 *  Třída reprezentující GUI třídy UMLClass
 */
public class ClassBox extends StackPane {
    private UMLClass cl;
    private TextField classTitle;
    private ArrayList<TextField> attributes = new ArrayList<TextField>();
    private ArrayList<Connection> connections = new ArrayList<Connection>();
    private Rectangle rectangle = new Rectangle(100, 160, Color.WHITE);

    /**
     * Konstruktor třidy ClassBox, nastavuje vlastnosti GUI komponentů
     * @param cl Třída UML, která Classbox reprezentuje
     */
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

    /**
     * Přidá instanci třidy reprezentující spojení mezi dvěma třidami
     * @param connection Proměnná třidy Connection, která je ,,propojena'' mezi dvěma ClassBox instancemi
     */
    public void appendConnection(Connection connection) {
        connections.add(connection);
    }

    /**
     * Konstruktor třidy ClassBox, nastavuje vlastnosti GUI komponentů
     * @param connection Proměnná třidy Connection, která je ,,propojena'' mezi dvěma ClassBox instancemi
     */
    public ArrayList<Connection> getConnections() {
        return connections;
    }

    /**
     * Funkce přidává contextMenu dané GUI kompomenty.
     * @param node komponenta, do které se přidá contextMenu
     */
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

    /**
     * Funkce přetěžuje výchozí contextMenu třídy TextField a přidává nový.
     * @param node komponenta, do které se přidá contextMenu
     */
    public void setContextMenu(TextField tf){
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

    /**
     * Funkce přidá atribut do třidy reprezentující model třídy z jazyka UMLenu
     */
    public void addClassAttribute() {
        this.cl.addAttribute(new UMLAttribute("Nový atribut"));
        this.getChildren().clear();
        update();
    }

    public void addClassAttribute(String attr) {
        this.cl.addAttribute(new UMLAttribute(attr));
        this.getChildren().clear();
        update();
    }

    /**
     * Funkce odstraní atribut z třídy reprezentující model třídy z jazyka UMLenu
     * @param name jméno atributu, která bude odstraněna z instance UML třídy
     */
    public void removeClassAttribute(String name) {
        this.cl.removeAttribute(name);
        this.getChildren().clear();
        update();
    }

    public List<UMLAttribute> getClassAttributes() {
        return this.cl.getAttributes();
    }

    /**
     * Funkce změní jméno atributu (zatím nefunkční)
     * @param name jméno atributu, která bude změnena v instanci UML třídy
     */
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
            posY += 27;
        }
        rectangle.toBack();
        this.getChildren().addAll(rectangle, classTitle);
        for (TextField attr : attributes) {
            this.getChildren().add(attr);
        }
    }

    public String getClassName(){
        return this.cl.getName();
    }

    public Rectangle getRectangle() {
        return rectangle;
    }
}