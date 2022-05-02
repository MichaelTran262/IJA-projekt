package ija.project.controller;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Arrow extends Polygon {

    private Connection conn;

    private ClassBox box;

    private static class Position {
        double x;
        double y;
    }
    /**
     * Konstruktor třidy Connection
     * @param start instance Classbox, odkud vazba začíná
     * @param end instance Classbox, kde vazba končí
     */
    public Arrow(Connection line, ClassBox end){
        super();
        this.box = end;
        this.conn = line;
        drawArrow(line.getStartX(), line.getStartY(), box.getHeight(), box.getWidth());
    }

    public void drawArrow(double x, double y, double rectHeight, double rectWidth) {
        Position arrowTipPosition = getIntersection(x, y, rectHeight, rectWidth);
        System.out.println("Arrow position: x=" + arrowTipPosition.x + " y=" + arrowTipPosition.y);
    }

    public Position getIntersection(double x, double y, double rectHeight, double rectWidth){
        Position pos = new Position();
        Position i = new Position();
        double w = rectWidth/2;
        double h = rectHeight/2;

        double dx = conn.getEndX() - x;
        double dy = conn.getEndY() - y;

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
}
