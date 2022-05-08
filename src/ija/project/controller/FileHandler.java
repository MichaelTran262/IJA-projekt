package ija.project.controller;

import ija.project.model.*;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
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

/**
 * Třída načítající diagramy ze souboru
 * @author      Lukáš Fuis  xfuisl00
 * @author      Thanh Q. Tran   xtrant02
 * @version     1.0
*/

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

    private static class Position {
        double x;
        double y;
    }

    /**
     * Funkce zparsuje soubor a vrátí ClassDiagram obsahující diagram tříd
     *  @author      Thanh Q. Tran     xtrant02
     * @return Instance třídy ClassDiagram
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
                UMLClass cl = newDiagram.createClass(classname);
                try{
                    int pos_x = classes.getJSONObject(i).getInt("pos_x");
                    int pos_y = classes.getJSONObject(i).getInt("pos_y");
                    //Adding positions if exists
                    cl.setX(pos_x);
                    cl.setY(pos_y);
                } catch (Exception e) {
                    System.out.println("Missing pos_x or pos_y");
                }
                JSONArray attributes = classes.getJSONObject(i).getJSONArray("attributes");
                //Adding attributes
                for(int j = 0; j < attributes.length(); j++) {
                    UMLAttribute attr = new UMLAttribute(attributes.getString(j));
                    cl.addAttribute(attr);
                }
                try {
                    JSONArray methodsArray = classes.getJSONObject(i).getJSONArray("methods");
                    for(int j = 0; j < methodsArray.length(); j++) {
                        UMLOperation op = new UMLOperation(methodsArray.getString(j));
                        cl.addOperation(op);
                    }
                } catch (Exception e) {
                    System.out.println("Chybi metody");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("Can't parse JSON on classes");
        }
        return newDiagram;
    }

    /**
     * Funkce zparsuje spojení a vrátí list Connection obsahující spojení mezi třídami
     *   @author      Thanh Q. Tran     xtrant02
     * @return ArrayList instancí třídy Connection
     */
    public ArrayList<Connection> parseConnections(List<ClassBox> classesList) {
        lineList.clear();
        try {
            String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject json = new JSONObject(content);
            // parse connections
            JSONArray lines = json.getJSONArray("connections");
            for(int i = 0; i < lines.length(); i++) {
                String from = lines.getJSONObject(i).getString("start");
                String to = lines.getJSONObject(i).getString("end");
                String type = lines.getJSONObject(i).getString("type");
                ClassBox start = null, end = null;
                for (ClassBox box : classesList) {
                    if (from.equals(box.getClassName())) {
                        start = box;
                    } else if (to.equals(box.getClassName())){
                        end = box;
                    }
                }
                if(start == null || end == null) {
                    System.out.println("Missing node for connection");
                }
                Connection conn = new Connection(start, end, type);
                start.appendConnection(conn);
                end.appendConnection(conn);
                //conn.toBack();
                lineList.add(conn);
            }
        } catch (Exception e) {
            System.out.println("Can't parse JSON on connections");
        }
        return lineList;
    }

    /**
     * Funkce zparsuje soubor a vrátí list SequenceDiagram obsahující sekvenční diagramy
     * @param classDiagram Diagram tříd pro který se vytváří sekvenční diagram
     * @author      Lukáš Fuis      xfuisl00
     * @return List instancí třídy SequenceDiagram
     */
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
