package ija.project.controller;

import ija.project.model.*;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


import org.json.JSONArray;
import org.json.JSONObject;


public class FileHandler {

    private File file;
    //ArrayList<ClassBox> classesList = new ArrayList<ClassBox>();
    ArrayList<Connection> lineList = new ArrayList<Connection>();
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
    public ClassDiagram parseClassDiagram() {
        
        ClassDiagram newDiagram = new ClassDiagram("Diagram " + file.getName());
        try {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject json = new JSONObject(content);
            // Parse classes
            JSONArray classes = json.getJSONArray("classes");
            for(int i = 0; i < classes.length(); i++) {
                String classname = classes.getJSONObject(i).getString("name");
                JSONArray attributes = classes.getJSONObject(i).getJSONArray("attributes");
                UMLClass cl = newDiagram.createClass(classname);
                //Adding attributes
                for(int j = 0; j < attributes.length(); j++) {
                    UMLAttribute attr = new UMLAttribute(attributes.getString(j));
                    cl.addAttribute(attr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("Can't parse JSON on classes");
        }
        return newDiagram;
    }

    public ArrayList<Connection> parseConnections(List<ClassBox> classesList) {
        lineList.clear();
        try {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject json = new JSONObject(content);
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
        } catch (Exception e) {
            System.out.println("Can't parse JSON on connections");
        }
        return lineList;
    }


    public List<SequenceDiagram> parseSequence(ClassDiagram classDiagram) {

        try {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject json = new JSONObject(content);
            // Parse sequence
            JSONArray sequence = json.getJSONArray("sequence");
            List<SequenceDiagram> diagrams = new ArrayList<SequenceDiagram>();
            for (int i = 0; i < sequence.length(); i++) {
                String diagramName = sequence.getJSONObject(i).getString("name");
                SequenceDiagram diagram = new SequenceDiagram(diagramName, classDiagram);
                JSONArray objects = sequence.getJSONObject(i).getJSONArray("objects");
                for (int j = 0; j < objects.length(); j++) {
                    JSONObject currClass = objects.getJSONObject(j);
                    JSONArray currStart = currClass.getJSONArray("start");
                    JSONArray currEnd = currClass.getJSONArray("end");
                    if (currStart.length() != currEnd.length())
                        break;
                    String classname = currClass.getString("name");
                    UMLClass cl = diagram.createClass(classname);
                    ArrayList<Integer> start = new ArrayList<Integer>();
                    ArrayList<Integer> end = new ArrayList<Integer>();
                    for (int k = 0; k < currStart.length(); k++) {
                        start.add(currStart.getInt(k));
                        end.add(currEnd.getInt(k));
                    }
                    cl.setActiveFrom(start);
                    cl.setActiveTo(end);
                }
                JSONArray operace = sequence.getJSONObject(i).getJSONArray("operations");
                for (int j = 0; j < operace.length(); j++) {
                    JSONObject currOp = operace.getJSONObject(j);
                    String from = currOp.getString("from");
                    String to = currOp.getString("to");
                    int typ = currOp.getInt("type");
                    String name = currOp.getString("name");
                    diagram.createConnection(name, from, to, typ);
                }
                diagrams.add(diagram);
            }
            return diagrams;
        } catch (Exception e) {
            System.out.println("Chyba při načítání seq diagramu " + e.toString() + "\n");
            return null;
        }
    }
}
