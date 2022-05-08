package ija.project.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Třída znázorňující sekvenční diagram
 * @author      Lukáš Fuis  xfuisl00
 * @version     1.0
 */
public class SequenceDiagram extends Element{
    private ClassDiagram diagram;
    private List<UMLClass> classList = new LinkedList<UMLClass>();
    private List<UMLConnection> connectionList = new LinkedList<UMLConnection>();

    /**
     * Vytvoří instanci se zadaným názvem pro daný diagram tříd.
     *
     * @param name Název elementu.
     * @param diagram Diagram tříd
     */
    public SequenceDiagram(String name, ClassDiagram diagram) {
        super(name);
        this.diagram = diagram;
    }

    /**
     * Vytvoří instanci UML třídy a vloží ji do diagramu. Pokud v diagramu již existuje třída stejného názvu, nedělá nic.
     * @param name Název vytvářené třídy
     * @return Vytvořenou třídu
     */
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

    /**
     * Vytvoří instanci UML Connection a vloží ji do diagramu.
     * @param name Název vytvářené spojení
     * @return Vytvořené spojení
     */
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

    /**
     * Funkce vyhledá a vrátí UML třídu podle názvu
     * @param name Název třídy
     * @return Hledanou třídu, pokud nenajde tak vrátí null
     */
    public UMLClass getClassByName(String name) {
        for (UMLClass cl : classList) {
            if (name.equals(cl.getName())) {
                return cl;
            }
        }
        return null;
    }

    /**
     * Funkce vracející hodnotu v jakém sloupci se nachází třída s daným jménem
     * @param name Jméno hledané třídy
     * @return Hodnota sloupce, ve kterém se třída nachází
     */
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

