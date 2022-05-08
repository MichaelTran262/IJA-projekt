package ija.project.controller;

import ija.project.controller.ClassBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;


/**
 *  Třída reprezentující vazby mezi dvěma UML třidami, které jsou dále reprezentovány ve třídě ClassBox.
 *  Je odvozen od třídy Line, tudíž kreslí čáru mezi dvěma ClassBox instancemi
 * @author      Thanh Q. Tran     xtrant02
 * @version     1.0
 */
public class Connection extends Line {
    private ClassBox start;
    private ClassBox end;

    private Polygon arrowHead;

    private String arrowType;

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
        arrowHead = new Polygon();
    }

    /**
     * Konstruktor třidy Connection
     * @param start instance Classbox, odkud vazba začíná
     * @param end instance Classbox, kde vazba končí
     * @param type informce o typu spojení, které se má vytvořit
     */
    public Connection(ClassBox start, ClassBox end, String type){
        super();
        this.startXProperty().bind(start.layoutXProperty().add(start.widthProperty().divide(2)));
        this.startYProperty().bind(start.layoutYProperty().add(start.heightProperty().divide(2)));
        this.endXProperty().bind(end.layoutXProperty().add(end.widthProperty().divide(2)));
        this.endYProperty().bind(end.layoutYProperty().add(end.heightProperty().divide(2)));
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(2);
        this.start = start;
        this.end = end;
        this.arrowType = type;
        arrowHead = new Polygon();
        createArrowHead(arrowType);
    }

    /**
     * Funkce vytvářející Node znázorňující specifický typ spojení
     * @param x Hodnota souřadnice na ose X
     * @param y Hodnota souřadnice na ose Y
     * @param rectHeight Výška obdélníku
     * @param rectWidth Šířka obdélníku
     */
    public void drawArrow(double x, double y, double rectHeight, double rectWidth) {
        Position arrowTipPosition = getIntersection(x, y, rectHeight, rectWidth);
        double theta = Math.atan2((this.getEndY() - y),(this.getEndX() - x));
        //System.out.println("Angle" + Math.tan(theta) + "Arrow position: x=" + arrowTipPosition.x + " y=" + arrowTipPosition.y);
        double x2, y2, x3, y3, x4, y4;
        x2 = arrowTipPosition.x + Math.cos(theta + Math.toRadians(20)) * 20;
        y2 = arrowTipPosition.y + Math.sin(theta + Math.toRadians(20)) * 20;
        x3 = arrowTipPosition.x + Math.cos(theta - Math.toRadians(20)) * 20;
        y3 = arrowTipPosition.y + Math.sin(theta - Math.toRadians(20)) * 20;
        //System.out.println("x1 = " + arrowTipPosition.x + ", y1 = " + arrowTipPosition.y + ", x2 = " + x2 + ", y2 = " + y2 + ", x3 = " + x3 + ", y3 = " + y3);
        x4 = arrowTipPosition.x + (x2-arrowTipPosition.x)+(x3-arrowTipPosition.x);
        y4 = arrowTipPosition.y + (y2-arrowTipPosition.y)+(y3-arrowTipPosition.y);
        //System.out.println("x4 = " + x4 + ", y4 = " + arrowTipPosition.y);
        switch (arrowType) {
            case "dependency":
                this.arrowHead.getPoints().setAll(
                        x2, y2,
                        arrowTipPosition.x, arrowTipPosition.y,
                        x3, y3
                );
                this.arrowHead.setStroke(Color.BLACK);
                break;
            case "composition":
                this.arrowHead.getPoints().setAll(
                        arrowTipPosition.x, arrowTipPosition.y,
                        x2, y2,
                        x4, y4,
                        x3, y3
                );
                this.arrowHead.setStroke(Color.BLACK);
                break;
            case "inheritance":
                this.arrowHead.getPoints().setAll(
                        arrowTipPosition.x, arrowTipPosition.y,
                        x2, y2,
                        x3, y3
                );
                this.arrowHead.setFill(Color.WHITE);
                this.arrowHead.setStroke(Color.BLACK);
                break;
            case "aggregation":
                this.arrowHead.getPoints().setAll(
                        arrowTipPosition.x, arrowTipPosition.y,
                        x2, y2,
                        x4, y4,
                        x3, y3
                );
                this.arrowHead.setFill(Color.WHITE);
                this.arrowHead.setStroke(Color.BLACK);
                break;
        }
    }

    /**
     * Funkce získávající místo dotyku, kam je následně vložena šipka
     * @param x Hodnota souřadnice na ose X
     * @param y Hodnota souřadnice na ose Y
     * @param rectHeight Výška obdélníku
     * @param rectWidth Šířka obdélníku
     * @return Instance třídy Position obsahující bod průniku okraje obdélníku
     */
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
            i.x = x + (h / tan_theta) * qx;
            i.y = y + (h * qy);
        } else {
            i.x = x + (w * qx);
            i.y = y + (w * tan_theta * qy);
        }
        return i;
    }

    /**
     * Funkce vytvářející specifický typ šipky
     * @param type Informace o typu šipky
     */
    public void createArrowHead(String type) {
        arrowType = type;
        this.endXProperty().addListener((observableValue, number, t1) -> {
            //System.out.println("Connection is moving, X: " + number + "new X: " + t1);
            drawArrow(this.getStartX(), this.getStartY(), this.end.getHeight(), this.end.getWidth());
        });
        this.endYProperty().addListener((observableValue, number, t1) -> {
            //System.out.println("Connection is moving, old Y: " + number + "new Y: " + t1);
            drawArrow(this.getStartX(), this.getStartY(), this.end.getHeight(), this.end.getWidth());
        });
        this.startXProperty().addListener((observableValue, number, t1) -> {
            //System.out.println("Connection is moving, X: " + number + "new X: " + t1);
            drawArrow(this.getStartX(), this.getStartY(), this.end.getHeight(), this.end.getWidth());
        });
        this.startYProperty().addListener((observableValue, number, t1) -> {
            //System.out.println("Connection is moving, old Y: " + number + "new Y: " + t1);
            drawArrow(this.getStartX(), this.getStartY(), this.end.getHeight(), this.end.getWidth());
        });
    }

    public Polygon getArrowHead() {
        return this.arrowHead;
    }

    /**
     * Funkce vrátí Classbox instanci, která je začátkem vazby instance třídy Connection.
     */
    public ClassBox getStart() {
        return start;
    }

    /**
     * Funkce vrátí Classbox instanci, která je koncem vazby instance třídy Connection.
     */
    public ClassBox getEnd() {
        return end;
    }

    public String getArrowType() {
        return arrowType;
    }
}