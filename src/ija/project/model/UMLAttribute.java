package ija.project.model;

/**
 * Třída reprezentuje atribut, který má své jméno a typ.
 * Je odvozena (rozšiřuje) od třídy Element.
 * Typ atributu je reprezentován třidou UMLClassifier.
 * Lze použít jako atribut UML třídy nebo argument operace.
 */
public class UMLAttribute extends  Element {
    //private final UMLClassifier type;
    private final String type;

    /**
     * Vytvoří instanci atributu.
     * @param name Název atributu
     */
    public UMLAttribute(String name) {
        super(name);
        this.type = "";
    }

    /**
     * Vytvoří instanci atributu.
     * @param name Název atributu
     * @param type Typ atributu
     */
    public UMLAttribute(String name, String type) {
        super(name);
        this.type = type;
    }

    /**
     * Vrátí typ atributu
     * @return typ atributu
     */
    public String getType() {
        return this.type;
    }

    /**
     * Vrátí atribut převedený na třídu String
     * @return Řetězec s názvem atributu
     */
    public String toString() {
        if (type != "") {
            return name + ":<" + type + ">";
        }
        return name;
    }
}
