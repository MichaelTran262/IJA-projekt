package ija.project.model;

import java.util.ArrayList;
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
        UMLClass newClass = diagram.getClassByName(name);
        if(newClass == null){
            for (UMLClass cl:classList) {
                if(name.equals(cl.getName()))
                    return null;
            }
            newClass = new UMLClass(name);
        }
        classList.add(newClass);
        return newClass;
    }

    public UMLConnection createConnection(String name, String from, String to, int type){
        //System.out.println("createConnection(" + name + ", " + from + ", " + to + ")");
        UMLConnection newConnection;
        UMLClass fromClass;
        UMLClass toClass;
        if(from.equals("actor")) {
            toClass = getClassByName(to);
            newConnection = new UMLConnection(name, from, toClass, type);
        } else if (to.equals("actor")) {
            fromClass = getClassByName(from);
            newConnection = new UMLConnection(name, fromClass, to, type);
        }else {
            fromClass = getClassByName(from);
            toClass = getClassByName(to);
            newConnection = new UMLConnection(name, fromClass, toClass, type);
        }
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

    public int getOrder(String name){
        int i = 1;
        //System.out.println("getOrder, name = " + name);
        for(UMLClass cl : classList){
            //System.out.println("cl.getname = " + cl.getName());
            if(name.equals(cl.getName())){
                return i;
            }
            i++;
        }
        return 0;
    }


    public List<UMLClass> getClasses(){
        return classList;
    }

    public List<UMLConnection> getConnections(){
        return connectionList;
    }

    public UMLClass getClassOnIndex(int index){
        return classList.get(index);
    }
}

