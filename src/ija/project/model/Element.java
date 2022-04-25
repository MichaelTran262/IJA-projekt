package ija.project.model;

/**
 * Třída reprezentuje pojmenovaný element (thing), který může být součástí jakékoliv části v diagramu.
 */
public class Element {
    protected String name;

    /**
     * Vytvoří instanci se zadaným názvem.
     * @param name Název elementu.
     */
    public Element(String name) {
        this.name = name;
    }

    /**
     * Vrátí název elementu.
     * @return Název elementu.
     */
    public String getName() {
        return name;
    }

    /**
     * Přejmenuje element.
     * @param newName nové jméno
     */
    public void setName(String newName) {
        name = newName;
    }
}
