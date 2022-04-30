package ija.project.model;

import java.util.LinkedList;
import java.util.List;

public class SequenceDiagram extends Element{
    private ClassDiagram diagram;
    private List<UMLClass> classList = new LinkedList<UMLClass>();
    private List<UMLConnection> connectionList = new LinkedList<UMLConnection>();

    /**
     * Vytvoří instanci se zadaným názvem.
     *
     * @param name Název elementu.
     */
    public SequenceDiagram(String name, ClassDiagram diagram) {
        super(name);
        this.diagram = diagram;
    }

    public UMLClass createClass(String name){
        if(diagram.getClassByName(name) == null)
            return null;
        for (UMLClass cl:classList) {
            if(name.equals(cl.getName()))
                return null;
        }
        UMLClass newClass = new UMLClass(name);
        classList.add(newClass);
        return newClass;
    }

    public UMLConnection createConnection(String name, String from, String to, int type){
        UMLClass fromCLass = this.getClassByName(from);
        UMLClass toClass = this.getClassByName(to);
        if(fromCLass == null || toClass == null)
            return  null;
        UMLConnection newConnection = new UMLConnection(name, fromCLass,toClass,type);
        connectionList.add(newConnection);
        return newConnection;
    }

    public UMLClass getClassByName(String name) {
        for (UMLClass cl : classList) {
            if (name.equals(cl.getName())) {
                return cl;
            }
        }
        return null;
    }
}
