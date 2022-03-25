package ija.project.model;

public class UMLAttribute extends  Element {
    private final UMLClassifier type;

    public UMLAttribute(String name, UMLClassifier type) {
        super(name);
        this.type = type;
    }

    public UMLClassifier getType() {
        return this.type;
    }

    public String toString() {
        return name + ":" + type;
    }
}
