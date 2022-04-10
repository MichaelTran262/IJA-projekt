package ija.project.model;

public class UMLAttribute extends  Element {
    //private final UMLClassifier type;
    private final String type;

    public UMLAttribute(String name) {
        super(name);
        this.type = "";
    }
    public UMLAttribute(String name, String type) {
        super(name);
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public String toString() {
        if (type != "") {
            return name + ":<" + type + ">";
        }
        return name;
    }
}
