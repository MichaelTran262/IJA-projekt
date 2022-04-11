package ija.project.controller;

import java.io.IOException;
import java.util.ArrayList;

import ija.project.model.ClassDiagram;
import ija.project.model.UMLAttribute;
import ija.project.model.UMLClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.fxml.FXML;

public class ClassController {
    public ToggleButton deleteButton;
    public ToggleButton connectButton;
    public ToggleButton selectButton;
    @FXML
    public Text coordinatesText;
    public Text boxCoordinates;
    public Text textMode;
    public TextField formNameField;
    public TextField formTypeField;


    private enum Mode{
        select, connect, delete;
    }
    @FXML
    private Mode mouseMode = Mode.select;
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private AnchorPane anchorPane;
    private ArrayList<ClassBox> seznam = new ArrayList<ClassBox>();
    private ArrayList<Line> connections = new ArrayList<Line>();
    private ClassDiagram diagram = new ClassDiagram("Diagram");
    private double x;
    private double y;
    private int number = 1;
    private int numberToDelete = 0;
    private ClassBox selected = null;
    private UMLClass curr_class;

    private static class Position {
        double x;
        double y;
    }
    /*TODO Scene change
    public void switchToScene2(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("SequenceDiagram.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }*/

    public void addAttribute(ActionEvent event) {
        System.out.println("Add attribute " + formNameField.getCharacters().toString() + "Typu: " + formTypeField.getCharacters().toString() + " Do tridy " + curr_class.getName());
        //addClassAttribute(event,);
    }

    public void openAttributeWindow(ClassBox box) {
        curr_class = box.getUMLClass();
        System.out.println("Jdem přidat atribut do třídy " + curr_class.getName());
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("addAttributeWindow.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Přidat atribut");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            // Hide this current window (if this is what you want)
            //((Node)(event.getSource())).getScene().getWindow().hide();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addClassAttribute(ActionEvent event, Stage stage, UMLClass cl) {

    }

    public void addClass(ActionEvent event){
        System.out.println("Calling addClass");
        // Create class to model
        UMLClass new_class = diagram.createClass("Title " + number++);
        boolean made = (new_class.addAttribute(new UMLAttribute("jméno")));
        // Creating GUI
        ClassBox rectangle = new ClassBox(new_class);
        draggable(rectangle);
        connectable(rectangle);
        rectangle.toFront();
        anchorPane.getChildren().add(rectangle);
        seznam.add(rectangle);
    }

    private void draggable(Node node) {
        final Position pos = new Position();

        //Prompt the user that the node can be clicked
        node.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> node.setCursor(Cursor.HAND));
        node.addEventHandler(MouseEvent.MOUSE_EXITED, event -> node.setCursor(Cursor.DEFAULT));

