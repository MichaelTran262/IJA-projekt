package ija.project.model;

public class UMLConnection extends Element{
    private int type;
    private String from;
    private String to;

    public int getType() {
        return type;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    /**
     * Vytvoří instanci se zadaným názvem.
     *
     * @param name Název elementu.
     */
    public UMLConnection(String name, String from, String to, int type) {
        super(name);
        this.from = from;
        this.to = to;
        this.type = type;
    }
}

