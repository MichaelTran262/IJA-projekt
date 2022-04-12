package ija.project.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Třída reprezentuje diagram tříd.
 */
public class ClassDiagram extends Element{
    private List<UMLClass> classesList;
    private List<UMLClassifier> classifierList;

    /**
     * Konstruktor pro vytvoření instance diagramu. Každý diagram má svůj název.
     * @param name Název diagramu
     */
    public ClassDiagram(String name) {
        super(name);
        classesList = new LinkedList<UMLClass>();
        classifierList = new LinkedList<UMLClassifier>();
    }

    /**
     * Vytvoří instanci UML třídy a vloží ji do diagramu. Pokud v diagramu již existuje třída stejného názvu, nedělá nic.
     * @param name Název vytvářené třídy
     */
    public UMLClass createClass(String name) {
        for (UMLClass cl : classesList) {
            if (name.equals(cl.getName())) {
                return null;
            }
        }
        UMLClass newClass = new UMLClass(name);
        classesList.add(newClass);
        classifierList.add(newClass);
        return newClass;
    }

    /**
     * Vyhledá v diagramu klasifikátor podle názvu.
     * @param name Název klasifikátoru
     * @return Nalezený, příp. vytvořený, klasifikátor.
     */
    public UMLClassifier classifierForName(String name) {
        UMLClassifier found = findClassifier(name);
        if (found == null) {
            found = UMLClassifier.forName(name);
            classifierList.add(found);
        }
        return found;
    }

    /**
     * Vyhledá v diagramu klasifikátor podle názvu.
     * @param name Název diagramu
     * @return Nalezený klasifikátor
     */
    public UMLClassifier findClassifier(String name) {
        for (UMLClassifier classifier : classifierList) {
            if (name.equals(classifier.getName())) {
                //System.out.println(classifier.getName());
                return classifier;
            }
        }
        return null;
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
}
