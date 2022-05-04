package ija.project.controller;

import ija.project.controller.ClassBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

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

    private Polygon arrowHead;

    private static class Position {
        double x;
        double y;
    }

    /**
     * Konstruktor třidy Connection
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
        Position tip = getIntersection(this.getStartX(), this.getStartY(), this.end.getHeight(), this.end.getWidth());
        arrowHead = new Polygon();
        arrowHead.getPoints().addAll(
                tip.x, tip.y,
                tip.x+25,tip.y+25,
                tip.x-25,tip.y+25
        );
        this.endXProperty().addListener((observableValue, number, t1) -> {
            System.out.println("Connection is moving, X: " + number + "new X: " + t1);
            drawArrow(this.getStartX(), this.getStartY(), this.end.getHeight(), this.end.getWidth());
        });
        this.endYProperty().addListener((observableValue, number, t1) -> {
            System.out.println("Connection is moving, old Y: " + number + "new Y: " + t1);
            drawArrow(this.getStartX(), this.getStartY(), this.end.getHeight(), this.end.getWidth());
        });
        this.startXProperty().addListener((observableValue, number, t1) -> {
            System.out.println("Connection is moving, X: " + number + "new X: " + t1);
            drawArrow(this.getStartX(), this.getStartY(), this.end.getHeight(), this.end.getWidth());
        });
        this.startYProperty().addListener((observableValue, number, t1) -> {
            System.out.println("Connection is moving, old Y: " + number + "new Y: " + t1);
            drawArrow(this.getStartX(), this.getStartY(), this.end.getHeight(), this.end.getWidth());
        });
    }

    public void drawArrow(double x, double y, double rectHeight, double rectWidth) {
        Position arrowTipPosition = getIntersection(x, y, rectHeight, rectWidth);
        double angle = Math.atan((y - this.getEndY()) / (x - this.getEndX()));
        System.out.println("Angle" + Math.tan(angle) + "Arrow position: x=" + arrowTipPosition.x + " y=" + arrowTipPosition.y);
        double x2 = arrowTipPosition.x + Math.cos(angle + Math.toRadians(20)) * 25;
        double y2 = arrowTipPosition.y + Math.sin(angle + Math.toRadians(20)) * 25;
        double x3 = arrowTipPosition.x + Math.cos(angle - Math.toRadians(20)) * 25;
        double y3 = arrowTipPosition.y + Math.sin(angle - Math.toRadians(20)) * 25;
        this.arrowHead.getPoints().setAll(
                arrowTipPosition.x, arrowTipPosition.y,
                x2, y2,
                x3, y3
        );
    }

    public Position getIntersection(double x, double y, double rectHeight, double rectWidth){
        Position pos = new Position();
        Position i = new Position();
        double w = rectWidth/2;
        double h = rectHeight/2;

        double dx = this.getEndX() - x;
        double dy = this.getEndY() - y;

        if (dx == 0 && dy == 0 ) {
            pos.x = dx;
            pos.y = dy;
            return pos;
        }
        pos.x = dx;
        pos.y = dy;
        double tan_phi = h/w;
        double tan_theta = Math.abs(pos.y/pos.x);
        // kvadrant?
        double qx = Math.signum(pos.x);
        double qy = Math.signum(pos.y);

        if (tan_theta > tan_phi) {
            System.out.println("1 tan_theta");
            i.x = x + (h / tan_theta) * qx;
            i.y = y + (h * qy);
        } else {
            System.out.println("2 tan_theta");
            i.x = x + (w * qx);
            i.y = y + (w * tan_theta * qy);
        }
        return i;
    }

    public Polygon getArrowHead() {
        return this.arrowHead;
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