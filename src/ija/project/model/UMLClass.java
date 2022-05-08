package ija.project.model;

import java.util.*;

/**
 * Třída (její instance) reprezentuje model třídy z jazyka UML.
 * Rozšiřuje třídu UMLClassifier.
 * Obsahuje seznam atributů a operací (metod).
 * Třída může být abstraktní.
 */
public class UMLClass extends Element {
    private boolean isAbstract;
    private final LinkedList<UMLAttribute> attributeList;

    private final LinkedList<UMLOperation> operationList;

    private ArrayList<Integer> activeFrom;
    private ArrayList<Integer> activeTo;

    private double x;

    private double y;

    public ArrayList<Integer> getActiveTo() {
        return activeTo;
    }

    public void setActiveTo(ArrayList<Integer> activeTo) {
        this.activeTo = activeTo;
    }

    public ArrayList<Integer> getActiveFrom() {
        return activeFrom;
    }

    public void setActiveFrom(ArrayList<Integer> activeFrom) {
        this.activeFrom = activeFrom;
    }


    /**
     * Vytvoří instanci reprezentující model třídy z jazyka UML.
     */
    public UMLClass(String name) {
        super(name);
        attributeList = new LinkedList<UMLAttribute>();
        operationList = new LinkedList<UMLOperation>();
    }

    /**
     * Test, zda objekt reprezentuje model abstraktní třídy.
     */
    public boolean isAbstract() {
        return isAbstract;
    }

    /**
     * Změní informaci objektu, zda reprezentuje abstraktní třídu.
     */
    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    /**
     * Vloží atribut do modelu UML třídy.
     */
    public boolean addAttribute(UMLAttribute attr) {
        if (attributeList.contains(attr)) {
            return false;
        } else {
            attributeList.add(attr);
            return true;
        }
    }

    /**
     * Vloží metodu do modelu UML třídy
     */
    public boolean addOperation(UMLOperation op) {
        System.out.println("Added " + op.getName());
        if (operationList.contains(op)) {
            return false;
        } else {
            operationList.add(op);
            return true;
        }
    }

    /**
     * Odstraní atribut z modelu UML třídy.
     */
    public void removeAttribute(String name) {
        for (UMLAttribute attr : attributeList) {
            //System.out.println("Attr.getName() = " + attr.getName());
            if (attr.getName().equals(name)) {
                attributeList.remove(attr);
                System.out.println("Attribute removed");
            }
        }
    }

    public void removeOperation(String name) {
        for(UMLOperation op : operationList) {
            //System.out.println("op.getName() = " + op.getName());
            if (op.getName().equals(name)) {
                operationList.remove(op);
                System.out.println("Method/operation removed");
                break;
            }
        }
    }

    /**
     * Vrátí pozici v seznamu atributů
     * @param attr Jméno atributu jehož pozice má být vrácena
     * @return Index
     */
    public int getAttrPosition(String attr) {
        for(int i = 0; i < attributeList.size(); i++)
            if (attributeList.get(i).getName().equals(attr))
                return i;
        return -1;
    }
    /**
     * Vrátí pozici v seznamu metod
     * @param met Jméno metody jejíž pozice má být vrácena
     * @return Index
     */
    public int getMetPosition(String met) {
        for(int i = 0; i < operationList.size(); i++)
            if (operationList.get(i).getName().equals(met))
                return i;
        return -1;
    }

    /**
     * Vrátí list atributů
     */
    public List<UMLAttribute> getAttributes() {
        return attributeList;
    }

    public List<UMLOperation> getOperations() {
        return operationList;
    }

    public double getX(){
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY(){
        return y;
    }

    public void setY(double y){
        this.y = y;
    }
}
