package ija.project.model;

/**
 * Třída reprezentuje klasifikátor v diagramu.
 * Odvozené třídy reprezentují konkrétní podoby klasifikátoru (třída, rozhraní, atribut, apod.)
 */
public class UMLClassifier extends Element {
    private final boolean isUserDefined;

    public UMLClassifier(String name) {
        super(name);
        this.isUserDefined = true;
    }

    public UMLClassifier(String name, boolean isUserDefined) {
        super(name);
        this.isUserDefined = isUserDefined;
    }

    public static UMLClassifier forName(String name) {
        return new UMLClassifier(name, false);
    }

    public boolean isUserDefined() { return isUserDefined; }

    public String toString() {
        return name+'('+isUserDefined+')';
    }
}
