package ija.project.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ija.project.model.ClassBox;
import ija.project.model.Connection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.fxml.FXML;

public class ClassController {
    public ToggleButton deleteButton;
    public ToggleButton connectButton;
    public ToggleButton selectButton;

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
    private List<ClassBox> seznam = new ArrayList<ClassBox>();
    private List<Line> connections = new ArrayList<Line>();
    private double x;
    private double y;
    private int number = 1;
    private int numberToDelete = 0;
    private ClassBox selected = null;

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
        StackPane stp = new StackPane();
        ClassBox rectangle = new ClassBox(40,80);
        rectangle.relocate(x+=20,y+=30);
        rectangle.setId(Integer.toString(number++));
        rectangle.setOnMouseClicked(this::classClick);
        //circle.setOnMousePressed(event1 -> {classPress(event1);});
        rectangle.setOnMouseDragged(this::classMove);
        rectangle.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2;");
        //Class Name
        anchorPane.getChildren().add(rectangle);
        seznam.add(rectangle);
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
    public void classClick(MouseEvent event){
        System.out.println("Calling classClick");
        switch(mouseMode){
            case select:
                break;
            case delete:
                anchorPane.getChildren().remove(event.getTarget());
                seznam.remove(((ClassBox)event.getTarget()).getId());
                break;
            case connect: if(selected == null) {
                selected = (ClassBox) event.getTarget();
            }
            else {
                Connection connect = new Connection(selected,(ClassBox)event.getTarget());
                anchorPane.getChildren().add(connect);
                connections.add(connect);
                selected.addStart(connect);
                ((ClassBox)event.getTarget()).addEnd(connect);
                mouseMode = Mode.select;
                connectButton.setSelected(false);
                selectButton.setSelected(true);
                selected = null;
            }
        }
    }
    /*
    public void classPress(MouseEvent event){
        x = event.getSceneX();
        y = event.getSceneY();
        newx = ((ClassBox)(event.getSource())).getTranslateX();
        newy = ((ClassBox)(event.getSource())).getTranslateY();
    }*/
    //Ovládání drag-move objektem třídy
    public void classMove(MouseEvent event){
        System.out.println("Calling classMove");
        ClassBox movingBox = ((ClassBox)(event.getSource()));
        if (inTheWindow((ClassBox)(event.getSource())))  {
            System.out.println("Box is in the anchorPane");
            double offsetX = event.getX() - x;
            double offsetY = event.getY() - y;
            System.out.println(offsetX + ", " + offsetY);
            movingBox.setX(offsetX);
            movingBox.setY(offsetY);
        } else {
            System.out.println("Box is outside the anchorPane");
            double offsetX = event.getX() - x;
            double offsetY = event.getY() - y;
            movingBox.setX(offsetX);
            movingBox.setY(offsetY);
        }
        //-System.out.println("classMove position" + newx + ", " + newy);
    }

    private boolean inTheWindow(ClassBox box) {
        return anchorPane.getLayoutBounds().contains(box.getBoundsInParent());
    }
}