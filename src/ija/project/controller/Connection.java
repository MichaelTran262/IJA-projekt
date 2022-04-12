package ija.project.controller;

import ija.project.controller.ClassBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * @author      Thanh Q. Tran     <xtrant02 @ stud.fit.vutbr.cz>
 * @version     0.5
 */

/**
 *  Třída reprezentující vazby mezi dvěma UML třidami, které jsou dále reprezentovány ve třídě ClassBox.
 *  Je odvozen od třídy Line, tudíž kreslí čáru mezi dvěma ClassBox instancemi
 */
public class Connection extends Line {
    private ClassBox start;
    private ClassBox end;

    /**
     * Konstruktor třidy ClassBox, nastavuje vlastnosti GUI komponentů
     * @param start instance Classbox, odkud vazba začíná
     * @param end instance Classbox, kde vazba končí
     */
    public Connection(ClassBox start, ClassBox end){
        super();
        this.startXProperty().bind(start.layoutXProperty().add(start.widthProperty().divide(2)));
        this.startYProperty().bind(start.layoutYProperty().add(start.heightProperty().divide(2)));
        this.endXProperty().bind(end.layoutXProperty().add(end.widthProperty().divide(2)));
        this.endYProperty().bind(end.layoutYProperty().add(end.heightProperty().divide(2)));
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(2);
        this.start = start;
        this.end = end;
    }

    /**
     * Funkce vrátí Classbox instanci, která je začátkem vazby instance třídy Connection.
     * @param start Classbox instanci, která je začátkem vazby
     */
    public ClassBox getStart() {
        return start;
    }

    /**
     * Funkce vrátí Classbox instanci, která je koncem vazby instance třídy Connection.
     * @param start Classbox instanci, která je koncem vazby
     */
    public ClassBox getEnd() {
        return end;
    }
}
