package ija.project.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import ija.project.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
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
import javafx.scene.layout.VBox;
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
 *  Třída reprezentující GUI diagramu tříd
 * @author      Lukáš Fuis      xfuisl00
 * @author Thanh Q. Tran     xtrant02
 * @version     1.0
 */
public class ClassController {
    public ToggleButton deleteButton;

    public ToggleButton connectLineButton;

    public ToggleButton connectDependencyButton;

    public ToggleButton connectCompositionButton;

    public ToggleButton connectAggregationButton;

    public ToggleButton connectInheritanceButton;

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
    public Button undoButton;
    public Button redoButton;
    private int currentPane = -1;
    private ArrayList<Integer> numberOfObjects = new ArrayList<Integer>();
    private ArrayList<List<UMLClass>> sequenceClasses = new ArrayList<>();

    private enum Mode{
        select, connect, delete
    }

    private enum ArrowType{
        line, dependency, composition, inheritance, aggregation
    }

    ArrowType arrowType;
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

    Stack<Action> history = new Stack<>();
    Stack<Action> undoHistory = new Stack<>();

    /**
     * Třída reprezentující akci změny jména v diagramu
     * Implementuje rozhraní Action
     * @author Lukáš Fuis xfuisl00
     * @version 1.0
     */

    class EditName implements Action{
        String before;
        String after;
        ClassBox edit;

        public EditName(ClassBox box, String edit) {
            this.edit = box;
            before = this.edit.getClassName();
            after = edit;
        }

        @Override
        public void run() {
            edit.setClName(after);
        }

        @Override
        public void undo() {
            edit.setClName(before);
        }

        @Override
        public void redo() {
            run();
        }
    }

    /**
     * Třída reprezentující akci přidání nové třídy
     * Implementuje rozhraní Action
     * @author Lukáš Fuis xfuisl00
     * @version 1.0     *
     */
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
            clsAdd.addOperation(new UMLOperation("method()"));
            // Creating GUI
            add = new ClassBox(clsAdd);
            add.getClassTitle().textProperty().addListener((observable, oldValue, newValue) -> {
                //Dej sem execute a undo
                execute(new EditName(add,newValue));
            });
            draggable(add);
            connectable(add);
            add.toFront();
            add.relocate(120, 10);
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

