package ija.project.model;

import java.util.LinkedList;
import java.util.List;

public class ClassDiagram extends Element{
    private List<UMLClass> classesList;
    private List<UMLClassifier> classifierList;

    public ClassDiagram(String name) {
        super(name);
        classesList = new LinkedList<UMLClass>();
        classifierList = new LinkedList<UMLClassifier>();
    }

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

    public UMLClassifier classifierForName(String name) {
        UMLClassifier found = findClassifier(name);
        if (found == null) {
            found = UMLClassifier.forName(name);
            classifierList.add(found);
        }
        return found;
    }

    public UMLClassifier findClassifier(String name) {
        for (UMLClassifier classifier : classifierList) {
            if (name.equals(classifier.getName())) {
                //System.out.println(classifier.getName());
                return classifier;
            }
        }
        return null;
    }
}
