package ija.project.controller;

import ija.project.model.ClassDiagram;
import ija.project.model.UMLAttribute;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;


import ija.project.model.UMLClass;
import org.json.JSONArray;
import org.json.JSONObject;


public class FileHandler {

    private File file;
    ArrayList<ClassBox> classesList = new ArrayList<ClassBox>();
    ArrayList<Line> lineList = new ArrayList<Line>();
    AnchorPane newPane = new AnchorPane();

    public FileHandler(File file) {
        this.file = file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void getFile() {
        System.out.println(file.getAbsolutePath());
    }


    /**
     * Funkce zparsuje soubor a vrátí třídy
     */
    public ObservableList<Node> parseFile() {

        try {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject json = new JSONObject(content);
            // Parse classes
            JSONArray classes = json.getJSONArray("classes");
            for(int i = 0; i < classes.length(); i++) {
                String classname = classes.getJSONObject(i).getString("name");
                JSONArray attributes = classes.getJSONObject(i).getJSONArray("attributes");
                UMLClass cl = new UMLClass(classname);
                //Adding attributes
                ClassBox box = new ClassBox(cl);
                for(int j = 0; j < attributes.length(); j++) {
                    //System.out.println(attributes.getString(j))
                    box.addClassAttribute(attributes.getString(j));
                }
                box.toFront();
                classesList.add(box);
            }
            newPane.getChildren().addAll(classesList);
            // parse connections
            JSONArray lines = json.getJSONArray("connections");
            for(int i = 0; i < lines.length(); i++) {
                String line = lines.getString(i);
                String[] nodes = line.split("--");
                ClassBox start = null, end = null;
                for (ClassBox box : classesList) {
                    if (nodes[0].equals(box.getClassName())) {
                        start = box;
                    } else if (nodes[1].equals(box.getClassName())){
                        end = box;
                    }
                }
                Connection conn = new Connection(start, end);
                start.appendConnection(conn);
                end.appendConnection(conn);
                conn.toBack();
                lineList.add(conn);
            }
            newPane.getChildren().addAll(lineList);
        } catch (Exception e) {
            System.out.println("Error at line 48");
        }
        return newPane.getChildren();
    }

}