        //Prompt the user that the node can be dragged
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (mouseMode == Mode.select) {
                    node.setCursor(Cursor.MOVE);
                    //When a press event occurs, the location coordinates of the event are cached
                    pos.x = event.getX();
                    pos.y = event.getY();
                }

            } else if (event.getButton() == MouseButton.SECONDARY){
                System.out.println("RIGHT CLICK");
                ContextMenu contextMenu = new ContextMenu();
                /*
                MenuItem menuItem1 = new MenuItem("Změnit název");
                editable(menuItem1);
                MenuItem menuItem2 = new MenuItem("Změnit atributy");
                editable(menuItem2);*/
                MenuItem menuItem3 = new MenuItem("Přidat atribut");
                menuItem3.setOnAction(actionEvent-> {
                    openAttributeWindow((ClassBox)event.getSource());
                });
                // TODO: MenuItem1, MenuItem2
                contextMenu.getItems().addAll(menuItem3);
                node.setOnContextMenuRequested( contextEvent -> {
                    contextMenu.show(node, contextEvent.getScreenX(), contextEvent.getScreenY());
                });

            }
        });

        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> node.setCursor(Cursor.DEFAULT));

        //Realize drag and drop function
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (mouseMode == Mode.select) {
                double distanceX = event.getX() - pos.x;
                double distanceY = event.getY() - pos.y;


                double new_x = node.getLayoutX() + distanceX;
                double new_y = node.getLayoutY() + distanceY;
                boxCoordinates.setText("Selected box: X = " + new_x + ", Y = " + new_y);
                //After calculating X and y, relocate the node to the specified coordinate point (x, y)
                if (new_x < 0 && new_y < 0) {
                    node.relocate(0, 0);
                } else if (new_y < 0) {
                    node.relocate(new_x, 0);
                } else if (new_x < 0) {
                    node.relocate(0, new_y);
                } else {
                    node.relocate(new_x, new_y);
                }
            }
        });
    }
    /*
    public void editable(MenuItem menuItem) {
        menuItem.setOnAction(actionEvent-> {
            Parent root;
            try {
                String text = menuItem.getText();
                switch (text) {
                    case "Změnit název":
                        root = FXMLLoader.load(getClass().getClassLoader().getResource("editNameWindow.fxml"));
                        break;
                    case "Změnit atributy":
                        root = FXMLLoader.load(getClass().getClassLoader().getResource("editAttributesWindow.fxml"));
                        break;
                }
                Stage stage = new Stage();
                stage.setTitle("Edit attribut");
                stage.setScene(new Scene(root, 450, 450));
                stage.show();
                // Hide this current window (if this is what you want)
                //((Node)(event.getSource())).getScene().getWindow().hide();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
    }*/


    /*public void Delete(ActionEvent event){
        String retez = "#";
        retez = retez.concat(Integer.toString(numberToDelete));
        panel.getChildren().remove(panel.lookup(retez));
        numberToDelete++;
    }*/
    //Ovládání toggle tlačítka select
    public void changeToSelect(ActionEvent event){
        System.out.println("Calling changeToSelect");
        textMode.setText("Mode:\n Select");
        selected = null;
        if(mouseMode == Mode.select){
            selectButton.setSelected(true);
        }
        mouseMode = Mode.select;
        deleteButton.setSelected(false);
        connectButton.setSelected(false);
    }
    //Ovládání toggle tlačítka delete
    public void changeToDelete(ActionEvent event){
        System.out.println("Calling changeDelete");
        textMode.setText("Mode:\n Delete");
        if(mouseMode == Mode.delete){
            mouseMode = Mode.select;
            textMode.setText("Mode:\n Select");
            deleteButton.setSelected(false);
            selectButton.setSelected(true);
        }
        else {
            mouseMode = Mode.delete;
            selectButton.setSelected(false);
            connectButton.setSelected(false);
        }
    }
    //Ovládání toggle tlačítka connect
    public void changeToConnect(ActionEvent event){
        System.out.println("Calling changeToConnect");
        textMode.setText("Mode:\n Connect");
        if(mouseMode == Mode.connect){
            mouseMode = Mode.select;
            textMode.setText("Mode:\n Select");
            connectButton.setSelected(false);
            selectButton.setSelected(true);
        }
        else {
            mouseMode = Mode.connect;
            deleteButton.setSelected(false);
            selectButton.setSelected(false);
        }
    }
    //Ovládání objektu třídy při jednoduchém mouseClicku
    private void connectable(Node node) {
        node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            node.setCursor(Cursor.HAND);
            switch (mouseMode){
                case select:
                    break;
                case delete:
                    ClassBox to_remove = (ClassBox)event.getSource();
                    for(Connection conn : to_remove.getConnections()) {
                        anchorPane.getChildren().remove(conn);
                    }
                    anchorPane.getChildren().remove(node);
                    seznam.remove(node.getId());

                    break;
                case connect:
                    if (selected == null) {
                        System.out.println("Connection select 1");
                        selected = (ClassBox)event.getSource();
                        selected.setStyle("-fx-border-color: green");
                    } else {
                        System.out.println("Connection select 2");
                        ClassBox start = selected;
                        ClassBox end = (ClassBox)event.getSource();
                        Connection connect = new Connection(start, end);
                        anchorPane.getChildren().add(connect);
                        start.appendConnection(connect);
                        end.appendConnection(connect);
                        start.toFront();
                        end.toFront();
                        connections.add(connect);
                        connectButton.setSelected(false);
                        selectButton.setSelected(true);
                        selected.setStyle("-fx-border-style: none");
                        selected = null;
                    }
            }
        });
    }

    private boolean inTheWindow(ClassBox box) {
        return anchorPane.getLayoutBounds().contains(box.getBoundsInParent());
    }

    @FXML
    private void setCoordinatesText(MouseEvent event){
        coordinatesText.setText("Mouse: X = " + event.getX() + ", Y = " + event.getY());
    }
}