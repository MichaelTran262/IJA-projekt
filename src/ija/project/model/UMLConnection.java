package ija.project.model;

public class UMLConnection extends Element{
    private int type;
    private UMLClass from;
    private UMLClass to;
    /**
     * Vytvoří instanci se zadaným názvem.
     *
     * @param name Název elementu.
     */
    public UMLConnection(String name, UMLClass from, UMLClass to, int type) {
        super(name);
        this.from = from;
        this.to = to;
        this.type = type;
    }
}
