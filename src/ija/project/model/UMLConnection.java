package ija.project.model;

public class UMLConnection extends Element{
    private int type;
    private UMLClass fromClass;
    private UMLClass toClass;
    private String from;
    private String to;

    public int getType() {
        return type;
    }

    public String getTo() {
        if(to == null)
            return toClass.getName();
        return to;
    }

    public String getFrom() {
        if(from == null)
            return fromClass.getName();
        return from;
    }

    /**
     * Vytvoří instanci se zadaným názvem.
     *
     * @param name Název elementu.
     */
    public UMLConnection(String name, UMLClass from, UMLClass to, int type) {
        super(name);
        this.fromClass = from;
        this.toClass = to;
        this.type = type;
    }
    public UMLConnection(String name, String from, UMLClass to, int type) {
        super(name);
        this.toClass = to;
        this.type = type;
        this.from = from;
    }
    public UMLConnection(String name, UMLClass from, String to, int type) {
        super(name);
        this.fromClass = from;
        this.type = type;
        System.out.println("from.getName() = " + from);
        this.to = to;
    }
}

