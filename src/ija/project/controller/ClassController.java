package ija.project.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ija.project.model.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
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
    public static Button undoButton;
    public Button redoButton;
    private int currentPane = -1;
    private ArrayList<Integer> numberOfObjects = new ArrayList<Integer>();


    private enum Mode{
        select, connect, delete
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
    private static ClassDiagram diagram = new ClassDiagram("Diagram");
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

    private static Stack<Action> history = new Stack<>();
    private static Stack<Action> undoHistory = new Stack<>();

    interface Action{
        void run();
        void undo();
        void redo();
    }

    class AddClass implements Action{
        AnchorPane root;
        ClassDiagram clsDiagram;
        ArrayList<ClassBox> clsList;

        ClassBox add;

        public AddClass(AnchorPane root, ClassDiagram clsDiagram, ArrayList<ClassBox> clsList) {
            this.root = root;
            this.clsDiagram = clsDiagram;
            this.clsList = clsList;
        }

        @Override
        public void run() {
            UMLClass clsAdd = diagram.createClass("Title " + number);
            clsAdd.addAttribute(new UMLAttribute("jméno"));
            clsAdd.addAttribute(new UMLAttribute("mail"));
            // Creating GUI
            add = new ClassBox(clsAdd);
            TextField textField = add.getClassTitle();
            draggable(add);
            connectable(add);
            add.toFront();
            add.relocate(120*number, 10*number);
            anchorPane.getChildren().add(add);
            seznam.add(add);
            number++;
        }

        @Override
        public void undo() {
            root.getChildren().remove(add);
            clsDiagram.removeClass(add.getUMLClass());
            number--;
        }

        @Override
        public void redo() {
            root.getChildren().add(add);
            clsDiagram.addClass(add.getUMLClass());
            number++;
        }
    }

    class DeleteClass implements Action{
        AnchorPane root;
        ClassDiagram clsDiagram;
        ArrayList<ClassBox> clsList;
        ArrayList<Connection> conList;
        ClassBox remove;

        UMLClass clsAdd;

        public DeleteClass(AnchorPane root, ClassDiagram clsDiagram, ArrayList<ClassBox> clsList, ArrayList<Connection> connections, ClassBox remove) {
            this.root = root;
            this.clsDiagram = clsDiagram;
            this.clsList = clsList;
            this.conList = connections;
            this.remove = remove;
        }

        @Override
        public void run() {
            for(Connection conn : remove.getConnections()) {
                root.getChildren().remove(conn.getArrowHead());
                root.getChildren().remove(conn);
                conList.remove(conn);
            }
            diagram.removeClass(remove.getUMLClass());
            root.getChildren().remove(remove);
            clsList.remove(remove);
        }

        @Override
        public void undo() {
            for (Connection conn: remove.getConnections()) {
                root.getChildren().add(conn.getArrowHead());
                root.getChildren().add(conn);
                conList.add(conn);
            }
            diagram.addClass(remove.getUMLClass());
            root.getChildren().add(remove);
            clsList.add(remove);
        }

        @Override
        public void redo() {
            for (Connection conn: remove.getConnections()) {
                root.getChildren().add(conn.getArrowHead());
                root.getChildren().remove(conn);
                conList.remove(conn);
            }
            diagram.removeClass(remove.getUMLClass());
            root.getChildren().remove(remove);
            clsList.remove(remove);
        }
    }

    class ConnectClasses implements Action{
        AnchorPane root;
        ArrayList<Connection> conList;
        ClassBox from;
        ClassBox to;

        Connection connect;

        public ConnectClasses(AnchorPane root,  ArrayList<Connection> conList, ClassBox from, ClassBox to) {
            this.root = root;
            this.conList = conList;
            this.from = from;
            this.to = to;
        }

        @Override
        public void run(){
            connect = new Connection(from, to);
            root.getChildren().add(connect);
            // šipka
            root.getChildren().add(connect.getArrowHead());
            from.appendConnection(connect);
            to.appendConnection(connect);
            from.toFront();
            //to.toFront();
            conList.add(connect);
            connectButton.setSelected(false);
            selectButton.setSelected(true);
            selected.setStyle("-fx-border-style: none");
            selected = null;
        }

        @Override
        public void undo() {
            from.deleteConnection(connect);
            to.deleteConnection(connect);
            root.getChildren().remove(connect);
            root.getChildren().remove(connect.getArrowHead());
            conList.remove(connect);
        }

        @Override
        public void redo() {
            from.appendConnection(connect);
            to.appendConnection(connect);
            root.getChildren().add(connect);
            root.getChildren().add(connect.getArrowHead());
            conList.add(connect);
        }
    }
/*
    class EditClass implements Action{
        ClassBox box;
        String valueNew;
        String valueOld;

        public EditClass(ClassBox box, KeyEvent event) {
            System.out.println(box.getUMLClass().getName() + "\n");
            this.box = box;
            this.valueOld = box.getClassTitle().getText();
            if(isWriteable(event.getCode()))
                this.valueNew = valueOld + event.getCharacter();
            else {
                this.valueNew = valueOld;
                if (event.getCode() == KeyCode.valueOf("BACK_SPACE")) {
                    StringBuffer sb = new StringBuffer(valueNew);
                    sb.deleteCharAt(sb.length()-1);
                }
            }
        }

        private boolean isWriteable(KeyCode code){
            if(code.getName() == "SPACE")
                return true;
            return code.isDigitKey() || code.isLetterKey();
        }

        @Override
        public void run() {
            System.out.println("old = " + valueOld + "\n new = " + valueNew + "\n");
            box.getUMLClass().setName(valueNew);
            box.update();
        }

        @Override
        public void undo() {
            box.getUMLClass().setName(valueOld);
            box.update();
        }

        @Override
        public void redo() {
            run();
        }
    }
*/
    private static void execute(Action action){
        history.add(action);
        action.run();
        undoButton.setDisable(false);
    }

    @FXML
    private void undo(){
        Action undo = history.pop();
        undo.undo();
        undoHistory.add(undo);
        redoButton.setDisable(false);
        if(history.isEmpty())
            undoButton.setDisable(true);
    }

    @FXML
    private void redo(){
        Action redo = undoHistory.pop();
        redo.redo();
        history.add(redo);
        undoButton.setDisable(false);
        if(undoHistory.isEmpty())
            redoButton.setDisable(true);
    }

    /**
     * Funkce přidá GUI instanci třídy Classbox do diagramu tříd
     */
    public void addClass(){
        try {
            execute(new AddClass(anchorPane, diagram, seznam));
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
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
    public void changeToSelect(){
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
    public void changeToDelete(){
        textMode.setText("Mode:\n Delete");
        if(mouseMode == Mode.delete){
            changeToSelect();
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
    public void changeToConnect(){
        textMode.setText("Mode:\n Connect");
        if(mouseMode == Mode.connect){
            changeToSelect();
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
                    execute(new DeleteClass(anchorPane, diagram, seznam, connections, to_remove));
                    break;
                case connect:
                    if (selected == null) {
                        selected = (ClassBox)event.getSource();
                        selected.setStyle("-fx-border-color: green");
                    } else {
                        ClassBox start = selected;
                        ClassBox end = (ClassBox)event.getSource();
                        execute(new ConnectClasses(anchorPane,connections,start,end));
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
    private void loadFile() throws IOException {
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
                diagram = fileHandler.parseClassDiagram();
                for (UMLClass cl : diagram.getClassesList()) {
                    // Creating GUI
                    ClassBox rectangle = new ClassBox(cl);
                    draggable(rectangle);
                    connectable(rectangle);
                    seznam.add(rectangle);
                    anchorPane.getChildren().add(rectangle);
                    rectangle.toFront();
                    rectangle.relocate(x, y);
                    x += 200;
                    y += 10;
                }
                for (Connection conn : fileHandler.parseConnections(seznam)){
                    anchorPane.getChildren().add(conn);
                    connections.add(conn);
                    conn.toBack();
                }
                sequenceDiagrams = fileHandler.parseSequence(diagram);
            }
            catch (Exception e){
                System.out.println("vadnej load " + e.toString() + "\n");
                e.printStackTrace();
            }
        }
        else{
                System.out.println("Not a valid file");
        }
    }

    @FXML
    private void saveToFile() {
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

    public void sequenceAddClass() {/*TODO
        UMLClass newClass = sequenceDiagrams.get(currentPane).createClass("new class");
        int numberObjects = numberOfObjects.get(currentPane);
        numberObjects++;
        System.out.println(numberObjects);
        sequencePanes.get(currentPane).getChildren().addAll(createObjects("xd",50+6*100));
        Text actor = new Text(newClass.getName());
        numberOfObjects.remove(currentPane);
        numberOfObjects.add(currentPane,numberObjects);
*/
    }

    public void setSequencePanes(){
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
                int numberObjects = 0;
                for (UMLClass cl:classes) {
                    numberObjects++;
                    pane.getChildren().addAll(createObjects(cl.getName(),50+numberObjects*100));
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
                numberOfObjects.add(numberObjects);
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

    public void nextDiagram(){
        List<Node> clone = new ArrayList<>();
        if(sequencePanes.size() == 1)
            return;
        if(currentPane == sequencePanes.size()-1){
            currentPane = 0;
        }
        else {
            clone.addAll(sequenceAnchorPane.getChildren());
            sequencePanes.get(currentPane).getChildren().clear();
            sequencePanes.get(currentPane).getChildren().addAll(clone);
            currentPane++;
        }
        sequenceAnchorPane.getChildren().clear();
        nameOfDiagram.setText(sequenceDiagrams.get(currentPane).getName());
        sequenceAnchorPane.getChildren().addAll(sequencePanes.get(currentPane).getChildrenUnmodifiable());
    }

    public void previousDiagram(){
        List<Node> clone = new ArrayList<>();
        if(sequencePanes.size() == 1)
            return;
        if(currentPane == 0){
            currentPane = sequencePanes.size()-1;
        }
        else {
            clone.addAll(sequenceAnchorPane.getChildren());
            sequencePanes.get(currentPane).getChildren().clear();
            sequencePanes.get(currentPane).getChildren().addAll(clone);
            currentPane--;
        }
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
        int X = 65+poradi*100;
        int Ye = 135+(from-1)*60;
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