    /**
     * Třída reprezentující akci smazání třídy
     * Implementuje rozhraní Action
     * @author Lukáš Fuis xfuisl00
     * @version 1.0     *
     */
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
                root.getChildren().remove(conn.getArrowHead());
                root.getChildren().remove(conn);
                conList.remove(conn);
            }
            diagram.removeClass(remove.getUMLClass());
            root.getChildren().remove(remove);
            clsList.remove(remove);
        }
    }

    /**
     * Třída reprezentující akci spojení tříd
     * Implementuje rozhraní Action
     * @author Lukáš Fuis xfuisl00
     * @version 1.0     *
     */
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
            if(arrowType != ArrowType.line) {
                System.out.println(arrowType.name());
                connect.createArrowHead(arrowType.name());
                root.getChildren().add(connect.getArrowHead());
            }
            from.appendConnection(connect);
            to.appendConnection(connect);
            from.toFront();
            to.toFront();
            conList.add(connect);
            connectLineButton.setSelected(false);
            connectDependencyButton.setSelected(false);
            connectCompositionButton.setSelected(false);
            connectAggregationButton.setSelected(false);
            connectInheritanceButton.setSelected(false);
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

    /**
     * Funkce vykonávající undoable akci
     * @author Lukáš Fuis xfuisl00
     */
    void execute(Action action){
        history.push(action);
        action.run();
        undoButton.setDisable(false);
        undoHistory.clear();
        redoButton.setDisable(true);
    }

    /**
     * Funkce provádějící akci undo
     * @author Lukáš Fuis xfuisl00
     */
    @FXML
    private void undo(){
        Action undo = history.pop();
        undo.undo();
        undoHistory.add(undo);
        redoButton.setDisable(false);
        if(history.isEmpty())
            undoButton.setDisable(true);
    }

    /**
     * Funkce provádějící akci redo
     * @author Lukáš Fuis xfuisl00
     */
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
     * @author Thanh Q. Tran     xtrant02
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
     */
    public void changeToSelect(){
        textMode.setText("Mode:\n Select");
        selected = null;
        if(mouseMode == Mode.select){
            selectButton.setSelected(true);
        }
        mouseMode = Mode.select;
        deleteButton.setSelected(false);
        connectLineButton.setSelected(false);
        connectDependencyButton.setSelected(false);
        connectCompositionButton.setSelected(false);
        connectAggregationButton.setSelected(false);
        connectInheritanceButton.setSelected(false);
    }

    /**
     * Funkce změní uživatelský mod na Delete
     */
    public void changeToDelete(){
        textMode.setText("Mode:\n Delete");
        if(mouseMode == Mode.delete){
            changeToSelect();
        }
        else {
            mouseMode = Mode.delete;
            selectButton.setSelected(false);
            connectLineButton.setSelected(false);
            connectDependencyButton.setSelected(false);
            connectCompositionButton.setSelected(false);
            connectAggregationButton.setSelected(false);
            connectInheritanceButton.setSelected(false);
        }
    }

    /**
     * Funkce změní uživatelský mod na line Connect
     * @author Thanh Q. Tran     xtrant02
     */
    public void changeToLineConnect(){
        arrowType = ArrowType.line;
        textMode.setText("Mode:\n Connect " + arrowType.name());
        if(mouseMode == Mode.connect){
            changeToSelect();
        }
        else {
            mouseMode = Mode.connect;
            deleteButton.setSelected(false);
            selectButton.setSelected(false);
            connectDependencyButton.setSelected(false);
            connectCompositionButton.setSelected(false);
            connectAggregationButton.setSelected(false);
            connectInheritanceButton.setSelected(false);
        }
    }

    /**
     * Funkce změní uživatelský mod na composition Connect
     * @author Thanh Q. Tran     xtrant02
     */
    public void changeToCompositionConnect(){
        arrowType = ArrowType.composition;
        textMode.setText("Mode:\n Connect " + arrowType.name());
        if(mouseMode == Mode.connect){
            changeToSelect();
        }
        else {
            mouseMode = Mode.connect;
            deleteButton.setSelected(false);
            selectButton.setSelected(false);
            connectLineButton.setSelected(false);
            connectDependencyButton.setSelected(false);
            connectAggregationButton.setSelected(false);
            connectInheritanceButton.setSelected(false);
        }
    }

    /**
     * Funkce změní uživatelský mod na aggregation Connect
     * @author Thanh Q. Tran     xtrant02
     */
    public void changeToAggregationConnect(){
        arrowType = ArrowType.aggregation;
        textMode.setText("Mode:\n Connect " + arrowType.name());
        if(mouseMode == Mode.connect){
            changeToSelect();
        }
        else {
            mouseMode = Mode.connect;
            deleteButton.setSelected(false);
            selectButton.setSelected(false);
            connectLineButton.setSelected(false);
            connectDependencyButton.setSelected(false);
            connectCompositionButton.setSelected(false);
            connectInheritanceButton.setSelected(false);
        }
    }

    /**
     * Funkce změní uživatelský mod na inheritance Connect
     * @author Thanh Q. Tran     xtrant02
     */
    public void changeToInheritanceConnect(){
        arrowType = ArrowType.inheritance;
        textMode.setText("Mode:\n Connect " + arrowType.name());
        if(mouseMode == Mode.connect){
            changeToSelect();
        }
        else {
            mouseMode = Mode.connect;
            deleteButton.setSelected(false);
            selectButton.setSelected(false);
            connectLineButton.setSelected(false);
            connectDependencyButton.setSelected(false);
            connectCompositionButton.setSelected(false);
            connectAggregationButton.setSelected(false);
        }
    }

    /**
     * Funkce změní uživatelský mod na dependency Connect
     * @author Thanh Q. Tran     xtrant02
     */
    public void changeToDependencyConnect(){
        arrowType = ArrowType.dependency;
        textMode.setText("Mode:\n Connect " + arrowType.name());
        if(mouseMode == Mode.connect){
            changeToSelect();
        }
        else {
            mouseMode = Mode.connect;
            deleteButton.setSelected(false);
            selectButton.setSelected(false);
            connectLineButton.setSelected(false);
            connectCompositionButton.setSelected(false);
            connectAggregationButton.setSelected(false);
            connectInheritanceButton.setSelected(false);
        }
    }


    /**
     * Funkce přidá dané komponentě vlastnost mít vazby
     * @param node komponenta, která získá schopnost mít vazbu
     * @author Thanh Q. Tran     xtrant02
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

    /**
     * Funkce načte ze souboru třídní a sekvenční diagramy
     * @author Thanh Q. Tran     xtrant02
     * @author      Lukáš Fuis      xfuisl00
     */
    @FXML
    private void loadFile() throws IOException {
        number = 1;
        anchorPane.getChildren().clear();
        seznam.clear();
        connections.clear();
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(null);
        selected = null;
        mouseMode = Mode.select;
        deleteButton.setSelected(false);
        selectButton.setSelected(true);
        connectLineButton.setSelected(false);
        connectDependencyButton.setSelected(false);
        connectCompositionButton.setSelected(false);
        connectAggregationButton.setSelected(false);
        connectInheritanceButton.setSelected(false);
        x = 20;
        y = 10;
        if (selectedFile != null) {
            try {
                history.clear();
                undoButton.setDisable(true);
                redoButton.setDisable(true);
                undoHistory.clear();
                sequenceScene = new Scene(FXMLLoader.load(getClass().getClassLoader().getResource("Sequence.fxml")));
                String css = this.getClass().getClassLoader().getResource("application.css").toExternalForm();
                sequenceScene.getStylesheets().add(css);
                sequenceClasses.add(new ArrayList<>());
                fileHandler.setFile(selectedFile);
                diagram = fileHandler.parseClassDiagram();
                for (UMLClass cl : diagram.getClassesList()) {
                    //System.out.println("UMLClass: " + cl.getName());
                    // Creating GUI
                    ClassBox rectangle = new ClassBox(cl);
                    draggable(rectangle);
                    connectable(rectangle);
                    rectangle.getClassTitle().textProperty().addListener((observable, oldValue, newValue) -> {
                        execute(new EditName(rectangle,newValue));
                    });
                    seznam.add(rectangle);
                    anchorPane.getChildren().add(rectangle);
                    rectangle.toFront();
                    rectangle.relocate(cl.getX(), cl.getY());
                    //x += 200;
                    //y += 10;
                }
                for (Connection conn : fileHandler.parseConnections(seznam)){
                    anchorPane.getChildren().addAll(conn,conn.getArrowHead());
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

    /**
     * Funkce uloží třídní a sekvenční diagramy do souboru
     * @author Thanh Q. Tran     xtrant02
     * @author      Lukáš Fuis      xfuisl00
     */
    @FXML
    private void saveToFile() {
        FileChooser fc = new FileChooser();
        File file = fc.showSaveDialog(new Stage());
        if(file != null) {
            JSONObject json = new JSONObject();
            JSONArray classes = new JSONArray();
            for (ClassBox cl : seznam) {
                JSONObject tmp = new JSONObject();
                System.out.println("HERE");
                tmp.put("name", cl.getClassName());
                tmp.put("pos_x", cl.getLayoutX());
                tmp.put("pos_y", cl.getLayoutY());
                List<UMLAttribute> attributes = cl.getClassAttributes();
                JSONArray jsonAttributeArray = new JSONArray();
                for (UMLAttribute attr : attributes) {
                    jsonAttributeArray.put(attr.getName());
                }
                tmp.put("attributes", jsonAttributeArray);
                classes.put(tmp);
            }
            json.put("classes", classes);
            JSONArray sequence = new JSONArray();
            if(sequenceDiagrams != null) {
                for (SequenceDiagram sdiagram:sequenceDiagrams){
                    JSONObject tmp = new JSONObject();
                    tmp.put("name", sdiagram.getName());
                    JSONArray objects = new JSONArray();
                    for (UMLClass cl:sdiagram.getClasses()){
                        JSONObject tridy = new JSONObject();
                        tridy.put("name",cl.getName());
                        tridy.put("start", cl.getActiveFrom());
                        tridy.put("end", cl.getActiveTo());
                        objects.put(tridy);
                    }
                    tmp.put("objects", objects);
                    JSONArray operations = new JSONArray();
                    for(UMLConnection con:sdiagram.getConnections()){
                        JSONObject operace = new JSONObject();
                        operace.put("from", con.getFrom());
                        operace.put("to", con.getTo());
                        operace.put("type", con.getType());
                        operace.put("name", con.getName());
                        operations.put(operace);
                    }
                    tmp.put("operations",operations);
                    sequence.put(tmp);
                }
                json.put("sequence", sequence);
            }

            JSONArray jsonConnections = new JSONArray();
            for (Connection conn : connections) {
                String arrowType = conn.getArrowType();
                String connectionString = new String("line");
                JSONObject tmp = new JSONObject();
                tmp.put("start", conn.getStart().getClassName());
                tmp.put("end", conn.getEnd().getClassName());
                tmp.put("type", arrowType);
                jsonConnections.put(tmp);
            }
            json.put("connections", jsonConnections);
            String toFileString = json.toString(4);
            try {
                PrintWriter printWriter = new PrintWriter(file);
                printWriter.write(toFileString);
                printWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();;
            }
        }
    }

    /**
     * Funkce přepne z diagramu tříd na sekvenční diagram
     * @author      Lukáš Fuis      xfuisl00
     */
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

    /**
     * Funkce přepne ze sekvenčního diagramu na diagram tříd
     * @author      Lukáš Fuis      xfuisl00
     */
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

    /**
     * Třída reprezentující výsledek vrácený při přidávání objektu v sekvenčním diagramu
     * @author Thanh Q. Tran     xtrant02
     * @version 1.0     *
     */
    private static class Results {
        String objectName;
        String resultClass;
        public Results(String name, String className) {
            this.objectName = name;
            this.resultClass = className;
        }
    }

    /**
     * Funkce přidávající třídu v sekvenčním diagramu
     * @author Thanh Q. Tran     xtrant02
     */
    public void sequenceAddClass() {
        ArrayList<String> dialogData = new ArrayList<>();
        dialogData.add("User");
        for(ClassBox box : seznam) {
            String className = box.getClassName();
            dialogData.add(className);
        }
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("Přidat objekt");
        dialog.setHeaderText("Zvolte třídu a jméno objektu (jméno je nepovinné)");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField objectText = new TextField("o1");
        ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableList(dialogData));
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, objectText, comboBox));
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(objectText.getText(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResults = dialog.showAndWait();
        optionalResults.ifPresent((Results results) -> {
            UMLClass newClass = sequenceDiagrams.get(currentPane).createClass(results.resultClass);
            setSequencePanes();
        });
    }

    /**
     * Třída reprezentující výsledek vrácený při vytváření spojení v sekvenčním diagramu
     * @author Thanh Q. Tran     xtrant02
     * @version 1.0     *
     */
    private static class ResultsActivity {
        String methodName;
        int fromHorizontal;
        int toHorizontal;
        int fromVertical;
        int toVertical;
        public ResultsActivity(String methodName, int fromHorizontal, int toHorizontal, int fromVertical, int toVertical) {
            this.methodName = methodName;
            this.fromHorizontal = fromHorizontal;
            this.toHorizontal = toHorizontal;
            this.fromVertical = fromVertical;
            this.toVertical = toVertical;
        }
    }

    /**
     * Funkce přidávající aktivitu a spojení v sekvenčním diagramu
     * @author Thanh Q. Tran     xtrant02
     * @author      Lukáš Fuis      xfuisl00
     */
    public void sequenceAddActivity() {
        ArrayList<String> dialogData = new ArrayList<>();
        dialogData.add("<<create>>");
        Dialog<ResultsActivity> dialog = new Dialog<>();
        dialog.setTitle("Přidat aktivitu");
        dialog.setHeaderText("Zvolte Aktivitu a rozsah");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField objectText = new TextField("method()");
        Spinner<Integer> spinnerFromVertical = new Spinner<>(1, 100, 1);
        Spinner<Integer> spinnerToVertical= new Spinner<>(1, 100, 1);
        final Separator dblSeparator = new Separator(Orientation.HORIZONTAL);
        Spinner<Integer> spinnerFromHorizontal = new Spinner<>(1, 100, 1);
        Spinner<Integer> spinnerToHorizontal = new Spinner<>(1, 100, 1);
        dialogPane.setContent(new VBox(8, objectText, new Text("Výška od - do"),
                spinnerFromVertical, spinnerToVertical,
                dblSeparator, new Text("Šířka od - do"),
                spinnerFromHorizontal, spinnerToHorizontal));
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new ResultsActivity(objectText.getText(), spinnerFromHorizontal.getValue(), spinnerToHorizontal.getValue(),
                        spinnerFromVertical.getValue(), spinnerToVertical.getValue());
            }
            return null;
        });
        Optional<ResultsActivity> optionalResults = dialog.showAndWait();
        optionalResults.ifPresent((ResultsActivity results) -> {
            System.out.println("Sirka = " + results.fromHorizontal + "-" + results.toHorizontal + ", Vyska = " + results.fromVertical + "-" + results.toVertical);
            // TODO nechápu proč není schopnej najít prvek na indexu 0 když tam ten prvek je (řádek 694,700,706,708)
            SequenceDiagram sdiagram = sequenceDiagrams.get(currentPane);
            String fromString;
            String toString;
            UMLClass from = null;
            UMLClass to = null;
            if(results.fromHorizontal == 1) {
                fromString = "actor";
                System.out.println(sequenceClasses.get(currentPane));
                to = sequenceClasses.get(currentPane).get(results.toHorizontal-2); //-2 protože 1 je actor a má to být index (indexováno je od 0)
                toString = to.getName();
            }
            else if(results.toHorizontal == 1) {
                System.out.println(sequenceClasses.get(currentPane));
                from = sequenceClasses.get(currentPane).get(results.fromHorizontal);
                fromString = from.getName();
                toString = "actor";
            }
            else {
                System.out.println(sequenceClasses.get(currentPane));
                from = sequenceClasses.get(currentPane).get(results.fromHorizontal-2);
                fromString = from.getName();
                to = sequenceClasses.get(currentPane).get(results.toHorizontal-2);
                toString = to.getName();
            }
            // TODO: Co s těmi listy?? Vím že určují výšku, to je vše.
            ArrayList<Integer> fromList;
            ArrayList<Integer> toList;
            if (from != null) {
                if(!isInconsistentClass(fromString)){
                    System.out.println("Dostanu se na radek 714");
                    fromList = from.getActiveFrom();
                    fromList.add(results.fromVertical);
                }
            }
            if (to != null) {
                if(!isInconsistentClass(toString)) {
                    toList = to.getActiveTo();
                    toList.add(results.toVertical);
                }
            }
            sdiagram.createConnection(results.methodName, fromString, toString, 1);
            sdiagram.createConnection("return", toString, fromString, 2);
            setSequencePanes();
        });
    }


    /**
     * Funkce na aktualizaci sekvenčního diagramu
     * @author      Lukáš Fuis      xfuisl00
     */
    public void setSequencePanes(){
        int i = 0;
        //System.out.println("setSequencePanes: Updating sequence diagram");
        if(sequenceDiagrams == null) {
            sequenceDiagrams = new ArrayList<>();
            //System.out.println("sequenceDiagrams is empty " + sequenceDiagrams.isEmpty());
            sequenceDiagrams.add(new SequenceDiagram("name", diagram));
        }
        for (SequenceDiagram diagram : sequenceDiagrams) {
            sequencePanes.clear();
            numberOfObjects.clear();
            int x = 50;
            int max = 0;
            int min = 1000;
            int j = 1;
            AnchorPane pane = new AnchorPane();
            Label nameDiagram = new Label(diagram.getName());
            nameDiagram.setId("nameOfDiagram");
            nameDiagram.setLayoutX(238);
            nameDiagram.setLayoutY(14);
            nameDiagram.setFont(new Font(26));
            //pane.getChildren().add(nameDiagram);
            List<UMLClass> classes = diagram.getClasses();
            sequenceClasses.add(classes);
            currentPane = 0;
            List<Rectangle> rectangles = new ArrayList<>();
            int numberObjects = 0;
            for (UMLClass cl : classes) {
                numberObjects++;
                pane.getChildren().addAll(createObjects(cl.getName(),50+numberObjects*100));
                //System.out.println(cl.getName() + "number of objects: " + numberObjects);
                try{
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
                }
                catch(Exception e){
                    System.out.println(cl.getName() + " nemá aktivity");
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
                String name = con.getName();
                //System.out.println("Operation("+from+", "+to+", "+con.getType()+", "+k+", "+name+", " +con.getTo());
                Operation operation = new Operation(from,to,con.getType(),k,name,isInconsistentMethod(con.getTo(),name, con.getType()));
                //System.out.println("radek 626");
                k++;
                pane.getChildren().addAll(operation, operation.getName(), operation.getUp(), operation.getDown());
                operation.toBack();
            }
            pane.getChildren().addAll(drawActor());
            pane.setId(Integer.toString(i));
            //pravitko
            final int width = 100;
            final int height = 60;
            Line horizontalRuler = new Line(10, 10, 1920, 10);
            Line verticalRuler = new Line(10, 10, 10, 1920);
            for (int i2 = 0; i2*width+70 < 1920; i2++)
            {
                Text number = new Text(66 + i2*width, 35, Integer.toString(i2+1));
                pane.getChildren().add(number);
                Line offsetLine = new Line(70 + i2*width, 10, 70 + i2*width, 20);
                pane.getChildren().add(offsetLine);
                number = new Text(35, 135 + i2*height, Integer.toString(i2+1));
                pane.getChildren().add(number);
                offsetLine = new Line(10, 135 + i2*height, 20, 135 + i2*height);
                pane.getChildren().add(offsetLine);
            }
            pane.getChildren().addAll(horizontalRuler, verticalRuler);
            sequencePanes.add(pane);
            i++;
            currentPane++;
        }
        currentPane = 0;
        nameOfDiagram.setText("");
        sequenceAnchorPane.getChildren().clear();
        sequenceAnchorPane.getChildren().addAll(sequencePanes.get(currentPane).getChildrenUnmodifiable());
        //showButton.setDisable(true);
    }

    /**
     * Funkce přepínající mezi sekvenčními diagramy dopředu
     * @author      Lukáš Fuis      xfuisl00
     */
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

    /**
     * Funkce přepínající mezi sekvenčními diagramy dozadu
     * @author      Lukáš Fuis      xfuisl00
     */
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


    /**
     * Funkce na vytvoření listu Node znázorňující actora
     * @author      Lukáš Fuis      xfuisl00
     * @return List instancí třídy Node obsahující nody znázorňující actora
     */
    public List<Node> drawActor(){
        System.out.println("drawActor()");
        List<Node> list = new ArrayList<>();
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

    /**
     * Funkce na vytvoření listu Node znázorňující objekt v sekvenčním diagramu
     * @author      Lukáš Fuis      xfuisl00
     * @param name Jméno třídy vytvářeného objektu
     * @param x souřadnice na ose X kam se má objekt přidat
     * @return List instancí třídy Node obsahující objekt
     */
    public List<Node> createObjects(String name, int x){
        List<Node> list = new ArrayList<Node>();
        Text actor = new Text(name);
        if(isInconsistentClass(name)) {
            actor.setStroke(Color.RED);
        }
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

    /**
     * Funkce na vytvoření listu Node znázorňující čas v sekvenčním diagramu
     * @author      Lukáš Fuis      xfuisl00
     * @param max Hodnota znázorňující kolik akcí se v diagramu vykoná
     * @param count Počet objektů, pro které se má osa vytvořit
     * @return List instancí třídy Node obsahující nody znázorňující čas
     */
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


    /**
     * Funkce na vytvoření Node typu Rectangle znázorňující aktivitu objektu v sekvenčním diagramu
     * @author      Lukáš Fuis      xfuisl00
     * @param poradi Informace, ve kterém sloupci se objekt nachází
     * @param from Informace na kterém řádku začíná aktivita objektu
     * @param to Informace na kterém řádku končí aktivita objektu
     * @return Vrací instanci třídy Rectangle znázorňující aktivitu
     */
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

    /**
     * Funkce kontrolující zda je objekt třídy s názvem name v třídním diagramu
     * @author      Lukáš Fuis      xfuisl00
     * @param name Jméno třídy objektu v sekvenčním diagramu
     * @return Vrací zda je třída v sekvenčním diagramu nekonzistentní
     */
    public boolean isInconsistentClass(String name){return diagram.getClassByName(name) == null;}

    /**
     * Funkce kontrolující zda je metoda ve třídě implementována
     * @author      Lukáš Fuis      xfuisl00
     * @param className Jméno třídy objektu, jehož metoda je volána
     * @param methodName Jméno metody, která je volána
     * @param type Typ metody, která je volána
     * @return Vrací zda je metoda v sekvenčním diagramu nekonzistentní
     */
    public boolean isInconsistentMethod(String className, String methodName, int type){
        if(type == 2)
            return  false;
        UMLClass cl = sequenceDiagrams.get(currentPane).getClassByName(className);
        for (UMLAttribute attr:cl.getAttributes()) {
            if(methodName.equals(attr.getName()))
                return false;
        }
        return true;
    }
}