package ija.project.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Třída reprezentuje diagram tříd.
 */
public class ClassDiagram extends Element{
    private List<UMLClass> classesList;

    /**
     * Konstruktor pro vytvoření instance diagramu. Každý diagram má svůj název.
     * @param name Název diagramu
     */
    public ClassDiagram(String name) {
        super(name);
        classesList = new LinkedList<UMLClass>();
    }

    /**
     * Vytvoří instanci UML třídy a vloží ji do diagramu. Pokud v diagramu již existuje třída stejného názvu, nedělá nic.
     * @param name Název vytvářené třídy
     * @return Vytvořená třída
     */
    public UMLClass createClass(String name) {
        for (UMLClass cl : classesList) {
            if (name.equals(cl.getName())) {
                return null;
            }
        }
        UMLClass newClass = new UMLClass(name);
        classesList.add(newClass);
        return newClass;
    }



    /**
     * Funkce vyhledá a vrátí UML třídu podle názvu
     * @param name Název třídy
     * @return Hledanou třídu, pokud nenajde tak vrátí null
     */
    public UMLClass getClassByName(String name) {
        for (UMLClass cl : classesList) {
            if (name.equals(cl.getName())) {
                return cl;
            }
        }
        return null;
    }

    /**
     * Funkce vrátí seznam svých tříd
     * @return Seznam tříd
     */
    public List<UMLClass> getClassesList() {
        return classesList;
    }

    /**
     * Funkce odstraňující třídu z diagramu
     * @param toRemove Třída, která má být odstraněna
     */
    public void removeClass(UMLClass toRemove){
        classesList.remove(toRemove);
    }

    /**
     * Funkce přidávající existující třídu z diagramu
     * @param toAdd Třída, která má být přidána
     */
    public void addClass(UMLClass toAdd){
        classesList.add(toAdd);
    }
}

