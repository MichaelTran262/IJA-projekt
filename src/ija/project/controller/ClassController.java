package ija.project.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ija.project.model.*;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author      Lukáš Fuis      <xfuisl00 @ stud.fit.vutbr.cz>
 * @version     0.5
 */

/**
 *  Třída reprezentující GUI diagramu tříd
 */
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
    public MenuItem saveFileItem;
    public MenuItem loadFileItem;
    public AnchorPane classDiagramWindow;
    public Button sequenceButton;
    public Button classButton;
    public Button addButton;
    public AnchorPane sequenceDiagramWindow;
    public AnchorPane sequenceAnchorPane;
    public Label nameOfDiagram;
    public Button showButton;
    private int currentPane = -1;


    private enum Mode{
        select, connect, delete;
    }
    @FXML
    private static String activeScene;
    private Mode mouseMode = Mode.select;
    private static Scene classScene;
    private static ArrayList<AnchorPane> sequencePanes = new ArrayList<AnchorPane>();
    private static Scene sequenceScene;
    @FXML
    private AnchorPane anchorPane;
    private static ArrayList<ClassBox> seznam = new ArrayList<ClassBox>();
    private static ArrayList<Connection> connections = new ArrayList<Connection>();
    private static final ClassDiagram diagram = new ClassDiagram("Diagram");
    private static List<SequenceDiagram> sequenceDiagrams;
    private int number = 1;
    private ClassBox selected = null;
    private static FileHandler fileHandler = new FileHandler(null);

    private double x = 0;
    private double y = 0;
    private static class Position {
        double x;
        double y;
    }

    /**
     * Funkce přidá GUI instanci třídy Classbox do diagramu tříd
     */
    public void addClass(ActionEvent event){
        // Create class to model
        UMLClass new_class = diagram.createClass("Title " + number++);
        boolean made = (new_class.addAttribute(new UMLAttribute("jméno")));
        made = (new_class.addAttribute(new UMLAttribute("mail")));
        // Creating GUI
        ClassBox rectangle = new ClassBox(new_class);
        draggable(rectangle);
        connectable(rectangle);
        rectangle.toFront();
        rectangle.relocate(x, y);
        anchorPane.getChildren().add(rectangle);
        x += 120;
        y += 10;
        seznam.add(rectangle);
    }

    /**
     * Funkce přidává vlastnoti interakce dané GUI komponenty
     * @param node GUI komponenta, ke které se přidají vlastnosti interakce
     */
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

    /**
     * Funkce změní uživatelský mod na Select
     * @param event JavaFX ActionEvent
     */
    public void changeToSelect(ActionEvent event){
        textMode.setText("Mode:\n Select");
        selected = null;
        if(mouseMode == Mode.select){
            selectButton.setSelected(true);
        }
        mouseMode = Mode.select;
        deleteButton.setSelected(false);
        connectButton.setSelected(false);
    }

    /**
     * Funkce změní uživatelský mod na Delete
     * @param event JavaFX ActionEvent
     */
    public void changeToDelete(ActionEvent event){
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

    /**
     * Funkce změní uživatelský mod na Connect
     * @param event JavaFX ActionEvent
     */
    public void changeToConnect(ActionEvent event){
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

    /**
     * Funkce přidá dané komponentě vlastnost mít vazby
     * @param node komponenta, která získá schopnost mít vazbu
     */
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
                        selected = (ClassBox)event.getSource();
                        selected.setStyle("-fx-border-color: green");
                    } else {
                        ClassBox start = selected;
                        ClassBox end = (ClassBox)event.getSource();
                        Connection connect = new Connection(start, end);
                        //draggable(connect.getArrowHead());
                        anchorPane.getChildren().add(connect);
                        // šipka
                        anchorPane.getChildren().add(connect.getArrowHead());
                        start.appendConnection(connect);
                        end.appendConnection(connect);
                        //start.toFront();
                        //end.toFront();
                        connections.add(connect);
                        connectButton.setSelected(false);
                        selectButton.setSelected(true);
                        selected.setStyle("-fx-border-style: none");
                        selected = null;
                    }
            }
        });
    }

    /**
     * Funkce vypisuje souřadice kurzoru v pracovním prostoru diagramu tříd
     * @param event JavaFX MouseEvent
     */
    @FXML
    private void setCoordinatesText(MouseEvent event){
        coordinatesText.setText("Mouse: X = " + event.getX() + ", Y = " + event.getY());
    }

    @FXML
    private void loadFile(ActionEvent event) throws IOException {
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(null);
        selected = null;
        mouseMode = Mode.select;
        deleteButton.setSelected(false);
        selectButton.setSelected(true);
        connectButton.setSelected(false);
        x = 20;
        y = 10;
        if (selectedFile != null) {
            try {
                sequenceScene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("Sequence.fxml")));
                String css = this.getClass().getClassLoader().getResource("application.css").toExternalForm();
                sequenceScene.getStylesheets().add(css);
                seznam.clear();
                connections.clear();
                anchorPane.getChildren().clear();
                fileHandler.setFile(selectedFile);
                sequenceDiagrams = fileHandler.parseSequence();
                anchorPane.getChildren().addAll(fileHandler.parseFile());
                for (Node node : anchorPane.getChildren()) {
                    if (node instanceof ClassBox) {
                        draggable(node);
                        connectable(node);
                        node.relocate(x, y);
                        x += 200;
                        y += 10;
                        seznam.add((ClassBox) node);
                    } else if (node instanceof Connection) {
                        node.toBack();
                        connections.add((Connection) node);
                    }
                }
            }
            catch (Exception e){
                System.out.println("vadnej load " + e.toString() + "\n");
            }
        }
             else{
                System.out.println("Not a valid file");
            }
        }

    @FXML
    private void saveToFile(ActionEvent event) {
        FileChooser fc = new FileChooser();
        File file = fc.showSaveDialog(new Stage());
        if(file != null) {
            JSONObject json = new JSONObject();
            JSONArray classes = new JSONArray();
            for (ClassBox cl : seznam) {
                JSONObject tmp = new JSONObject();
                tmp.put("name", cl.getClassName());
                List<UMLAttribute> attributes = cl.getClassAttributes();
                JSONArray jsonAttributeArray = new JSONArray();
                for (UMLAttribute attr : attributes) {
                    jsonAttributeArray.put(attr.getName());
                }
                tmp.put("attributes", jsonAttributeArray);
                classes.put(tmp);
            }
            json.put("classes", classes);
            JSONArray jsonConnections = new JSONArray();
            for (Connection conn : connections) {
                String connectionString = conn.getStart().getClassName() + "--" + conn.getEnd().getClassName();
                jsonConnections.put(connectionString);
            }
            json.put("connections", jsonConnections);
            String toFileString = json.toString();
            System.out.println(toFileString);
            try {
                PrintWriter printWriter = new PrintWriter(file);
                printWriter.write(toFileString);
                printWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();;
            }
        }
    }
    public void switchToSequence(ActionEvent event) throws IOException {
        Scene currScene = ((Node) event.getSource()).getScene();
        Stage thisStage = (Stage) currScene.getWindow();
        if(sequenceScene == null) {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Sequence.fxml"));
            sequenceScene = new Scene(root);
            String css = this.getClass().getClassLoader().getResource("application.css").toExternalForm();
            sequenceScene.getStylesheets().add(css);
        }
        classScene = currScene;
        thisStage.setScene(sequenceScene);
        activeScene = "Sequence";
    }


    public void switchToClass(ActionEvent event) throws IOException {
        Scene currScene = ((Node) event.getSource()).getScene();
        Stage thisStage = (Stage) currScene.getWindow();
        if(classScene == null) {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Main.fxml"));
            classScene = new Scene(root);
            String css = this.getClass().getClassLoader().getResource("application.css").toExternalForm();
            classScene.getStylesheets().add(css);
        }
        sequenceScene = currScene;
        thisStage.setScene(classScene);
        activeScene = "Class";
    }

    public void sequenceAddClass(ActionEvent event) {
        Operation operace = new Operation(1,2,1,1, "Čus hnus");
        Operation operace2 = new Operation(3,2,2,4, "ez");
        sequenceAnchorPane.getChildren().addAll(operace, operace.getUp(), operace.getDown(), operace.getName(),operace2,operace2.getDown(),operace2.getUp(), operace2.getName());
    }

    public void setSequencePanes(){
            sequenceDiagrams.add(new SequenceDiagram("pog"));
            sequenceDiagrams.add(new SequenceDiagram("xd"));
            int i = 0;
            for (SequenceDiagram diagram:sequenceDiagrams) {
                AnchorPane pane = new AnchorPane();
                Label nameDiagram = new Label(diagram.getName());
                nameDiagram.setId("nameOfDiagram");
                nameDiagram.setLayoutX(238);
                nameDiagram.setLayoutY(14);
                nameDiagram.setFont(new Font(26));
                pane.getChildren().add(nameDiagram);
                List<UMLClass> classes = diagram.getClasses();
                int x = 50;
                int max = 0;
                int min = 1000;
                int j = 1;
                List<Rectangle> rectangles = new ArrayList<>();
                for (UMLClass cl:classes) {
                    pane.getChildren().addAll(createObjects(cl.getName(),x+=100));
                    ArrayList<Integer> from = cl.getActiveFrom();
                    ArrayList<Integer> to = cl.getActiveTo();
                    for(int l = 0; l<from.size();l++){
                        if (to.get(l) > max)
                            max = to.get(l);
                        if(from.get(l) < min)
                            min = from.get(l);
                        Rectangle rectangle = createActivity(j, from.get(l),to.get(l));
                        rectangles.add(rectangle);
                    }
                    j++;
                }
                pane.getChildren().addAll(createLines(max,classes.size()));
                pane.getChildren().addAll(rectangles);
                pane.getChildren().addAll(createActivity(0,min, max));
                List<UMLConnection> spojeni = diagram.getConnections();
                int k = 0;
                for (UMLConnection con : spojeni) {
                    int from = diagram.getOrder(con.getFrom());
                    int to = diagram.getOrder(con.getTo());
                    Operation operation = new Operation(from,to,con.getType(),k,con.getName());
                    k++;
                    pane.getChildren().addAll(operation, operation.getName(), operation.getUp(), operation.getDown());
                    operation.toBack();
                }
                pane.getChildren().addAll(createActor());
                pane.setId(Integer.toString(i));
                sequencePanes.add(pane);
                i++;
            }
            currentPane = 0;
            nameOfDiagram.setText("");
            sequenceAnchorPane.getChildren().addAll(sequencePanes.get(currentPane).getChildrenUnmodifiable());
            showButton.setDisable(true);
    }

    public void nextDiagram(ActionEvent e){
        List<Node> clone = new ArrayList<>();
        clone.addAll(sequenceAnchorPane.getChildren());
        sequencePanes.get(currentPane).getChildren().clear();
        sequencePanes.get(currentPane).getChildren().addAll(clone);
        if(sequencePanes.size() == 1)
            return;
        if(currentPane == sequencePanes.size()-1){
            currentPane = 0;
        }
        else
            currentPane++;
        sequenceAnchorPane.getChildren().clear();
        nameOfDiagram.setText(sequenceDiagrams.get(currentPane).getName());
        sequenceAnchorPane.getChildren().addAll(sequencePanes.get(currentPane).getChildrenUnmodifiable());
    }

    public void previousDiagram(ActionEvent e){
        List<Node> clone = new ArrayList<>();
        clone.addAll(sequenceAnchorPane.getChildren());
        sequencePanes.get(currentPane).getChildren().clear();
        sequencePanes.get(currentPane).getChildren().addAll(clone);
        if(sequencePanes.size() == 1)
            return;
        if(currentPane == 0){
            currentPane = sequencePanes.size()-1;
        }
        else
            currentPane--;
        sequenceAnchorPane.getChildren().clear();
        nameOfDiagram.setText(sequenceDiagrams.get(currentPane).getName());
        sequenceAnchorPane.getChildren().addAll(sequencePanes.get(currentPane).getChildrenUnmodifiable());
    }

    public List<Node> createActor(){
        List<Node> list = new ArrayList<Node>();
        Circle head = new Circle(7.5);
        head.setCenterX(70);
        head.setCenterY(90);
        head.setFill(Color.WHITE);
        head.setStroke(Color.BLACK);
        list.add(head);
        Text actor = new Text("Actor");
        actor.setX(56);
        actor.setY(80);
        list.add(actor);
        Line body = new Line(70,97.5,70,110);
        list.add(body);
        Line arms = new Line(65,102,75,102);
        list.add(arms);
        Line left = new Line(70,110,65,115);
        list.add(left);
        Line right = new Line(70,110,75,115);
        list.add(right);
        return list;
    }

    public List<Node> createObjects(String name, int x){
        List<Node> list = new ArrayList<Node>();
        Text actor = new Text(name);
        actor.setX(x);
        actor.setY(80);
        list.add(actor);
        Rectangle box = new Rectangle();
        box.setX(x);
        box.setY(90);
        box.setHeight(25);
        box.setWidth(40);
        box.setFill(Color.WHITE);
        box.setStroke(Color.BLACK);
        list.add(box);
        return list;
    }

    public List<Node> createLines(int max, int count){
        List<Node> list = new ArrayList<Node>();
        int X = 70;
        int Ys = 130;
        int Ye = Ys+(max-1)*60+20;
        Line line = new Line(X,Ys,X,Ye);
        X += 100;
        line.toBack();
        list.add(line);
        for(int i = 0;i<count;i++){
            line = new Line(X,Ys,X,Ye);
            line.toBack();
            list.add(line);
            X += 100;
        }
        return list;
    }

    public Rectangle createActivity(int poradi, int from, int to){
        System.out.println("activity");
        System.out.println(poradi);
        System.out.println(from);
        System.out.println(to);
        int X = 65+poradi*100;
        int Ye = 135+(from-1)*60;
        System.out.println(Ye);
        Rectangle activity = new Rectangle();
        activity.setY(Ye);
        activity.setX(X);
        activity.setStroke(Color.BLACK);
        activity.setFill(Color.WHITE);
        activity.setHeight((to-from)*60);
        activity.setWidth(10);
        activity.toFront();
        return activity;
    }
}