package ija.project.controller;

import ija.project.model.ClassDiagram;
import ija.project.model.UMLAttribute;
import ija.project.model.UMLClass;
import ija.project.model.UMLOperation;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import javax.tools.Tool;
import java.util.ArrayList;
import java.util.List;

/**
 *  Třída reprezentující GUI třídy UMLClass
 * @author      Thanh Q. Tran     xtrant02
 * @version     1
 */
public class ClassBox extends StackPane {
    private UMLClass cl;

    private VBox vbox = new VBox(4);
    private TextField classTitle;
    private ArrayList<TextField> attributes = new ArrayList<TextField>();

    private ArrayList<TextField> methods = new ArrayList<>();

    private ArrayList<Connection> connections = new ArrayList<Connection>();

    private Rectangle rectangle = new Rectangle(200, 160, Color.WHITE);

    private Separator separator = new Separator();

    private Separator methodSeparator = new Separator();

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
        classTitle.setAlignment(Pos.CENTER);
        classTitle.setFont(Font.font("Verdana", FontWeight.BOLD,13));
        classTitle.setMaxWidth(rectangle.getWidth()-8);
        //vbox.setStyle("-fx-border-style: dashed; -fx-border-color: black");
        vbox.setAlignment(Pos.BASELINE_CENTER);
        vbox.getChildren().add(classTitle);
        separator.setOrientation(Orientation.HORIZONTAL);
        separator.setStyle("-fx-border-color: black; -fx-border-width: 0 0 1 0");
        methodSeparator.setOrientation(Orientation.HORIZONTAL);
        methodSeparator.setStyle("-fx-border-color: black; -fx-border-width: 0 0 1 0");
        setContextMenu(vbox);
        this.setContextMenu(this);
        this.getChildren().add(rectangle);
        update();
    }

    /**
     * Přidá instanci třidy reprezentující spojení mezi dvěma třidami
     * @param connection Proměnná třidy Connection, která je ,,propojena'' mezi dvěma ClassBox instancemi
     */
    public void appendConnection(Connection connection) {
        connections.add(connection);
    }

    /**
     * Odstraní instanci třidy reprezentující spojení mezi dvěma třidami
     * @param connection Proměnná třidy Connection, která je odstraněna
     */
    public void deleteConnection(Connection connection){ connections.remove(connection);}

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
            MenuItem menuItem2 = new MenuItem("Přidat metodu");
            menuItem1.setOnAction(actionEvent -> {
                addClassAttribute();
            });
            menuItem2.setOnAction(actionEvent -> {
                addClassOperation();
            });
            contextMenu.getItems().add(menuItem1);
            contextMenu.getItems().add(menuItem2);
            node.setOnContextMenuRequested(contextEvent -> {
                        contextMenu.show(node, contextEvent.getScreenX(), contextEvent.getScreenY());
                }
            );
        });
    }

    /**
     * Funkce přetěžuje výchozí contextMenu třídy TextField pro atributy.
     * @param tf TextField obsahující jméno atributu, který je odstraněn při kliknutí na "Smazat atribut"
     */
    public void setAttributeContextMenu(TextField tf){
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
     * Funkce přetěžuje výchozí contextMenu třídy TextField pro metody.
     * @param tf TextField obsahující jméno metody, která je odstraněna při kliknutí na "Smazat metodu"
     */
    public void setMethodContextMenu(TextField tf) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Přidat metodu");
        menuItem1.setOnAction(actionEvent -> {
            addClassOperation();
        });
        MenuItem menuItem2 = new MenuItem("Smazat metodu");
        menuItem2.setOnAction(actionEvent -> {
            removeClassOperation(tf.getText());
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
     * Přidá metodu třídě znázorňované tímto ClassBoxem
     */
    public void addClassOperation() {
        this.cl.addOperation(new UMLOperation("new_method()"));
        this.getChildren().clear();
        update();
    }

    /**
     * Přidá metodu třídě znázorňované tímto ClassBoxem
     * @param opName Jméno nové metody
     */
    public void addClassOperation(String opName) {
        this.cl.addOperation(new UMLOperation(opName));
        this.getChildren().clear();
        update();
    }

    /**
     * Funkce odstraní atribut z instance třídy tohoto ClassBoxu
     * @param name jméno atributu, který bude odstraněn
     */
    public void removeClassAttribute(String name) {
        this.cl.removeAttribute(name);
        this.getChildren().clear();
        update();
    }

    /**
     * Funkce odstraní metodu z instance třídy tohoto ClassBoxu
     * @param name jméno metody, která bude odstraněna
     */
    public void removeClassOperation(String name) {
        this.cl.removeOperation(name);
        this.getChildren().clear();
        update();
    }

    public List<UMLAttribute> getClassAttributes() {
        return this.cl.getAttributes();
    }

    /**
     * Funkce aktualizující ClassBox po změně
     */
    public void update(){
        attributes.clear();
        vbox.getChildren().clear();
        this.getChildren().clear();
        this.getChildren().add(rectangle);
        for ( UMLAttribute attr : cl.getAttributes()) {
            TextField attrText = new TextField(attr.toString());
            attrText.setMaxWidth(rectangle.getWidth() - 20);
            attrText.textProperty().addListener((observableValue, oldValue, newValue) -> {
                attr.setName(newValue);
                if(!attr.hasModifier()){
                    attrText.setStyle("-fx-text-inner-color: red");
                    Tooltip tooltip = new Tooltip("Chybí modifikátor přístupu");
                    attrText.setTooltip(tooltip);
                } else {
                    attrText.setStyle("-fx-text-inner-color: black");
                    attrText.setTooltip(null);
                }
                //System.out.println("textfield changed from " + oldValue + " to " + attr.getName());
            });
            attributes.add(attrText);
            attrText.setId("Textfield " + 1+attributes.size());
            setAttributeContextMenu(attrText);
        }
        vbox.getChildren().addAll(classTitle, separator);
        for (TextField attr : attributes) {
            vbox.getChildren().add(attr);
        }
        methodSeparator.setMaxWidth(rectangle.getWidth() - 2);
        vbox.getChildren().add(methodSeparator);
        // process Operations
        methods.clear();
        for (UMLOperation op : cl.getOperations()) {
            TextField methodText = new TextField(op.getName());
            methodText.setMaxWidth(rectangle.getWidth() - 20);
            methodText.textProperty().addListener((observableValue, oldValue, newValue) -> {
                op.setName(newValue);
                if(!op.hasModifier()){
                    methodText.setStyle("-fx-text-inner-color: red");
                    Tooltip tooltip = new Tooltip("Chybí modifikátor přístupu");
                    methodText.setTooltip(tooltip);
                } else {
                    methodText.setStyle("-fx-text-inner-color: black");
                    methodText.setTooltip(null);
                }
                //System.out.println("textfield changed from " + oldValue + " to " + attr.getName());
            });
            methods.add(methodText);
            methodText.setId("methodTextField " + 1 + methods.size());
            setMethodContextMenu(methodText);
        }
        for (TextField mText : methods) {
            vbox.getChildren().add(mText);
        }
        this.getChildren().add(vbox);
        if (vbox.getHeight() > rectangle.getHeight()) {
            rectangle.setHeight(this.getHeight()+15);
        }
    }

    public String getClassName(){
        return this.cl.getName();
    }

    public UMLClass getUMLClass(){
        return this.cl;
    }

    public TextField getClassTitle() {
        return classTitle;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setClName(String name) {
        cl.setName(name);
        classTitle.setText(name);
    }
}