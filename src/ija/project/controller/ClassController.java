package ija.project.controller;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
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

    public void addAttribute(ActionEvent event) {
    }

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
    private double x;
    private double y;
    private double tmp_x;
    private double tmp_y;
    private int number = 1;
    private int numberToDelete = 0;
    private ClassBox selected = null;

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

    public void addClass(ActionEvent event){
        System.out.println("Calling addClass");
        ClassBox rectangle = new ClassBox(number++);
        //rectangle.relocate(x+=100,y+=0);
        //rectangle.setId(Integer.toString(number++));
        //rectangle.setOnMouseClicked(this::classClick);
        //rectangle.setOnMouseDragged(this::classMove);
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
            node.setCursor(Cursor.MOVE);

            //When a press event occurs, the location coordinates of the event are cached
            pos.x = event.getX();
            pos.y = event.getY();
        });
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> node.setCursor(Cursor.DEFAULT));

        //Realize drag and drop function
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
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
        });
    }


    /*public void Delete(ActionEvent event){
        String retez = "#";
        retez = retez.concat(Integer.toString(numberToDelete));
        panel.getChildren().remove(panel.lookup(retez));
        numberToDelete++;
    }*/
    //Ovládání toggle tlačítka select
    public void changeToSelect(ActionEvent event){
        System.out.println("Calling changeToSelect");
        textMode.setText("Mode: Select");
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
        textMode.setText("Mode: Delete");
        if(mouseMode == Mode.delete){
            mouseMode = Mode.select;
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
        textMode.setText("Mode: Connect");
        if(mouseMode == Mode.connect){
            mouseMode = Mode.select;
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