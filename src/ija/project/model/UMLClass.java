package ija.project.model;

import java.util.*;

/**
 * Třída (její instance) reprezentuje model třídy z jazyka UML.
 * Rozšiřuje třídu UMLClassifier.
 * Obsahuje seznam atributů a operací (metod).
 * Třída může být abstraktní.
 */
public class UMLClass extends UMLClassifier {
    private boolean isAbstract;
    private final List<UMLAttribute> attributeList;
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
     * Odstraní atribut z modelu UML třídy.
     */
    public void removeAttribute(String name) {
        for (UMLAttribute attr : attributeList) {
            System.out.println("Attr.getName() = " + attr.getName());
            if (attr.getName().equals(name)) {
                attributeList.remove(attr);
                System.out.println("Attribute removed");
                break;
            }
        }
    }

    /**
     * Vrátí pozici v seznamu atributů
     */
    public int getAttrPosition(UMLAttribute attr) {
        return attributeList.indexOf(attr);
    }

    /**
     * Přesune pozici atributu na nově zadanou
     */
    public int moveAttrAtPosition(UMLAttribute attr, int pos) {
        if (attributeList.contains(attr)) {
            attributeList.remove(getAttrPosition(attr));
            attributeList.add(pos, attr);
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * Vrátí list atributů
     */
    public List<UMLAttribute> getAttributes() {
        return attributeList;
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
